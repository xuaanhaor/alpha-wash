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
    license_plate VARCHAR(8) NOT NULL,
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

DROP FUNCTION get_order_detail(p_order_id UUID);
CREATE OR REPLACE FUNCTION get_order_detail(p_order_id UUID)
    RETURNS TABLE
            (
                id            UUID,
                create_at     TIMESTAMP,
                date          TIMESTAMP,
                checkin_time  TIME,
                checkout_time TIME,
                customer_name VARCHAR(100),
                service_name  VARCHAR(100),
                vehicle_name  TEXT,
                size          size,
                total_price   NUMERIC
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT o.id,
               o.created_at,
               o.date,
               o.checkin_time,
               o.checkout_time,
               c.customer_name,
               s.service_name,
               concat(b.brand_name, ' ', m.model_name) AS vehicle_name,
               sc.size,
               o.total_price
        FROM orders o
                 JOIN order_detail d ON o.id = d.order_id
                 JOIN service_catalog sc ON d.service_catalog_id = sc.id
                 JOIN service s ON sc.service_id = s.id
                 JOIN customer c ON d.customer_id = c.id
                 JOIN vehicle v ON c.id = v.customer_id
                 JOIN brands b ON b.id = v.brand_id
                 JOIN model m ON m.id = v.model_id
        WHERE o.id = p_order_id
        GROUP BY o.id, c.customer_name, d.id, s.service_name, sc.size, b.id, m.id;
END;
$$;


SELECT o.id,
       o.created_at,
       o.date,
       o.checkin_time,
       o.checkout_time,
       c.customer_name,
       s.service_name,
       concat(b.brand_name, ' ', m.model_name) as vehicle_name,
       sc.size,
       o.total_price
FROM orders o
         JOIN order_detail d ON o.id = d.order_id
         JOIN service_catalog sc on d.service_catalog_id = sc.id
         JOIN service s on sc.service_id = s.id
         JOIN customer c on d.customer_id = c.id
         JOIN vehicle v on c.id = v.customer_id
         JOIN brands b on b.id = v.brand_id
         JOIN model m on m.id = v.model_id
WHERE o.id = '0b07fa84-3f58-4240-a0a9-0b49c29d86a8'
GROUP BY o.id, c.customer_name, d.id, s.service_name, sc.size, b.id, m.id;
SELECT *
FROM get_order_detail('0b07fa84-3f58-4240-a0a9-0b49c29d86a8');


select *
from vehicle v
         join customer c on v.customer_id = c.id
         join brands b on v.brand_id = b.id
         join model m on v.model_id = m.id
where c.phone = '0123456789';

SELECT *
FROM vehicle v
         JOIN customer c ON v.customer_id = c.id
         JOIN brands b ON v.brand_id = b.id
         JOIN model m ON v.model_id = m.id
WHERE c.phone = '0123456789';

select c.customer_name,
       c.phone
from customer c
         join vehicle v on c.id = v.customer_id
         join brands b on v.brand_id = b.id
         join model m on b.id = m.brand_id
where c.phone = '0123456789';

-- [12:55:57.229] [13.00ms]
select c1_0.customer_name, c1_0.phone
from customer c1_0
         join vehicle v1_0 on c1_0.id = v1_0.customer_id
         join brands b1_0 on b1_0.id = v1_0.brand_id
         join model m1_0 on m1_0.id = v1_0.model_id
where c1_0.phone = '0373038920'