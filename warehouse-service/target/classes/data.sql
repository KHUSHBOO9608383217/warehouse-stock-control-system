-- Sample Data for Warehouse Service

INSERT INTO warehouses (warehouse_code, name, location, manager_name, capacity, active, created_at, updated_at)
SELECT 'WH-001', 'Raw Material Warehouse', 'Bangalore, Karnataka', 'Rajesh Kumar', 10000, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM warehouses WHERE warehouse_code = 'WH-001');

INSERT INTO warehouses (warehouse_code, name, location, manager_name, capacity, active, created_at, updated_at)
SELECT 'WH-002', 'Finished Goods Warehouse', 'Bangalore, Karnataka', 'Priya Sharma', 5000, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM warehouses WHERE warehouse_code = 'WH-002');
