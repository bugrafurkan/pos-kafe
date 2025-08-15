-- Add product_name column to order_item table
ALTER TABLE order_item ADD COLUMN product_name VARCHAR(200) NOT NULL DEFAULT '';

-- Update existing records with product names from product table
UPDATE order_item 
SET product_name = p.name 
FROM product p 
WHERE order_item.product_id = p.id;

-- Make the column NOT NULL after updating existing data
ALTER TABLE order_item ALTER COLUMN product_name SET NOT NULL;
