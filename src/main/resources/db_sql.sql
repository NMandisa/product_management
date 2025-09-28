-- Create indexes for better performance
CREATE INDEX idx_stock_allocation_warehouse ON stock_allocations(warehouse_id);
CREATE INDEX idx_stock_allocation_variant ON stock_allocations(product_variant_id);
CREATE INDEX idx_stock_allocation_warehouse_variant ON stock_allocations(warehouse_id, product_variant_id);

CREATE INDEX idx_product_variant_product ON product_variants(product_id);
CREATE INDEX idx_product_variant_sku ON product_variants(sku);

CREATE INDEX idx_warehouse_code ON warehouses(code);

SELECT * FROM payment_provider;
SELECT * FROM provider_region;
SELECT * FROM provider_currency;
SELECT * FROM provider_capability;
SELECT * FROM region_provider_mapping;
SELECT * FROM routing_priority_rule;
SELECT * FROM routing_preferred_provider;
SELECT * FROM routing_fallback;
SELECT * FROM settlement_rules;

SELECT * FROM compliance_region;
SELECT * FROM language_region;
SELECT * FROM language;
SELECT * FROM localization_config;
SELECT * FROM regional_fallback_language;
SELECT * FROM regional_settings;
SELECT * FROM supported_calendar;
SELECT * FROM supported_number_system;
SELECT * FROM time_format_config;
SELECT * FROM accessibility_settings;

SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = DATABASE();

-- Check currency configuration
SELECT * FROM currency_config;

-- Check currencies
SELECT * FROM currency;

-- Check currency formatting
SELECT c.code, c.name, cf.thousands_separator, cf.decimal_separator
FROM currency c
JOIN currency_formatting cf ON c.currency_code = cf.currency_code;

-- Check FX rules and allowed crosses
SELECT c.code, cr.default_rate_source, cr.max_daily_limit, acc.allowed_currency
FROM currency c
JOIN currency_fx_rules cr ON c.currency_code = cr.currency_code
LEFT JOIN allowed_currency_cross acc ON cr.id = acc.fx_rules_id;

-- Check regional defaults
SELECT country_code, currency_code FROM regional_default ORDER BY country_code;

-- Check FX configuration
SELECT primary_source, markup, cache_timeout_minutes FROM fx_configuration;

-- Check backup sources
SELECT source_name FROM fx_backup_sources;

-- Check advanced features
SELECT real_time_conversion, historical_rates, multi_currency_wallets FROM advanced_features;