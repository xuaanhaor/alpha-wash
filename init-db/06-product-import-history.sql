-- Product Import History
CREATE TABLE IF NOT EXISTS product_import_history (
    id              SERIAL PRIMARY KEY,
    file_name       VARCHAR(255) NOT NULL,
    imported_by     VARCHAR(100) NOT NULL DEFAULT 'admin',
    imported_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    total_rows      INT NOT NULL DEFAULT 0,
    success_rows    INT NOT NULL DEFAULT 0,
    failed_rows     INT NOT NULL DEFAULT 0,
    updated_rows    INT NOT NULL DEFAULT 0,
    skipped_rows    INT NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'PROCESSING',
    import_mode     VARCHAR(20) NOT NULL DEFAULT 'CREATE_ONLY',
    error_file_path TEXT,
    delete_flag     BOOLEAN NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(100) DEFAULT 'admin',
    updated_by      VARCHAR(100),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW(),
    exclusive_key   INT DEFAULT 0
);

CREATE INDEX idx_product_import_history_status ON product_import_history(status);
CREATE INDEX idx_product_import_history_imported_at ON product_import_history(imported_at DESC);
