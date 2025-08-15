package com.kafe.infra.service;

import com.kafe.core.dto.*;
import com.kafe.infra.entity.*;
import com.kafe.infra.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class OrderFlowService {

    private final OrderRepository2 orderRepo;
    private final OrderItemRepository2 itemRepo;
    private final PaymentRepository2 payRepo;
    private final CafeTableRepository tableRepo;
    private final ProductRepository productRepo;
    private final ProductCategoryRepository categoryRepo;
    private final StockService stockService;

    // ---------- Sipariş Aç ----------
    @Transactional
    public OrderSummaryResp createOrder(CreateOrderReq req) {
        CafeTableEntity t = tableRepo.findById(req.tableId)
                .orElseThrow(() -> new NoSuchElementException("Table not found: " + req.tableId));
        t.setStatus("OCCUPIED");
        tableRepo.save(t);

        OrderEntity o = OrderEntity.builder()
                .tableId(req.tableId)
                .status("OPEN")
                .openedAt(OffsetDateTime.now())
                .note(req.note)
                .preDiscountTotal(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .subtotalExclVat(BigDecimal.ZERO)
                .vatTotal(BigDecimal.ZERO)
                .grandTotal(BigDecimal.ZERO)
                .build();
        o = orderRepo.save(o);
        return buildSummary(o, itemRepo.findByOrderId(o.getId()));
    }

    // ---------- Ürün Ekle ----------
    @Transactional
    public OrderSummaryResp addItem(Long orderId, AddOrderItemReq req) {
        OrderEntity o = mustOpenOrder(orderId);

        var p = productRepo.findById(req.productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + req.productId));
        var cat = categoryRepo.findById(p.getCategoryId()).orElse(null);
        BigDecimal vatRate = (cat != null && cat.getVatRate()!=null) ? cat.getVatRate() : BigDecimal.ZERO;
        BigDecimal unitPrice = p.getListPrice();

        // Fiyat KDV ayrıştırma:
        BigDecimal net, vatAmtPerUnit;
        if (Boolean.TRUE.equals(p.getPriceIncludesVat())) {
            BigDecimal divisor = BigDecimal.ONE.add(vatRate.divide(new BigDecimal("100"), 6, HALF_UP));
            net = unitPrice.divide(divisor, 2, HALF_UP);
            vatAmtPerUnit = unitPrice.subtract(net);
        } else {
            net = unitPrice;
            vatAmtPerUnit = unitPrice.multiply(vatRate).divide(new BigDecimal("100"), 2, HALF_UP);
        }

        BigDecimal lineTotal = unitPrice.multiply(req.qty).setScale(2, HALF_UP);
        BigDecimal vatAmount = vatAmtPerUnit.multiply(req.qty).setScale(2, HALF_UP);

        OrderItemEntity line = OrderItemEntity.builder()
                .orderId(orderId)
                .productId(req.productId)
                .qty(req.qty)
                .unitPrice(unitPrice)
                .vatRate(vatRate)
                .vatAmount(vatAmount)
                .appliedDiscountAmount(BigDecimal.ZERO)
                .lineTotal(lineTotal)
                .build();
        itemRepo.save(line);

        recalcTotals(o);
        return buildSummary(o, itemRepo.findByOrderId(orderId));
    }

    // ---------- İndirim Uygula (oransal) ----------
    @Transactional
    public OrderSummaryResp applyDiscount(Long orderId, ApplyDiscountReq req) {
        OrderEntity o = mustOpenOrPending(orderId);
        o.setDiscountRate(req.discountRate);
        recalcTotals(o);
        return buildSummary(o, itemRepo.findByOrderId(orderId));
    }

    // ---------- Ödeme Ekranına Geç ----------
    @Transactional
    public OrderSummaryResp goPayment(Long orderId) {
        OrderEntity o = mustOpenOrder(orderId);
        o.setStatus("PENDING_PAYMENT");
        orderRepo.save(o);
        return buildSummary(o, itemRepo.findByOrderId(orderId));
    }

    // ---------- Ödemeyi Tamamla ----------
    @Transactional
    public OrderSummaryResp finalizePayment(Long orderId, FinalizePaymentReq req) {
        OrderEntity o = mustPending(orderId);

        BigDecimal amount = o.getGrandTotal();

        if ("CASH".equalsIgnoreCase(req.paymentType)) {
            // Nakit → direkt CAPTURED + kapanış
            PaymentEntity pay = PaymentEntity.builder()
                    .orderId(orderId).method("CASH").status("CAPTURED")
                    .amount(amount).approvedAt(OffsetDateTime.now()).build();
            payRepo.save(pay);
            closeOrderAndFreeTable(o);
        } else if ("CARD".equalsIgnoreCase(req.paymentType)) {
            if (Boolean.TRUE.equals(req.cardPaid)) {
                PaymentEntity pay = PaymentEntity.builder()
                        .orderId(orderId).method("CARD").status("CAPTURED")
                        .amount(amount).approvedAt(OffsetDateTime.now()).build();
                payRepo.save(pay);
                closeOrderAndFreeTable(o);
            } else {
                // kart seçildi ama ödenmedi
                PaymentEntity pay = PaymentEntity.builder()
                        .orderId(orderId).method("CARD").status("FAILED")
                        .amount(amount).build();
                payRepo.save(pay);
                // Sipariş PENDING_PAYMENT'ta kalır
            }
        } else {
            throw new IllegalArgumentException("paymentType must be CASH or CARD");
        }

        return buildSummary(o, itemRepo.findByOrderId(orderId));
    }

    // ---------- Yardımcılar ----------
    private OrderEntity mustOpenOrder(Long id) {
        var o = orderRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
        if (!"OPEN".equals(o.getStatus())) throw new IllegalStateException("Order not OPEN");
        return o;
    }

    private OrderEntity mustOpenOrPending(Long id) {
        var o = orderRepo.findById(id).orElseThrow();
        if (!List.of("OPEN","PENDING_PAYMENT").contains(o.getStatus()))
            throw new IllegalStateException("Order not OPEN/PENDING");
        return o;
    }

    private OrderEntity mustPending(Long id) {
        var o = orderRepo.findById(id).orElseThrow();
        if (!"PENDING_PAYMENT".equals(o.getStatus())) throw new IllegalStateException("Order not PENDING_PAYMENT");
        return o;
    }

    private void recalcTotals(OrderEntity o) {
        var lines = itemRepo.findByOrderId(o.getId());
        BigDecimal pre = lines.stream().map(OrderItemEntity::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal vat = lines.stream().map(OrderItemEntity::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rate = Optional.ofNullable(o.getDiscountRate()).orElse(BigDecimal.ZERO);
        BigDecimal discAmt = pre.multiply(rate).setScale(2, HALF_UP);
        BigDecimal grand = pre.subtract(discAmt).setScale(2, HALF_UP);

        // Subtotal_excl_vat'ı yaklaşık al: (pre - toplam KDV) üzerinden oransal indirim uygula
        BigDecimal subtotalExcl = pre.subtract(vat).setScale(2, HALF_UP);
        BigDecimal subtotalDisc = subtotalExcl.multiply(rate).setScale(2, HALF_UP);
        BigDecimal subtotalAfter = subtotalExcl.subtract(subtotalDisc).setScale(2, HALF_UP);

        o.setPreDiscountTotal(pre);
        o.setDiscountAmount(discAmt);
        o.setVatTotal(vat); // basit model
        o.setSubtotalExclVat(subtotalAfter);
        o.setGrandTotal(grand);
        orderRepo.save(o);
    }

    private void closeOrderAndFreeTable(OrderEntity o) {
        // stok düşümü (BOM destekli)
        var lines = itemRepo.findByOrderId(o.getId());
        for (var l : lines) {
            stockService.applySaleForOrderItem(l.getProductId(), l.getQty(), o.getId());
        }
        // masa boşalt
        tableRepo.findById(o.getTableId()).ifPresent(t -> {
            t.setStatus("AVAILABLE");
            tableRepo.save(t);
        });
        // siparişi kapat
        o.setStatus("PAID");
        o.setClosedAt(OffsetDateTime.now());
        orderRepo.save(o);
    }

    private OrderSummaryResp buildSummary(OrderEntity order, List<OrderItemEntity> items) {
        OrderSummaryResp resp = new OrderSummaryResp();
        resp.orderId = order.getId();
        resp.tableId = order.getTableId();
        resp.status = order.getStatus();
        resp.preDiscountTotal = order.getPreDiscountTotal();
        resp.discountRate = order.getDiscountRate();
        resp.discountAmount = order.getDiscountAmount();
        resp.subtotalExclVat = order.getSubtotalExclVat();
        resp.vatTotal = order.getVatTotal();
        resp.grandTotal = order.getGrandTotal();
        resp.openedAt = order.getOpenedAt();
        resp.closedAt = order.getClosedAt();
        
        resp.items = items.stream().map(this::buildItemLine).toList();
        return resp;
    }

    private OrderSummaryResp.OrderItemLine buildItemLine(OrderItemEntity item) {
        OrderSummaryResp.OrderItemLine line = new OrderSummaryResp.OrderItemLine();
        line.productId = item.getProductId();
        line.qty = item.getQty();
        line.unitPrice = item.getUnitPrice();
        line.vatRate = item.getVatRate();
        line.lineTotal = item.getLineTotal();
        
        // Product name'i al
        var product = productRepo.findById(item.getProductId()).orElse(null);
        line.name = product != null ? product.getName() : "Unknown Product";
        
        return line;
    }
}
