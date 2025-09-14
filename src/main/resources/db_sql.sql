-- Create indexes for better performance
CREATE INDEX idx_stock_allocation_warehouse ON stock_allocations(warehouse_id);
CREATE INDEX idx_stock_allocation_variant ON stock_allocations(product_variant_id);
CREATE INDEX idx_stock_allocation_warehouse_variant ON stock_allocations(warehouse_id, product_variant_id);

CREATE INDEX idx_product_variant_product ON product_variants(product_id);
CREATE INDEX idx_product_variant_sku ON product_variants(sku);

CREATE INDEX idx_warehouse_code ON warehouses(code);