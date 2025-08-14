CREATE TABLE user_account (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(64) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  roles JSONB NOT NULL DEFAULT '[]'::jsonb,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE product_category (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL UNIQUE,
  vat_rate NUMERIC(5,2) NOT NULL DEFAULT 0
);

CREATE TABLE product (
  id BIGSERIAL PRIMARY KEY,
  sku VARCHAR(64) UNIQUE NOT NULL,
  name VARCHAR(200) NOT NULL,
  category_id BIGINT NOT NULL REFERENCES product_category(id),
  list_price NUMERIC(12,2) NOT NULL DEFAULT 0,
  price_includes_vat BOOLEAN NOT NULL DEFAULT true,
  barcode VARCHAR(64) UNIQUE,
  unit VARCHAR(16) DEFAULT 'pcs',
  cost_price NUMERIC(12,2),
  min_price NUMERIC(12,2),
  max_price NUMERIC(12,2),
  active BOOLEAN NOT NULL DEFAULT true,
  deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_product_category_active ON product(category_id, active);

CREATE TABLE stock_item (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL UNIQUE REFERENCES product(id),
  current_qty NUMERIC(14,3) NOT NULL DEFAULT 0,
  reorder_level NUMERIC(14,3) NOT NULL DEFAULT 0
);

CREATE TABLE stock_movement (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES product(id),
  qty_delta NUMERIC(14,3) NOT NULL,
  reason VARCHAR(32) NOT NULL, -- SALE, RETURN, MANUAL_IN, MANUAL_OUT, CORRECTION
  ref_type VARCHAR(32),
  ref_id BIGINT,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  note VARCHAR(255)
);
CREATE INDEX idx_stock_movement_prod_time ON stock_movement(product_id, occurred_at);

CREATE TABLE cafe_table (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(32) UNIQUE NOT NULL,
  name VARCHAR(120),
  capacity INT,
  status VARCHAR(24) NOT NULL DEFAULT 'AVAILABLE' -- AVAILABLE, OCCUPIED, CLOSED
);

CREATE TABLE "order" (
  id BIGSERIAL PRIMARY KEY,
  table_id BIGINT REFERENCES cafe_table(id),
  status VARCHAR(24) NOT NULL DEFAULT 'OPEN', -- OPEN, PENDING_PAYMENT, PAID, VOID
  pre_discount_total NUMERIC(12,2) NOT NULL DEFAULT 0,
  discount_rate NUMERIC(6,3),
  discount_amount NUMERIC(12,2),
  subtotal_excl_vat NUMERIC(12,2),
  vat_total NUMERIC(12,2),
  grand_total NUMERIC(12,2),
  opened_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  closed_at TIMESTAMPTZ,
  note VARCHAR(255)
);
CREATE INDEX idx_order_closed_at ON "order"(closed_at);

CREATE TABLE order_item (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
  product_id BIGINT NOT NULL REFERENCES product(id),
  qty NUMERIC(12,3) NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  line_total NUMERIC(12,2) NOT NULL,
  vat_rate NUMERIC(5,2) NOT NULL DEFAULT 0,
  vat_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  applied_discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0
);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_product ON order_item(product_id);

CREATE TABLE payment (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT NOT NULL REFERENCES "order"(id),
  method VARCHAR(16) NOT NULL, -- CASH, CARD
  amount NUMERIC(12,2) NOT NULL,
  status VARCHAR(24) NOT NULL, -- INITIATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED
  pos_txn_id VARCHAR(64),
  approved_at TIMESTAMPTZ,
  details_json JSONB DEFAULT '{}'::jsonb
);
CREATE INDEX idx_payment_order_status ON payment(order_id, status);

CREATE TABLE day_closure (
  id BIGSERIAL PRIMARY KEY,
  business_date DATE NOT NULL UNIQUE,
  cash_total NUMERIC(14,2) NOT NULL DEFAULT 0,
  card_total NUMERIC(14,2) NOT NULL DEFAULT 0,
  orders_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE product_price_history (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES product(id),
  old_price NUMERIC(12,2),
  new_price NUMERIC(12,2) NOT NULL,
  changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  changed_by BIGINT REFERENCES user_account(id),
  reason VARCHAR(24) -- MANUAL, PROMO, CORRECTION
);
CREATE INDEX idx_price_history_prod_time ON product_price_history(product_id, changed_at);

CREATE TABLE product_modifier_group (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  required BOOLEAN NOT NULL DEFAULT false,
  max_select INT NOT NULL DEFAULT 1
);

CREATE TABLE product_modifier_option (
  id BIGSERIAL PRIMARY KEY,
  group_id BIGINT NOT NULL REFERENCES product_modifier_group(id) ON DELETE CASCADE,
  name VARCHAR(120) NOT NULL,
  price_delta NUMERIC(12,2) NOT NULL DEFAULT 0,
  sku_suffix VARCHAR(32)
);

CREATE TABLE product_modifier_link (
  product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
  group_id  BIGINT NOT NULL REFERENCES product_modifier_group(id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, group_id)
);

CREATE TABLE product_bom (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
  component_product_id BIGINT NOT NULL REFERENCES product(id),
  component_qty NUMERIC(14,3) NOT NULL
);
CREATE INDEX idx_bom_product ON product_bom(product_id);
CREATE INDEX idx_bom_component ON product_bom(component_product_id);
