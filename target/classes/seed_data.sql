-- Sample data for the Online Shop schema (assumes tables already exist)

INSERT INTO customer (id, email, password, full_name, billing_address, default_shipping_address, country, phone, created_at, updated_at) VALUES
    (10, 'andrei.vasilescu@example.com', 'pwdAndrei', 'Andrei Vasilescu', 'Str. Mihai 5, Bucuresti', 'Str. Mihai 5, Bucuresti', 'Romania', '+40 711 000 111', '2025-01-10 08:00:00', '2025-01-10 08:00:00'),
    (11, 'ioana.dumitru@example.com', 'pwdIoana', 'Ioana Dumitru', 'Bd. Unirii 42, Cluj', 'Bd. Unirii 42, Cluj', 'Romania', '+40 722 111 222', '2025-01-10 08:05:00', '2025-01-10 08:05:00'),
    (12, 'mihai.pop@example.com', 'pwdMihai', 'Mihai Pop', 'Str. Libertatii 8, Timisoara', 'Str. Libertatii 8, Timisoara', 'Romania', '+40 733 222 333', '2025-01-10 08:10:00', '2025-01-10 08:10:00'),
    (13, 'roxana.ilie@example.com', 'pwdRoxana', 'Roxana Ilie', 'Str. Oltetului 3, Brasov', 'Str. Oltetului 3, Brasov', 'Romania', '+40 744 333 444', '2025-01-10 08:15:00', '2025-01-10 08:15:00');

INSERT INTO products (id, sku, name, price, weight, descriptions, category, create_date, stock, created_at, updated_at) VALUES
    (20, 'SKU-2001', 'Monitor UltraWide 34"', 2900, 5400, 'Monitor 34" QHD cu HDR', 'Electronice', '2024-12-01', 20, '2025-01-10 08:00:00', '2025-01-10 08:00:00'),
    (21, 'SKU-2002', 'Mouse Wireless Pro', 250, 90, 'Mouse ergonomic cu Bluetooth', 'Accesorii', '2024-12-03', 150, '2025-01-10 08:00:00', '2025-01-10 08:00:00'),
    (22, 'SKU-2003', 'Tastatura Mecanica RGB', 450, 800, 'Tastatura mecanica cu switch-uri tactile', 'Accesorii', '2024-12-05', 60, '2025-01-10 08:00:00', '2025-01-10 08:00:00'),
    (23, 'SKU-2004', 'Tableta SketchPad', 1800, 600, 'Tableta grafica pentru designeri', 'Electronice', '2024-12-07', 35, '2025-01-10 08:00:00', '2025-01-10 08:00:00');

INSERT INTO orders (id, ammount, shipping_adress, order_adress, order_email, order_date, order_status, user_id, created_at, updated_at) VALUES
    (30, 3350, 'Str. Mihai 5, Bucuresti', 'Str. Mihai 5, Bucuresti', 'andrei.vasilescu@example.com', '2025-01-12', 'LIVRATA', 10, '2025-01-12 09:00:00', '2025-01-12 09:00:00'),
    (31, 2050, 'Bd. Unirii 42, Cluj', 'Bd. Unirii 42, Cluj', 'ioana.dumitru@example.com', '2025-01-13', 'IN_PROCESARE', 11, '2025-01-13 10:15:00', '2025-01-13 10:15:00'),
    (32, 2250, 'Str. Libertatii 8, Timisoara', 'Str. Libertatii 8, Timisoara', 'mihai.pop@example.com', '2025-01-14', 'PLASATA', 12, '2025-01-14 11:20:00', '2025-01-14 11:20:00');

INSERT INTO order_details (id, price, sku, quantity, order_id, product_id, created_at, updated_at) VALUES
    (40, 2900, 'SKU-2001', 1, 30, 20, '2025-01-12 09:00:00', '2025-01-12 09:00:00'),
    (41, 450, 'SKU-2003', 1, 30, 22, '2025-01-12 09:00:00', '2025-01-12 09:00:00'),
    (42, 250, 'SKU-2002', 1, 31, 21, '2025-01-13 10:15:00', '2025-01-13 10:15:00'),
    (43, 1800, 'SKU-2004', 1, 32, 23, '2025-01-14 11:20:00', '2025-01-14 11:20:00'),
    (44, 450, 'SKU-2003', 1, 32, 22, '2025-01-14 11:20:00', '2025-01-14 11:20:00');
