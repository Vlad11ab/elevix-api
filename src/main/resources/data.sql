-- Seed data for Online Shop
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_details;
TRUNCATE TABLE orders;
TRUNCATE TABLE products;
TRUNCATE TABLE customer;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO customer (id, email, password, full_name, billing_address, default_shipping_address, country, phone, created_at, updated_at)
VALUES
    (1, 'alex.popescu@example.com', 'hash123', 'Alex Popescu', 'Str. Libertatii 10, Bucuresti', 'Str. Libertatii 10, Bucuresti', 'Romania', '+40 721 123 456', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
    (2, 'maria.ionescu@example.com', 'hash456', 'Maria Ionescu', 'Str. Eminescu 24, Cluj', 'Str. Eminescu 24, Cluj', 'Romania', '+40 722 654 321', '2025-01-01 09:05:00', '2025-01-01 09:05:00'),
    (3, 'alexandra.stan@example.com', 'hash789', 'Alexandra Stan', 'Bd. Unirii 5, Iasi', 'Bd. Unirii 5, Iasi', 'Romania', '+40 723 987 654', '2025-01-01 09:10:00', '2025-01-01 09:10:00');

INSERT INTO products (id, sku, name, price, weight, descriptions, category, create_date, stock, created_at, updated_at)
VALUES
    (1, 'SKU-1001', 'Laptop Pro 15"', 5200, 1900, 'Laptop performant pentru lucru si gaming', 'Electronice', '2024-11-15', 12, '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
    (2, 'SKU-1002', 'Smartphone Alpha', 3200, 180, 'Telefon flagship cu camera tripla', 'Electronice', '2024-11-20', 30, '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
    (3, 'SKU-1003', 'Casti Wireless AirBeat', 550, 60, 'Casti Bluetooth cu anulare a zgomotului', 'Accesorii', '2024-11-25', 45, '2025-01-01 09:00:00', '2025-01-01 09:00:00');

INSERT INTO orders (id, ammount, shipping_adress, order_adress, order_email, order_date, order_status, user_id, created_at, updated_at)
VALUES
    (1, 8750, 'Str. Libertatii 10, Bucuresti', 'Str. Libertatii 10, Bucuresti', 'alex.popescu@example.com', '2025-01-05', 'LIVRATA', 1, '2025-01-05 10:00:00', '2025-01-05 10:00:00'),
    (2, 3750, 'Str. Eminescu 24, Cluj', 'Str. Eminescu 24, Cluj', 'maria.ionescu@example.com', '2025-01-07', 'IN_PROCESARE', 2, '2025-01-07 11:30:00', '2025-01-07 11:30:00');

INSERT INTO order_details (id, price, sku, quantity, order_id, product_id, created_at, updated_at)
VALUES
    (1, 5200, 'SKU-1001', 1, 1, 1, '2025-01-05 10:00:00', '2025-01-05 10:00:00'),
    (2, 3550, 'SKU-1002', 1, 1, 2, '2025-01-05 10:00:00', '2025-01-05 10:00:00'),
    (3, 3200, 'SKU-1002', 1, 2, 2, '2025-01-07 11:30:00', '2025-01-07 11:30:00'),
    (4, 550, 'SKU-1003', 1, 2, 3, '2025-01-07 11:30:00', '2025-01-07 11:30:00');
