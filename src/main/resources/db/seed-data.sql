-- ═══════════════════════════════════════
--  SEED DATA — Online Shop
--  Runs only when SQL_INIT_MODE=always
-- ═══════════════════════════════════════

-- ── Products (10 items) ──────────────

INSERT IGNORE INTO products (id, sku, name, price, weight, descriptions, category, create_date, stock, created_at, updated_at) VALUES
(1,  'SKU-EL-001', 'Laptop ProMax 15',         4599, 2100,  'Laptop performant cu procesor ultima generatie, 16GB RAM, 512GB SSD. Ecran IPS Full HD de 15.6 inch.',               'Electronics', '2024-01-15', 45, NOW(), NOW()),
(2,  'SKU-EL-002', 'Monitor UltraWide 34"',    2199, 6800,  'Monitor curbat IPS, rezolutie WQHD 3440x1440, timp de raspuns 1ms, 144Hz.',                                         'Electronics', '2024-01-18', 22, NOW(), NOW()),
(3,  'SKU-EL-003', 'Tastatura Mecanica RGB',     349, 820,   'Tastatura mecanica cu switch-uri Cherry MX Red, iluminare RGB per tasta, layout full-size.',                         'Electronics', '2024-03-05', 31, NOW(), NOW()),
(4,  'SKU-EL-004', 'Mouse Wireless Ergonomic',   189, 95,    'Mouse wireless ergonomic, sensor 16000 DPI, baterie reincarcabila, conectivitate Bluetooth + USB.',                  'Electronics', '2024-03-10', 58, NOW(), NOW()),
(5,  'SKU-CL-010', 'Tricou Sport Dry-Fit',        89, 180,   'Material respirabil, uscare rapida, ideal pentru antrenament. Disponibil in 5 culori.',                              'Clothing',    '2024-02-03',  5, NOW(), NOW()),
(6,  'SKU-CL-011', 'Geaca Iarna Puf',            599, 750,   'Geaca cu puf natural, rezistenta la apa, captuseala termica, gluga detasabila.',                                     'Clothing',    '2024-03-12', 18, NOW(), NOW()),
(7,  'SKU-HM-020', 'Lampa LED Birou',            149, 950,   'Lampa LED cu 3 moduri de iluminare (cald, neutru, rece), brat flexibil, port USB integrat.',                         'Home',        '2024-02-10',  0, NOW(), NOW()),
(8,  'SKU-HM-021', 'Set Prosoape Bumbac',         79, 600,   'Set 4 prosoape 100% bumbac egipt, 600gsm, diverse culori. Include 2 mari si 2 mici.',                               'Home',        '2024-03-18',  8, NOW(), NOW()),
(9,  'SKU-SP-015', 'Minge Fotbal Pro',           120, 430,   'Minge profesionala FIFA Quality Pro, marime 5, cusuta manual, material PU premium.',                                'Sports',      '2024-03-01', 67, NOW(), NOW()),
(10, 'SKU-SP-016', 'Banda Alergare Pliabila',   2899, 35000, 'Banda de alergare pliabila, motor 2.5HP, viteza max 16km/h, ecran LCD, 12 programe presetate.',                     'Sports',      '2024-04-01',  3, NOW(), NOW());

-- ── Customers (6 users, first is admin) ──

INSERT IGNORE INTO customer (id, email, password, full_name, billing_address, default_shipping_address, country, phone, created_at, updated_at) VALUES
(1, 'admin@onlineshop.ro',       'admin123',   'Admin Shop',       'Str. Administratiei 1, Bucuresti',          'Str. Administratiei 1, Bucuresti',          'Romania',  '0700 000 000',    NOW(), NOW()),
(2, 'andrei.popescu@email.ro',   'parola123',  'Andrei Popescu',   'Str. Victoriei 45, Sector 1, Bucuresti',    'Str. Victoriei 45, Sector 1, Bucuresti',    'Romania',  '0722 111 222',    NOW(), NOW()),
(3, 'maria.ionescu@email.ro',    'maria2024',  'Maria Ionescu',    'Bd. Unirii 12, Cluj-Napoca',                'Bd. Unirii 12, Cluj-Napoca',                'Romania',  '0733 222 333',    NOW(), NOW()),
(4, 'klaus.muller@email.de',     'klaus2024',  'Klaus Muller',     'Berliner Str. 88, Berlin',                  'Berliner Str. 88, Berlin',                  'Germania', '+49 170 123 456', NOW(), NOW()),
(5, 'elena.v@email.ro',          'elena2024',  'Elena Vasilescu',  'Str. Mihai Eminescu 7, Iasi',               'Str. Libertatii 23, Iasi',                  'Romania',  '0744 333 444',    NOW(), NOW()),
(6, 'pierre.dubois@email.fr',    'pierre2024', 'Pierre Dubois',    '15 Rue de Rivoli, Paris',                   '15 Rue de Rivoli, Paris',                   'Franta',   '+33 6 12 34 56',  NOW(), NOW());

-- ── Orders (5 orders) ──

INSERT IGNORE INTO orders (id, ammount, shipping_adress, order_adress, order_email, order_date, order_status, user_id, created_at, updated_at) VALUES
(1, 4897, 'Str. Victoriei 45, Bucuresti', 'Str. Victoriei 45, Bucuresti', 'andrei.popescu@email.ro', '2024-04-01', 'Delivered',   2, NOW(), NOW()),
(2, 689,  'Bd. Unirii 12, Cluj-Napoca',   'Bd. Unirii 12, Cluj-Napoca',   'maria.ionescu@email.ro',  '2024-04-02', 'Processing',  3, NOW(), NOW()),
(3, 2548, 'Berliner Str. 88, Berlin',      'Berliner Str. 88, Berlin',      'klaus.muller@email.de',   '2024-04-03', 'Shipped',     4, NOW(), NOW()),
(4, 120,  '15 Rue de Rivoli, Paris',       '15 Rue de Rivoli, Paris',       'pierre.dubois@email.fr',  '2024-04-05', 'Cancelled',   6, NOW(), NOW()),
(5, 947,  'Str. Victoriei 45, Bucuresti', 'Str. Victoriei 45, Bucuresti', 'andrei.popescu@email.ro', '2024-04-07', 'Processing',  2, NOW(), NOW());

-- ── Order Details (10 line items) ──

INSERT IGNORE INTO order_details (id, price, sku, quantity, order_id, product_id, created_at, updated_at) VALUES
(1,  4599, 'SKU-EL-001', 1, 1, 1,  NOW(), NOW()),
(2,  89,   'SKU-CL-010', 2, 1, 5,  NOW(), NOW()),
(3,  120,  'SKU-SP-015', 1, 1, 9,  NOW(), NOW()),
(4,  599,  'SKU-CL-011', 1, 2, 6,  NOW(), NOW()),
(5,  89,   'SKU-CL-010', 1, 2, 5,  NOW(), NOW()),
(6,  2199, 'SKU-EL-002', 1, 3, 2,  NOW(), NOW()),
(7,  349,  'SKU-EL-003', 1, 3, 3,  NOW(), NOW()),
(8,  120,  'SKU-SP-015', 1, 4, 9,  NOW(), NOW()),
(9,  349,  'SKU-EL-003', 1, 5, 3,  NOW(), NOW()),
(10, 599,  'SKU-CL-011', 1, 5, 6,  NOW(), NOW());
