-- ============================================================
-- 08-service-quote.sql
-- Tạo bảng service_item, quote, quote_item
-- ============================================================

-- ─── service_item ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS service_item (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)   NOT NULL,
    category        VARCHAR(50)    NOT NULL,
    brand           VARCHAR(100),
    type_detail     VARCHAR(100),
    warranty        VARCHAR(100),
    price_s         NUMERIC(18, 2),
    price_m         NUMERIC(18, 2),
    price_l         NUMERIC(18, 2),
    price_sedan     NUMERIC(18, 2),
    price_suv       NUMERIC(18, 2),
    price_oversize  NUMERIC(18, 2),
    can_be_bonus    BOOLEAN        NOT NULL DEFAULT FALSE,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    description     TEXT,
    sort_order      INTEGER        NOT NULL DEFAULT 0,
    -- BaseEntity fields
    delete_flag     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    exclusive_key   INTEGER        DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_service_item_category ON service_item (category);
CREATE INDEX IF NOT EXISTS idx_service_item_active   ON service_item (active);

-- ─── quote ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS quote (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_code      VARCHAR(30)    NOT NULL UNIQUE,
    customer_id     UUID,
    customer_name   VARCHAR(255),
    customer_phone  VARCHAR(20),
    vehicle_id      UUID,
    license_plate   VARCHAR(20),
    car_model       VARCHAR(100),
    car_size        VARCHAR(20),
    quote_date      DATE,
    status          VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',
    subtotal        NUMERIC(18, 2),
    discount        NUMERIC(18, 2) NOT NULL DEFAULT 0,
    extra_charge    NUMERIC(18, 2) NOT NULL DEFAULT 0,
    total           NUMERIC(18, 2),
    notes           TEXT,
    -- BaseEntity fields
    delete_flag     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    exclusive_key   INTEGER        DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_quote_status     ON quote (status);
CREATE INDEX IF NOT EXISTS idx_quote_created_at ON quote (created_at);
CREATE INDEX IF NOT EXISTS idx_quote_phone      ON quote (customer_phone);

-- ─── quote_item ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS quote_item (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id     UUID           NOT NULL REFERENCES quote (id) ON DELETE CASCADE,
    service_id   UUID,
    service_name VARCHAR(255)   NOT NULL,
    brand        VARCHAR(100),
    type_detail  VARCHAR(100),
    warranty     VARCHAR(100),
    price        NUMERIC(18, 2) NOT NULL DEFAULT 0,
    is_bonus     BOOLEAN        NOT NULL DEFAULT FALSE,
    sort_order   INTEGER        NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_quote_item_quote_id ON quote_item (quote_id);
