DROP TABLE IF EXISTS alphawash_db;
create database alphawash_db;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
create type size as enum ('S', 'M', 'L');

create table service_type
(
    id                SERIAL PRIMARY KEY,
    service_type_name VARCHAR(50)
);

create table service
(
    id              SERIAL PRIMARY KEY,
    service_name    VARCHAR(200),
    duration        TEXT,
    service_type_id SERIAL REFERENCES service_type (id),
    note            TEXT
);

create table service_catalog
(
    id         SERIAL PRIMARY KEY,
    size       SIZE                           NOT NULL,
    price      NUMERIC                        NOT NULL,
    service_id SERIAL REFERENCES service (id) NOT NULL
);

create table employee
(
    id    SERIAL PRIMARY KEY,
    name  varchar(50) NOT NULL,
    phone varchar(10) UNIQUE NOT NULL,
    note  TEXT
);

create table employee_skill
(
    id          SERIAL PRIMARY KEY,
    employee_id SERIAL REFERENCES employee (id),
    service_id  SERIAL REFERENCES service (id)
);

create table customer
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_name VARCHAR(100),
    phone         VARCHAR(10) UNIQUE NOT NULL,
    NOTE          TEXT
);

CREATE TABLE brands
(
    id         SERIAL PRIMARY KEY,
    brand_name VARCHAR(50)
);

CREATE TABLE model
(
    id         SERIAL PRIMARY KEY,
    model_name VARCHAR(50) NOT NULL,
    size       VARCHAR(5)  NOT NULL,
    brand_id   SERIAL references brands (id)
);

CREATE TABLE vehicle
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    license_plate VARCHAR(8) UNIQUE NOT NULL,
    customer_id   UUID REFERENCES customer (id),
    brand_id      SERIAL REFERENCES brands (id),
    model_id      SERIAL REFERENCES model (id),
    image_url     TEXT,
    note          TEXT
);

CREATE TABLE orders
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id   TEXT,
    date          TIMESTAMP,
    created_at    TIMESTAMP,
    checkin_time  TIME,
    checkout_time TIME,
    vat           NUMERIC,
    total_price   NUMERIC
);

CREATE TABLE order_detail
(
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id           UUID REFERENCES orders (id)            NOT NULL,
    service_catalog_id SERIAL REFERENCES service_catalog (id) NOT NULL,
    customer_id        UUID REFERENCES customer (id)          NOT NULL,
    vehicle_id         UUID REFERENCES vehicle (id)           NOT NULL
);