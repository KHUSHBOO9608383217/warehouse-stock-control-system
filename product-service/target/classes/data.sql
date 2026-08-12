-- =============================================
-- Sample Data for Product Service
-- =============================================
-- These are laptop components used in a manufacturing warehouse.

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'PROC-001', 'Intel Core i5 Processor', 'PROCESSOR', '12th Gen Intel Core i5 processor for laptops', 'PCS', 50, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROC-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'PROC-002', 'Intel Core i7 Processor', 'PROCESSOR', '12th Gen Intel Core i7 processor for laptops', 'PCS', 30, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'PROC-002');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'RAM-001', '8GB DDR4 RAM', 'RAM', '8GB DDR4 3200MHz laptop memory module', 'PCS', 100, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'RAM-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'RAM-002', '16GB DDR4 RAM', 'RAM', '16GB DDR4 3200MHz laptop memory module', 'PCS', 80, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'RAM-002');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'SSD-001', '512GB NVMe SSD', 'SSD', '512GB NVMe M.2 solid state drive', 'PCS', 60, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'SSD-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'SSD-002', '1TB NVMe SSD', 'SSD', '1TB NVMe M.2 solid state drive', 'PCS', 40, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'SSD-002');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'MB-001', 'Laptop Motherboard', 'MOTHERBOARD', 'Standard laptop motherboard with Intel chipset', 'PCS', 30, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'MB-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'DISP-001', 'Laptop Display 15.6 inch', 'DISPLAY', '15.6 inch FHD IPS laptop display panel', 'PCS', 40, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'DISP-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'BAT-001', 'Laptop Battery 56Wh', 'BATTERY', '56Wh lithium-ion laptop battery pack', 'PCS', 50, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'BAT-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'KB-001', 'Laptop Keyboard', 'KEYBOARD', 'Standard US layout laptop keyboard module', 'PCS', 60, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'KB-001');

INSERT INTO products (product_code, name, category, description, unit, minimum_stock_level, active, created_at, updated_at)
SELECT 'CHG-001', 'Laptop Charger 65W', 'CHARGER', '65W USB-C laptop charger adapter', 'PCS', 70, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code = 'CHG-001');
