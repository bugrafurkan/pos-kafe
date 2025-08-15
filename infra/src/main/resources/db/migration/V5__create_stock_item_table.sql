-- Stock item table for tracking current stock levels
CREATE TABLE IF NOT EXISTS stock_item (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    current_qty DECIMAL(14,3) NOT NULL DEFAULT 0.000,
    reorder_level DECIMAL(14,3) NOT NULL DEFAULT 0.000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_stock_item_product_id ON stock_item(product_id);

-- Foreign key constraint
ALTER TABLE stock_item ADD CONSTRAINT fk_stock_item_product 
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;
