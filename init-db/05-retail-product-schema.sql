-- ============================================================
-- Retail Product Management Module
-- ============================================================

-- Enums
CREATE TYPE inventory_transaction_type AS ENUM (
    'PURCHASE_RECEIVE', 'SALE', 'STOCK_IN', 'STOCK_OUT', 'ADJUSTMENT', 'RETURN'
);

CREATE TYPE purchase_order_status AS ENUM (
    'DRAFT', 'ORDERED', 'PARTIAL_RECEIVED', 'RECEIVED', 'CANCELLED'
);

-- 1. Product Category
CREATE TABLE product_category (
    code          VARCHAR(20) UNIQUE NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    color         VARCHAR(7),
    display_order INT DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE,
    delete_flag   BOOLEAN DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT DEFAULT 0,
    PRIMARY KEY (id, code)
);

-- 2. Supplier
CREATE TABLE supplier (
    id            SERIAL,
    code          VARCHAR(20) UNIQUE NOT NULL,
    supplier_name VARCHAR(200) NOT NULL,
    phone         VARCHAR(15),
    email         VARCHAR(100),
    address       TEXT,
    tax_id        VARCHAR(20),
    notes         TEXT,
    is_active     BOOLEAN DEFAULT TRUE,
    delete_flag   BOOLEAN DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT DEFAULT 0,
    PRIMARY KEY (id, code)
);

-- 3. Product
CREATE TABLE product (
    id               SERIAL,
    code             VARCHAR(20) UNIQUE NOT NULL,
    barcode          VARCHAR(50),
    product_name     VARCHAR(200) NOT NULL,
    category_code    VARCHAR(20) REFERENCES product_category(code),
    brand            VARCHAR(100),
    description      TEXT,
    cost_price       NUMERIC(18,2) DEFAULT 0,
    selling_price    NUMERIC(18,2) NOT NULL,
    min_price        NUMERIC(18,2) DEFAULT 0,
    suggested_price  NUMERIC(18,2) DEFAULT 0,
    current_stock    INT DEFAULT 0,
    min_stock        INT DEFAULT 0,
    unit             VARCHAR(50) DEFAULT 'pcs',
    location         VARCHAR(100),
    track_inventory  BOOLEAN DEFAULT TRUE,
    supplier_code    VARCHAR(20) REFERENCES supplier(code),
    supplier_sku     VARCHAR(50),
    is_active        BOOLEAN DEFAULT TRUE,
    delete_flag      BOOLEAN DEFAULT FALSE,
    created_by       VARCHAR(50),
    updated_by       VARCHAR(50),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key    INT DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE INDEX idx_product_category ON product(category_code);
CREATE INDEX idx_product_supplier ON product(supplier_code);
CREATE INDEX idx_product_barcode ON product(barcode);
CREATE INDEX idx_product_active ON product(is_active, delete_flag);

-- 4. Product Image
CREATE TABLE product_image (
    id            SERIAL PRIMARY KEY,
    product_code  VARCHAR(20) REFERENCES product(code) NOT NULL,
    image_url     TEXT NOT NULL,
    display_order INT DEFAULT 0,
    is_primary    BOOLEAN DEFAULT FALSE,
    delete_flag   BOOLEAN DEFAULT FALSE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key INT DEFAULT 0
);

CREATE INDEX idx_product_image_product ON product_image(product_code);

-- 5. Inventory Transaction
CREATE TABLE inventory_transaction (
    id               SERIAL PRIMARY KEY,
    code             VARCHAR(30) UNIQUE NOT NULL,
    product_code     VARCHAR(20) REFERENCES product(code) NOT NULL,
    quantity         INT NOT NULL,
    before_qty       INT NOT NULL,
    after_qty        INT NOT NULL,
    type             inventory_transaction_type NOT NULL,
    reference_number VARCHAR(50),
    notes            TEXT,
    delete_flag      BOOLEAN DEFAULT FALSE,
    created_by       VARCHAR(50),
    updated_by       VARCHAR(50),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key    INT DEFAULT 0
);

CREATE INDEX idx_inv_tx_product ON inventory_transaction(product_code);
CREATE INDEX idx_inv_tx_type ON inventory_transaction(type);
CREATE INDEX idx_inv_tx_created ON inventory_transaction(created_at);

-- 6. Purchase Order
CREATE TABLE purchase_order (
    id             SERIAL,
    code           VARCHAR(30) UNIQUE NOT NULL,
    supplier_code  VARCHAR(20) REFERENCES supplier(code) NOT NULL,
    purchase_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invoice_number VARCHAR(50),
    total_amount   NUMERIC(18,2) DEFAULT 0,
    status         purchase_order_status DEFAULT 'DRAFT',
    notes          TEXT,
    delete_flag    BOOLEAN DEFAULT FALSE,
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key  INT DEFAULT 0,
    PRIMARY KEY (id, code)
);

CREATE INDEX idx_po_supplier ON purchase_order(supplier_code);
CREATE INDEX idx_po_status ON purchase_order(status);

-- 7. Purchase Order Item
CREATE TABLE purchase_order_item (
    id                SERIAL PRIMARY KEY,
    po_code           VARCHAR(30) REFERENCES purchase_order(code) NOT NULL,
    product_code      VARCHAR(20) REFERENCES product(code) NOT NULL,
    quantity          INT NOT NULL,
    unit_cost         NUMERIC(18,2) NOT NULL,
    total_cost        NUMERIC(18,2) NOT NULL,
    received_quantity INT DEFAULT 0,
    delete_flag       BOOLEAN DEFAULT FALSE,
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key     INT DEFAULT 0
);

CREATE INDEX idx_poi_po ON purchase_order_item(po_code);
CREATE INDEX idx_poi_product ON purchase_order_item(product_code);

-- 8. Order Product Detail (parallel to order_service_dtl)
CREATE TABLE order_product_dtl (
    id                    SERIAL PRIMARY KEY,
    code                  VARCHAR(30) UNIQUE NOT NULL,
    order_detail_code     VARCHAR(20) REFERENCES order_detail(code) NOT NULL,
    product_code          VARCHAR(20) REFERENCES product(code) NOT NULL,
    quantity              INT NOT NULL DEFAULT 1,
    unit_price            NUMERIC(18,2) NOT NULL,
    adjusted_price        NUMERIC(18,2),
    adjusted_price_flag   BOOLEAN DEFAULT FALSE,
    adjusted_price_reason VARCHAR(255),
    discount              NUMERIC(18,2) DEFAULT 0,
    note                  TEXT,
    delete_flag           BOOLEAN DEFAULT FALSE,
    created_by            VARCHAR(50),
    updated_by            VARCHAR(50),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exclusive_key         INT DEFAULT 0
);

CREATE INDEX idx_opd_order_detail ON order_product_dtl(order_detail_code);
CREATE INDEX idx_opd_product ON order_product_dtl(product_code);

-- Seed data: Product Categories
INSERT INTO product_category (code, category_name, color, display_order, is_active, delete_flag, created_by, created_at)
VALUES
    ('PCAT01', 'Hóa chất', '#3B82F6', 1, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT02', 'Sản phẩm DIY', '#10B981', 2, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT03', 'Phụ kiện', '#F59E0B', 3, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT04', 'Khăn Microfiber', '#8B5CF6', 4, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT05', 'Dụng cụ', '#EF4444', 5, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT06', 'Sản phẩm bảo dưỡng', '#06B6D4', 6, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT07', 'Nước hoa xe', '#EC4899', 7, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT08', 'Sản phẩm vệ sinh', '#84CC16', 8, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP),
    ('PCAT09', 'Khác', '#6B7280', 9, TRUE, FALSE, 'admin', CURRENT_TIMESTAMP);
