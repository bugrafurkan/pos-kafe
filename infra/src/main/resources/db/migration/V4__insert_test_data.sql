-- Insert test categories
INSERT INTO product_category (id, name, vat_rate) VALUES 
(1, 'Beverages', 8.00),
(2, 'Ingredients', 0.00),
(3, 'Food', 8.00)
ON CONFLICT (id) DO NOTHING;

-- Insert test products
INSERT INTO product (id, sku, name, category_id, list_price, unit) VALUES 
(1, 'LATTE', 'Latte', 1, 25.00, 'pcs'),
(2, 'MILK', 'Milk', 2, 15.00, 'lt'),
(3, 'COFFEE', 'Coffee Beans', 2, 120.00, 'kg'),
(4, 'CAPPUCCINO', 'Cappuccino', 1, 22.00, 'pcs'),
(5, 'SUGAR', 'Sugar', 2, 8.00, 'kg')
ON CONFLICT (id) DO NOTHING;
