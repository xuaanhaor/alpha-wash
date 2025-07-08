CREATE DATABASE alphawash_db;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE production;

CREATE TABLE production(
    id SERIAL PRIMARY KEY,
    stt varchar(20) NOT NULL,
    date varchar(20) NOT NULL,
    time_in varchar(20) NOT NULL,
    time_out varchar(20),
    plate_number varchar(20) NOT NULL,
    customer_name varchar(50),
    sdt varchar(20),
    car_company varchar(20) NOT NULL,
    vehicle_line varchar(20) NOT NULL,
    service varchar(20) ,
    car_size varchar(20),
    status varchar(20)
);

insert into production (car_company,car_size,date,plate_number,sdt,service,status,stt,time_in,time_out,user,vehicle_line) values ('string','string','string','string','string','string','string','string','string','string','string','string')

CREATE TABLE customer (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(100) NOT NULL,
  phone VARCHAR(20) NOT NULL
);

CREATE TABLE vehicle_catalog (
  id SERIAL PRIMARY KEY,
  brand VARCHAR(100) NOT NULL,
  model VARCHAR(100) NOT NULL,
  year INT,
  size VARCHAR(10) NOT NULL CHECK (size IN ('S','M','L','XL'))
);

CREATE TABLE vehicle (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  customer_id UUID NULL ,
  licence_plate VARCHAR(20) UNIQUE NOT NULL,
  catalog_id INT REFERENCES vehicle_catalog(id),
  image_url TEXT
);

CREATE TABLE service (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL
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

INSERT INTO vehicle_catalog (brand, model, year, size) VALUES
  ('Honda', 'Civic', 2024, 'M'),
  ('Vinfast', 'VF9', 2023, 'XL');

INSERT INTO vehicle (id, customer_id, licence_plate, catalog_id, image_url) VALUES
  ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '51H-12345', 1, 'https://s.cmx-cdn.com/tiepthigiadinh.vn/files/minhhieu/2024/01/16/honda-civic-rs-2024-8-103341.jpg'),
  ('20000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '59A-88888', 2, 'https://i1-vnexpress.vnecdn.net/2023/03/27/VF9thumjpg-1679907708.jpg?w=750&h=450&q=100&dpr=2&fit=crop&s=Emnnw5hm6JdteUhSkZGTww');

INSERT INTO service (name) VALUES
  ('Fast Wash'),
  ('Standard Wash'),
  ('Deep Wash');

INSERT INTO employee (id, name, role) VALUES
  ('40000000-0000-0000-0000-000000000001', 'Nguyễn Thanh Tùng', 'kỹ thuật'),
  ('40000000-0000-0000-0000-000000000002', 'Nguyễn Tấn Hưng', 'kỹ thuật');

INSERT INTO order_service (id, vehicle_id, service_id, check_in_time, check_out_time, status) VALUES
  ('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 2, NOW(), NOW() + INTERVAL '1 hour', 'PENDING');

INSERT INTO order_service_detail (id, order_service_id, employee_id) VALUES
  ('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001'),
  ('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000002');
