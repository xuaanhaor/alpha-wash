-- Merge Vehicle Log: audit trail for duplicate vehicle/customer merge operations
CREATE TABLE IF NOT EXISTS merge_vehicle_log (
    id                               BIGSERIAL PRIMARY KEY,
    merge_date                       TIMESTAMP NOT NULL DEFAULT NOW(),
    operator_username                VARCHAR(50),
    primary_customer_id              UUID REFERENCES customer (id),
    primary_customer_name            VARCHAR(100),
    primary_vehicle_id               UUID REFERENCES vehicle (id),
    primary_vehicle_license_plate    VARCHAR(8),
    duplicate_customer_ids           TEXT,
    duplicate_customer_names         TEXT,
    duplicate_vehicle_license_plates TEXT,
    orders_migrated                  INT NOT NULL DEFAULT 0,
    order_details_migrated           INT NOT NULL DEFAULT 0,
    customers_archived               INT NOT NULL DEFAULT 0,
    status                           VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    delete_flag                      BOOLEAN NOT NULL DEFAULT FALSE,
    created_by                       VARCHAR(100) DEFAULT 'admin',
    updated_by                       VARCHAR(100),
    created_at                       TIMESTAMP DEFAULT NOW(),
    updated_at                       TIMESTAMP DEFAULT NOW(),
    exclusive_key                    INT DEFAULT 0
);

CREATE INDEX idx_merge_vehicle_log_merge_date ON merge_vehicle_log (merge_date DESC);
