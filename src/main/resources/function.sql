CREATE OR REPLACE FUNCTION get_brand_with_model()
    RETURNS TABLE
            (
                model_code VARCHAR,
                model_name VARCHAR,
                size       VARCHAR,
                brand_code VARCHAR,
                brand_name VARCHAR
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT m.code as model_code,
               m.model_name,
               m.size,
               b.code as brand_code,
               b.brand_name
        FROM model m
                 JOIN brands b ON m.brand_code = b.code;
END;
$$ LANGUAGE plpgsql;

--

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
                 JOIN customer c ON o.customer_id = c.id
                 JOIN service_catalog sc ON d.service_catalog_code = sc.code
                 JOIN service s ON sc.service_code = s.code
                 JOIN vehicle v ON c.id = v.customer_id
                 JOIN brands b ON b.code = v.brand_code
                 JOIN model m ON m.code = v.model_code
        WHERE o.id = p_order_id
        GROUP BY o.id, c.customer_name, d.id, s.service_name, sc.size, b.id, m.id;
END;
$$;

--



CREATE OR REPLACE FUNCTION get_full_orders()
    RETURNS TABLE
            (
                order_id            UUID,
                order_date          TIMESTAMP,
                check_in            TIME,
                check_out           TIME,
                note                TEXT,
                payment_status      VARCHAR,
                payment_type        VARCHAR,
                tip                 NUMERIC,
                vat                 NUMERIC,
                discount            NUMERIC,
                order_delete_flag   BOOLEAN,
                total_price         NUMERIC,
                customer_id         UUID,
                customer_name       VARCHAR,
                customer_phone      VARCHAR,
                employee_ids        TEXT,
                status_order_detail VARCHAR,
                note_order_detail   TEXT,
                vehicle_id          UUID,
                license_plate       VARCHAR,
                image_url           TEXT,
                brand_id            INT,
                brand_code          VARCHAR,
                brand_name          VARCHAR,
                model_id            INT,
                model_code          VARCHAR,
                model_name          VARCHAR,
                model_size          VARCHAR,
                service_id          INT,
                service_code        VARCHAR,
                service_name        VARCHAR,
                service_type_code   VARCHAR,
                service_catalog_id  INT,
                service_catalog_code VARCHAR,
                service_price       NUMERIC,
                service_size        VARCHAR
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT o.id             AS order_id,
               o.date           AS order_date,
               o.checkin_time   AS check_in,
               o.checkout_time  AS check_out,
               o.note::TEXT     AS note,
               o.payment_status AS payment_status,
               o.payment_type   AS payment_type,
               o.tip            AS tip,
               o.vat,
               o.discount,
			   o.delete_flag AS order_delete_flag,
               o.total_price,
               c.id             AS customer_id,
               c.customer_name,
               c.phone          AS customer_phone,
               od.employee_id::TEXT AS employee_ids,
               od.status        AS status_order_detail,
               od.note::TEXT    AS note_order_detail,
               v.id             AS vehicle_id,
               v.license_plate,
               v.image_url::TEXT,
               b.id::INT        AS brand_id,
               b.code           AS brand_code,
               b.brand_name,
               m.id::INT        AS model_id,
               m.code           AS model_code,
               m.model_name,
               m.size           AS model_size,
               s.id::INT        AS service_id,
               s.code           AS service_code,
               s.service_name,
               s.service_type_code AS service_type_code,
               sc.id::INT       AS service_catalog_id,
               sc.code          AS service_catalog_code,
               sc.price         AS service_price,
               sc.size::VARCHAR AS service_size
        FROM orders o
        LEFT JOIN customer c ON o.customer_id = c.id
        LEFT JOIN order_detail od ON od.order_id = o.id
        LEFT JOIN vehicle v ON v.id = od.vehicle_id
        LEFT JOIN brands b ON b.code = v.brand_code
        LEFT JOIN model m ON m.code = v.model_code
        LEFT JOIN service_catalog sc ON sc.code = od.service_catalog_code
        LEFT JOIN service s ON s.code = sc.service_code;
END;
$$ LANGUAGE plpgsql;


------

CREATE OR REPLACE FUNCTION get_orders()
    RETURNS TABLE
            (
                order_id          UUID,
                date              TIMESTAMP,
                checkin_time      TIME,
                checkout_time     TIME,
                discount          NUMERIC,
                customer_name     VARCHAR,
                phone             VARCHAR,
                brand_code        VARCHAR,
                brand_name        VARCHAR,
                model_code        VARCHAR,
                model_name        VARCHAR,
                size              VARCHAR,
                service_name      VARCHAR,
                service_type_name VARCHAR,
                service_price     NUMERIC,
                duration          TEXT
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT o.id            AS order_id,
               o.date,
               o.checkout_time AS checkin_time,
               o.checkout_time AS checkout_time,
               o.discount,
               c.customer_name,
               c.phone,
               b.code          AS brand_code,
               b.brand_name,
               m.code          AS model_code,
               m.model_name,
               m.size,
               s.service_name,
               st.service_type_name,
               sc.price        AS service_price,
               s.duration
        FROM orders o
                 JOIN order_detail od ON o.id = od.order_id
                 LEFT JOIN customer c ON o.customer_id = c.id
                 JOIN service_catalog sc ON od.service_catalog_code = sc.code
                 JOIN service s ON sc.service_code = s.code
                 JOIN service_type st ON s.service_type_code = st.code
                 JOIN vehicle v ON od.vehicle_id = v.id
                 JOIN brands b ON v.brand_code = b.code
                 JOIN model m ON v.model_code = m.code
        WHERE o.delete_flag = false;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_customer_vehicle_by_phone(p_customer_phone VARCHAR)
    RETURNS TABLE
            (
                id            UUID,
                phone         VARCHAR,
                customer_name VARCHAR,
                brand_code    VARCHAR,
                brand_name    VARCHAR,
                model_code    VARCHAR,
                model_name    VARCHAR,
                license_plate VARCHAR
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT c.id,
               c.phone,
               c.customer_name,
               v.brand_code,
               b.brand_name,
               v.model_code,
               m.model_name,
               v.license_plate
        FROM customer c
                 LEFT JOIN vehicle v ON c.id = v.customer_id AND v.delete_flag = false
                 LEFT JOIN brands b ON v.brand_code = b.code
                 LEFT JOIN model m ON v.model_code = m.code
        WHERE c.phone = p_customer_phone
          AND c.delete_flag = false;
END;
$$ LANGUAGE plpgsql;

---

CREATE OR REPLACE FUNCTION get_customer_vehicle_by_license_plate(p_customer_license_plate VARCHAR)
    RETURNS TABLE
            (
                id            UUID,
                phone         VARCHAR,
                customer_name VARCHAR,
                brand_code    VARCHAR,
                brand_name    VARCHAR,
                model_code    VARCHAR,
                model_name    VARCHAR,
                license_plate VARCHAR
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT c.id,
               c.phone,
               c.customer_name,
               v.brand_code,
               b.brand_name,
               v.model_code,
               m.model_name,
               v.license_plate
        FROM customer c
                 LEFT JOIN vehicle v ON c.id = v.customer_id AND v.delete_flag = false
                 LEFT JOIN brands b ON v.brand_code = b.code
                 LEFT JOIN model m ON v.model_code = m.code
        WHERE v.license_plate = p_customer_license_plate
          AND v.delete_flag = false
          AND c.delete_flag = false;
END;
$$ LANGUAGE plpgsql;

---


CREATE OR REPLACE FUNCTION get_full_order_by_id(p_order_id UUID)
    RETURNS TABLE
            (
                order_id            UUID,
                order_date          TIMESTAMP,
                check_in            TIME,
                check_out           TIME,
                note                TEXT,
                payment_status      VARCHAR,
                payment_type        VARCHAR,
                tip                 NUMERIC,
                vat                 NUMERIC,
                discount            NUMERIC,
                order_delete_flag   BOOLEAN,
                total_price         NUMERIC,
                customer_id         UUID,
                customer_name       VARCHAR,
                customer_phone      VARCHAR,
                employee_ids        TEXT,
                status_order_detail VARCHAR,
                note_order_detail   TEXT,
                vehicle_id          UUID,
                license_plate       VARCHAR,
                image_url           TEXT,
                brand_id            INT,
                brand_code          VARCHAR,
                brand_name          VARCHAR,
                model_id            INT,
                model_code          VARCHAR,
                model_name          VARCHAR,
                model_size          VARCHAR,
                service_id          INT,
                service_code        VARCHAR,
                service_name        VARCHAR,
                service_type_code   VARCHAR,
                service_catalog_id  INT,
                service_catalog_code VARCHAR,
                service_price       NUMERIC,
                service_size        VARCHAR
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT o.id                 AS order_id,
               o.date               AS order_date,
               o.checkin_time       AS check_in,
               o.checkout_time      AS check_out,
               o.note::TEXT         AS note,
               o.payment_status     AS payment_status,
               o.payment_type       AS payment_type,
               o.tip,
               o.vat,
               o.discount,
               o.delete_flag AS order_delete_flag,
               o.total_price,
               c.id                 AS customer_id,
               c.customer_name,
               c.phone              AS customer_phone,
               od.employee_id::TEXT AS employee_ids,
               od.status            AS status_order_detail,
               od.note::TEXT        AS note_order_detail,
               v.id                 AS vehicle_id,
               v.license_plate,
               v.image_url::TEXT,
               b.id::INT            AS brand_id,
               b.code               AS brand_code,
               b.brand_name,
               m.id::INT            AS model_id,
               m.code               AS model_code,
               m.model_name,
               m.size               AS model_size,
               s.id::INT            AS service_id,
               s.code               AS service_code,
               s.service_name,
               s.service_type_code  AS service_type_code,
               sc.id::INT           AS service_catalog_id,
               sc.code              AS service_catalog_code,
               sc.price             AS service_price,
               sc.size::VARCHAR     AS service_size
        FROM orders o
            LEFT JOIN customer c ON o.customer_id = c.id
            LEFT JOIN order_detail od ON od.order_id = o.id
            LEFT JOIN vehicle v ON v.id = od.vehicle_id
            LEFT JOIN brands b ON b.code = v.brand_code
            LEFT JOIN model m ON m.code = v.model_code
            LEFT JOIN service_catalog sc ON sc.code = od.service_catalog_code
            LEFT JOIN service s ON s.code = sc.service_code
        WHERE o.id = p_order_id;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_vehicle_by_order_id(p_order_id UUID)
    RETURNS TABLE
            (
                id            UUID,
                license_plate VARCHAR,
                customer_id   UUID,
                brand_code    VARCHAR,
                model_code    VARCHAR,
                image_url     TEXT,
                note          TEXT,
                delete_flag   BOOLEAN,
                created_by    VARCHAR,
                updated_by    VARCHAR,
                created_at    TIMESTAMP,
                updated_at    TIMESTAMP,
                exclusive_key INT
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT
            v.id,
            v.license_plate,
            v.customer_id,
            v.brand_code,
            v.model_code,
            v.image_url::TEXT,
            v.note,
            v.delete_flag,
            v.created_by,
            v.updated_by,
            v.created_at,
            v.updated_at,
            v.exclusive_key
        FROM orders o
            JOIN order_detail od ON o.id = od.order_id
            JOIN vehicle v ON od.vehicle_id = v.id
        WHERE o.id = p_order_id
          AND o.delete_flag = false;
END;
$$ LANGUAGE plpgsql;