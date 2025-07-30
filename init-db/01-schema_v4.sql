DROP TABLE IF EXISTS alphawash_db_v4;

CREATE DATABASE alphawash_db_v4;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE size AS ENUM ('S', 'M', 'L');

CREATE TABLE service_type
(
    id                SERIAL,
    code              VARCHAR(20) UNIQUE NOT NULL,
    service_type_name VARCHAR(50),
    delete_flag       BOOLEAN   DEFAULT FALSE,
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key     INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE service
(
    id                SERIAL,
    code              VARCHAR(20) UNIQUE NOT NULL,
    service_name      VARCHAR(200),
    duration          TEXT,
    service_type_code VARCHAR(20) REFERENCES service_type (code),
    note              TEXT,
    delete_flag       BOOLEAN   DEFAULT FALSE,
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key     INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE service_catalog
(
    id            SERIAL,
    code          VARCHAR(20) UNIQUE                    NOT NULL,
    size          SIZE                                  NOT NULL,
    price         NUMERIC                               NOT NULL,
    service_code  VARCHAR(20) REFERENCES service (code) NOT NULL,
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE employee
(
    id              SERIAL,
    name            VARCHAR(50)        NOT NULL,
    phone           VARCHAR(10) UNIQUE NOT NULL,
    bank_name       VARCHAR(50),
    bank_account    VARCHAR(20),
    date_of_birth   TIMESTAMP,
    identity_number VARCHAR(15),
    join_date       TIMESTAMP,
    work_status     VARCHAR(50),
    note            TEXT,
    delete_flag     BOOLEAN   DEFAULT FALSE,
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key   INT       DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE employee_skill
(
    id            SERIAL,
    employee_id   SERIAL REFERENCES employee (id),
    service_code  VARCHAR(20) REFERENCES service (code),
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE customer
(
    id            UUID      DEFAULT uuid_generate_v4(),
    customer_name VARCHAR(100),
    phone         VARCHAR(10) UNIQUE,
    NOTE          TEXT,
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE brands
(
    id            SERIAL,
    code          VARCHAR(20) UNIQUE NOT NULL,
    brand_name    VARCHAR(50),
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE model
(
    id            SERIAL,
    code          VARCHAR(20) UNIQUE NOT NULL,
    model_name    VARCHAR(50)        NOT NULL,
    size          VARCHAR(5)         NOT NULL,
    brand_code    VARCHAR(20) REFERENCES brands (code),
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);
CREATE TABLE vehicle
(
    id            UUID      DEFAULT uuid_generate_v4(),
    license_plate VARCHAR(8) UNIQUE NOT NULL,
    customer_id   UUID REFERENCES customer (id),
    brand_code    VARCHAR(20) REFERENCES brands (code),
    model_code    VARCHAR(20) REFERENCES model (code),
    image_url     TEXT,
    note          TEXT,
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (id)
);


CREATE TABLE orders
(
    id             UUID        DEFAULT uuid_generate_v4(),
    code           VARCHAR(20) UNIQUE NOT NULL,
    customer_id    UUID,
    date           TIMESTAMP,
    checkin_time   TIME,
    checkout_time  TIME,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_type   VARCHAR(20),
    vat            NUMERIC,
    tip            NUMERIC,
    discount       NUMERIC,
    note           TEXT,
    total_price    NUMERIC,
    delete_flag    BOOLEAN     DEFAULT FALSE,
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    exclusive_key  INT         DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE order_detail
(
    id            UUID        DEFAULT uuid_generate_v4(),
    code          VARCHAR(20) UNIQUE                   NOT NULL,
    order_code    VARCHAR(20) REFERENCES orders (code) NOT NULL,
    employee_id   TEXT,
    status        VARCHAR(20) DEFAULT 'PENDING',
    vehicle_id    UUID REFERENCES vehicle (id)         NOT NULL,
    note          TEXT,
    delete_flag   BOOLEAN     DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT         DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE order_service_dtl
(
    code                 VARCHAR(20) UNIQUE                         NOT NULL PRIMARY KEY,
    order_detail_code    VARCHAR(20) REFERENCES order_detail (code) NOT NULL,
    service_catalog_code VARCHAR(20)                                NOT NULL,
    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,
    UNIQUE (order_detail_code, service_catalog_code)
);