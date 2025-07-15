DROP TABLE IF EXISTS alphawash_db;

CREATE DATABASE alphawash_db;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE size AS ENUM('S', 'M', 'L');

CREATE TABLE service_type (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL PRIMARY KEY,
  service_type_name VARCHAR(50),
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE service (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL PRIMARY KEY,
  service_name VARCHAR(200),
  duration TEXT,
  service_type_code VARCHAR(20) REFERENCES service_type (code),
  note TEXT,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE service_catalog (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL PRIMARY KEY,
  size SIZE NOT NULL,
  price NUMERIC NOT NULL,
  service_code VARCHAR(20) REFERENCES service (code) NOT NULL,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE employee (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  phone VARCHAR(10) UNIQUE NOT NULL,
  note TEXT,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE employee_skill (
  id SERIAL PRIMARY KEY,
  employee_id SERIAL REFERENCES employee (id),
  service_code VARCHAR(20) REFERENCES service (code),
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE customer (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
  customer_name VARCHAR(100),
  phone VARCHAR(10) UNIQUE NOT NULL,
  NOTE TEXT,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE brands (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL PRIMARY KEY,
  brand_name VARCHAR(50),
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE model (
  id SERIAL PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL PRIMARY KEY,
  model_name VARCHAR(50) NOT NULL,
  size VARCHAR(5) NOT NULL,
  brand_code VARCHAR(20) REFERENCES brands (code),
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE vehicle (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
  license_plate VARCHAR(8) UNIQUE NOT NULL,
  customer_id UUID REFERENCES customer (id),
  brand_code VARCHAR(20) REFERENCES brands (code),
  model_code VARCHAR(20) REFERENCES model (code),
  image_url TEXT,
  note TEXT,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);


CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
  customer_id UUID REFERENCES customer (id) NOT NULL,
  date TIMESTAMP,
  created_at TIMESTAMP,
  checkin_time TIME,
  checkout_time TIME,
  paymentStatus VARCHAR(20) DEFAULT 'PENDING',
  vat NUMERIC,
  discount NUMERIC,
  total_price NUMERIC,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);

CREATE TABLE order_detail (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
  order_id UUID REFERENCES orders (id) NOT NULL,
  employee_id TEXT,
  service_catalog_code VARCHAR(20) REFERENCES service_catalog (code) NOT NULL,
  status VARCHAR(20) DEFAULT 'PENDING',
  vehicle_id UUID REFERENCES vehicle (id) NOT NULL,
  delete_flag BOOLEAN DEFAULT FALSE,
  create_by VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  exclusive_key INT
);