CREATE DATABASE alphawash_db;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customer (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(100) NOT NULL,
  phone VARCHAR(20) NOT NULL
);

CREATE TABLE vehicle_catalog (
  id SERIAL PRIMARY KEY,
  brand VARCHAR(100) NOT NULL,
  model VARCHAR(100) NOT NULL,
  size VARCHAR(10) NOT NULL CHECK (size IN ('S','M','L'))
);

CREATE TABLE vehicle (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  customer_id UUID NULL ,
  licence_plate VARCHAR(20) UNIQUE NOT NULL,
  catalog_id INT REFERENCES vehicle_catalog(id),
  image_url TEXT
);

CREATE TABLE IF NOT EXISTS service (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration VARCHAR(50),
    labor_count VARCHAR(20),
    category VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS service_price (
    id SERIAL PRIMARY KEY,
    service_id INT REFERENCES service(id),
    size VARCHAR(2) NOT NULL CHECK (size IN ('S', 'M', 'L')),
    price NUMERIC(15, 2) NOT NULL
);

CREATE TABLE employee (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(100) NOT NULL,
  role VARCHAR(50)
);

CREATE TABLE order_service (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  vehicle_id UUID NOT NULL REFERENCES vehicle(id),
  service_id INT NULL,
  check_in_time TIMESTAMP NOT NULL,
  check_out_time TIMESTAMP,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE order_service_detail (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  order_service_id UUID NOT NULL REFERENCES order_service(id),
  employee_id UUID NULL
);

INSERT INTO customer (id, name, phone) VALUES
  ('00000000-0000-0000-0000-000000000001', 'Nguyễn Văn A', '0909123456'),
  ('00000000-0000-0000-0000-000000000002', 'Trần Thị B', '0911223344');

INSERT INTO vehicle (id, customer_id, licence_plate, catalog_id, image_url) VALUES
  ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '51H-12345', 1, 'https://s.cmx-cdn.com/tiepthigiadinh.vn/files/minhhieu/2024/01/16/honda-civic-rs-2024-8-103341.jpg'),
  ('20000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '59A-88888', 2, 'https://i1-vnexpress.vnecdn.net/2023/03/27/VF9thumjpg-1679907708.jpg?w=750&h=450&q=100&dpr=2&fit=crop&s=Emnnw5hm6JdteUhSkZGTww');

INSERT INTO service (id, name, duration, labor_count, category) VALUES (1, 'Quick Wash', '30m–45m', '1–3', 'Washing');
INSERT INTO service_price (service_id, size, price) VALUES (1, 'S', 150000);
INSERT INTO service_price (service_id, size, price) VALUES (1, 'M', 150000);
INSERT INTO service_price (service_id, size, price) VALUES (1, 'L', 170000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (2, 'Standard Wash', '1h–1h15m', '1–3', 'Washing');
INSERT INTO service_price (service_id, size, price) VALUES (2, 'S', 250000);
INSERT INTO service_price (service_id, size, price) VALUES (2, 'M', 300000);
INSERT INTO service_price (service_id, size, price) VALUES (2, 'L', 350000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (3, 'Deep Wash', '1h30–2h', '1–3', 'Washing');
INSERT INTO service_price (service_id, size, price) VALUES (3, 'S', 850000);
INSERT INTO service_price (service_id, size, price) VALUES (3, 'M', 950000);
INSERT INTO service_price (service_id, size, price) VALUES (3, 'L', 1050000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (4, 'Vệ sinh nội thất', '4h–5h', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (4, 'S', 2500000);
INSERT INTO service_price (service_id, size, price) VALUES (4, 'M', 2500000);
INSERT INTO service_price (service_id, size, price) VALUES (4, 'L', 3000000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (5, 'Vệ sinh khoang máy', '1h30–2h', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (5, 'S', 1800000);
INSERT INTO service_price (service_id, size, price) VALUES (5, 'M', 1800000);
INSERT INTO service_price (service_id, size, price) VALUES (5, 'L', 1800000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (6, 'Vệ sinh dàn lạnh', '1h', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (6, 'S', 1500000);
INSERT INTO service_price (service_id, size, price) VALUES (6, 'M', 1500000);
INSERT INTO service_price (service_id, size, price) VALUES (6, 'L', 1500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (7, 'Tẩy nhựa đường', '45P', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (7, 'S', 300000);
INSERT INTO service_price (service_id, size, price) VALUES (7, 'M', 400000);
INSERT INTO service_price (service_id, size, price) VALUES (7, 'L', 500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (8, 'Tẩy ố kính/màng dầu', '3H', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (8, 'S', 1000000);
INSERT INTO service_price (service_id, size, price) VALUES (8, 'M', 1200000);
INSERT INTO service_price (service_id, size, price) VALUES (8, 'L', 1300000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (9, 'Vệ sinh mâm xe (4 bánh)', '3h', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (9, 'S', 1200000);
INSERT INTO service_price (service_id, size, price) VALUES (9, 'M', 1200000);
INSERT INTO service_price (service_id, size, price) VALUES (9, 'L', 1200000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (10, 'Đánh bóng sơn', '8h', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (10, 'S', 2500000);
INSERT INTO service_price (service_id, size, price) VALUES (10, 'M', 2500000);
INSERT INTO service_price (service_id, size, price) VALUES (10, 'L', 2800000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (11, 'Hiệu chỉnh sơn', '1 ngày', '2–3', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (11, 'S', 3000000);
INSERT INTO service_price (service_id, size, price) VALUES (11, 'M', 3000000);
INSERT INTO service_price (service_id, size, price) VALUES (11, 'L', 3500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (12, 'Phục hồi nhựa ngoài', '45P', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (12, 'S', 500000);
INSERT INTO service_price (service_id, size, price) VALUES (12, 'M', 500000);
INSERT INTO service_price (service_id, size, price) VALUES (12, 'L', 500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (13, 'Phủ ceramic toàn xe', '3 ngày', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (13, 'S', 11000000);
INSERT INTO service_price (service_id, size, price) VALUES (13, 'M', 11500000);
INSERT INTO service_price (service_id, size, price) VALUES (13, 'L', 12500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (14, 'Ceramic kính', '2 ngày', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (14, 'S', 2200000);
INSERT INTO service_price (service_id, size, price) VALUES (14, 'M', 2200000);
INSERT INTO service_price (service_id, size, price) VALUES (14, 'L', 2500000);
INSERT INTO service (id, name, duration, labor_count, category) VALUES (15, 'Ceramic mâm', '2 ngày', '2', 'Cleaning');
INSERT INTO service_price (service_id, size, price) VALUES (15, 'S', 2200000);
INSERT INTO service_price (service_id, size, price) VALUES (15, 'M', 2200000);
INSERT INTO service_price (service_id, size, price) VALUES (15, 'L', 2200000);

INSERT INTO employee (id, name, role) VALUES
  ('40000000-0000-0000-0000-000000000001', 'Nguyễn Thanh Tùng', 'kỹ thuật'),
  ('40000000-0000-0000-0000-000000000002', 'Nguyễn Tấn Hưng', 'kỹ thuật');

INSERT INTO order_service (id, vehicle_id, service_id, check_in_time, check_out_time, status) VALUES
  ('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 2, NOW(), NOW() + INTERVAL '1 hour', 'PENDING');

INSERT INTO order_service_detail (id, order_service_id, employee_id) VALUES
  ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001'),
  ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000002');
