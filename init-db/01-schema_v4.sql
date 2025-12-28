DROP TABLE IF EXISTS alphawash_db_v4;

CREATE DATABASE alphawash_db_v4;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE size AS ENUM ('S', 'M', 'L', 'XL');

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
    size          SIZE               NOT NULL,
    brand_code    VARCHAR(20) REFERENCES brands (code),
    note          TEXT,
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

ALTER TABLE order_service_dtl
    ADD COLUMN adjusted_price_reason VARCHAR(255),
    ADD COLUMN adjusted_price NUMERIC(18,2),
    ADD COLUMN adjusted_price_flag BOOLEAN DEFAULT FALSE;

CREATE TABLE service_combo
(
    id            SERIAL,
    code          VARCHAR(20) NOT NULL,
    combo_name    VARCHAR(200),
    price         NUMERIC,
    note          TEXT,
    delete_flag   BOOLEAN   DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT       DEFAULT 0,
    PRIMARY KEY (code)
);

CREATE TABLE service_combo_dtl
(
    id                   SERIAL,
    code                 varchar(20) UNIQUE NOT NULL,
    combo_code           VARCHAR(20)        NOT NULL,
    service_catalog_code VARCHAR(20)        NOT NULL,
    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,
    PRIMARY KEY (code),
    UNIQUE (combo_code, service_catalog_code)
);

CREATE TABLE IF NOT EXISTS daily_sequence
(
    date_code      VARCHAR(10) PRIMARY KEY,
    current_number INT DEFAULT 0
);


CREATE TABLE service_sequence_code
(
    code          VARCHAR(20),
    current_value INT NOT NULL,
    max_value     INT NOT NULL,
    PRIMARY KEY (code)
);


CREATE TABLE promotion (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    promo_code      VARCHAR(50) UNIQUE NOT NULL,
    promo_name      VARCHAR(255) NOT NULL,
    promo_type      VARCHAR(30) NOT NULL,
    value           NUMERIC(10,2) NOT NULL,
    usage_limit     INT DEFAULT 1,
    start_date      TIMESTAMP NOT NULL,
    end_date        TIMESTAMP NULL,
    description     TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    promotion_method    VARCHAR(50),
    campaign_link       VARCHAR(255),
    target_audience     VARCHAR(100),
    active_weekdays VARCHAR(50);

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,
    exclusive_key   VARCHAR(50)
);

CREATE TABLE promotion_service (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    promotion_id    UUID NOT NULL,
    service_code    VARCHAR(20) NOT NULL,
    discount_amount     NUMERIC(10,2),
    discount_percent    NUMERIC(5,2),

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,

    CONSTRAINT fk_ps_promotion FOREIGN KEY (promotion_id) REFERENCES promotion(id),
    CONSTRAINT fk_ps_service FOREIGN KEY (service_code) REFERENCES service(code)
);

CREATE TABLE customer_promotion (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    customer_id      UUID        NOT NULL,
    promotion_id     UUID        NOT NULL,
    order_code       VARCHAR(20) NOT NULL,
    used_at          TIMESTAMP   NOT NULL,
    discount_amount  NUMERIC(10,2) NOT NULL DEFAULT 0,
    discount_percent NUMERIC(5,2),

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,

    CONSTRAINT fk_cp_customer  FOREIGN KEY (customer_id)  REFERENCES customer(id),
    CONSTRAINT fk_cp_promotion FOREIGN KEY (promotion_id) REFERENCES promotion(id),
    CONSTRAINT fk_cp_order     FOREIGN KEY (order_code)   REFERENCES orders(code)
);

CREATE TABLE service_combo (
    id              SERIAL,

    code            VARCHAR(50) UNIQUE NOT NULL,
    combo_name      VARCHAR(255) NOT NULL,
    base_price      NUMERIC(12,2) DEFAULT 0,
    note            TEXT,
    duration_days   INT NOT NULL CHECK (duration_days > 0),
    status          VARCHAR(20) DEFAULT 'ACTIVE',

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE TABLE service_combo_catalog (
    id                  SERIAL,

    code                VARCHAR(50) UNIQUE NOT NULL,
    combo_code          VARCHAR(50) REFERENCES service_combo(code) NOT NULL,
    combo_size          VARCHAR(30),

    price               NUMERIC(12,2) NOT NULL,
    price_include_tax   BOOLEAN DEFAULT TRUE,

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,

    PRIMARY KEY (id, code)
);

CREATE TABLE service_combo_quality (
    id                  SERIAL,
    combo_catalog_code  VARCHAR(50) REFERENCES service_combo_catalog(code) NOT NULL,
    service_catalog_code VARCHAR(50) REFERENCES service_catalog(code) NOT NULL,
    quality             INT NOT NULL CHECK (quality > 0),

    delete_flag          BOOLEAN   DEFAULT FALSE,
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key        INT       DEFAULT 0,

    UNIQUE (combo_catalog_code, service_catalog_code)
);

CREATE TABLE customer_combo_summary
(
    id            UUID        DEFAULT uuid_generate_v4(),
    code          VARCHAR(20) UNIQUE NOT NULL,          -- mã combo của khách (CCxxxx)

    customer_id   UUID        NOT NULL,
    order_code    VARCHAR(20) REFERENCES orders (code) NOT NULL,

    service_combo_catalog_code VARCHAR(20) NOT NULL,   -- combo template

    start_at      TIMESTAMP   NOT NULL,
    end_at        TIMESTAMP   NOT NULL,

    total_uses    INT         NOT NULL,
    used_uses     INT         DEFAULT 0,

    status        VARCHAR(20) DEFAULT 'ACTIVE',         -- ACTIVE / EXHAUSTED / EXPIRED

    delete_flag   BOOLEAN     DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT         DEFAULT 0,

    PRIMARY KEY (id, code)
);


CREATE TABLE customer_combo_quota
(
    id            UUID        DEFAULT uuid_generate_v4(),
    code          VARCHAR(20) UNIQUE NOT NULL,

    customer_combo_summary_code VARCHAR(20)
        REFERENCES customer_combo_summary (code)
        NOT NULL,

    service_catalog_code VARCHAR(20)
        REFERENCES service_catalog (code)
        NOT NULL,

    total_uses    INT         NOT NULL,
    used_uses     INT         DEFAULT 0,

    end_at        TIMESTAMP   NOT NULL,

    delete_flag   BOOLEAN     DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT         DEFAULT 0,

    PRIMARY KEY (id, code)
);

ALTER TABLE order_service_dtl
ADD COLUMN service_combo_catalog_code VARCHAR(20);

ALTER TABLE order_service_dtl
ADD CONSTRAINT fk_osd_combo
FOREIGN KEY (service_combo_catalog_code)
REFERENCES customer_combo_summary (code);

ALTER TABLE order_detail
ADD COLUMN order_type VARCHAR(20) NOT NULL DEFAULT 'SERVICE';