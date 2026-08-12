-- Sample Data for Supplier Service

INSERT INTO suppliers (supplier_code, name, contact_person, email, phone, address, active, created_at, updated_at)
SELECT 'SUP-001', 'TechParts India Pvt Ltd', 'Amit Verma', 'sales@techparts.in', '+91-9876543210', '45 Electronic City, Bangalore, Karnataka 560100', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_code = 'SUP-001');

INSERT INTO suppliers (supplier_code, name, contact_person, email, phone, address, active, created_at, updated_at)
SELECT 'SUP-002', 'Global Components Ltd', 'Sarah Chen', 'info@globalcomp.com', '+91-8765432109', '12 IT Park, Hyderabad, Telangana 500081', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_code = 'SUP-002');

INSERT INTO suppliers (supplier_code, name, contact_person, email, phone, address, active, created_at, updated_at)
SELECT 'SUP-003', 'MicroChip Supplies', 'Vikram Patel', 'orders@microchipsupplies.in', '+91-7654321098', '78 MIDC Industrial Area, Pune, Maharashtra 411057', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_code = 'SUP-003');

INSERT INTO suppliers (supplier_code, name, contact_person, email, phone, address, active, created_at, updated_at)
SELECT 'SUP-004', 'DigiSource Electronics', 'Neha Reddy', 'procurement@digisource.co.in', '+91-6543210987', '23 Whitefield Tech Park, Bangalore, Karnataka 560066', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE supplier_code = 'SUP-004');
