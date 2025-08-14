-- Product table
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    category_id BIGINT NOT NULL,
    list_price DECIMAL(12,2) NOT NULL,
    price_includes_vat BOOLEAN NOT NULL DEFAULT TRUE,
    barcode VARCHAR(64) UNIQUE,
    unit VARCHAR(16),
    cost_price DECIMAL(12,2),
    min_price DECIMAL(12,2),
    max_price DECIMAL(12,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Indexes
CREATE INDEX idx_product_deleted_at ON product(deleted_at);
CREATE INDEX idx_product_active ON product(active);
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_sku ON product(sku);
CREATE INDEX idx_product_barcode ON product(barcode);
