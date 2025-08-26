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
                order_id             UUID,
                order_date           TIMESTAMP,
                check_in             TIME,
                check_out            TIME,
                note                 TEXT,
                payment_status       VARCHAR,
                payment_type         VARCHAR,
                tip                  NUMERIC,
                vat                  NUMERIC,
                discount             NUMERIC,
                order_delete_flag    BOOLEAN,
                total_price          NUMERIC,
                customer_id          UUID,
                customer_name        VARCHAR,
                customer_phone       VARCHAR,
                employee_ids         TEXT,
                status_order_detail  VARCHAR,
                note_order_detail    TEXT,
                vehicle_id           UUID,
                license_plate        VARCHAR,
                image_url            TEXT,
                brand_id             INT,
                brand_code           VARCHAR,
                brand_name           VARCHAR,
                model_id             INT,
                model_code           VARCHAR,
                model_name           VARCHAR,
                model_size           VARCHAR,
                service_id           INT,
                service_code         VARCHAR,
                service_name         VARCHAR,
                service_type_code    VARCHAR,
                service_catalog_id   INT,
                service_catalog_code VARCHAR,
                service_price        NUMERIC,
                service_size         VARCHAR
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
               o.tip                AS tip,
               o.vat,
               o.discount,
               o.delete_flag        AS order_delete_flag,
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
                order_id             UUID,
                order_date           TIMESTAMP,
                check_in             TIME,
                check_out            TIME,
                note                 TEXT,
                payment_status       VARCHAR,
                payment_type         VARCHAR,
                tip                  NUMERIC,
                vat                  NUMERIC,
                discount             NUMERIC,
                order_delete_flag    BOOLEAN,
                total_price          NUMERIC,
                customer_id          UUID,
                customer_name        VARCHAR,
                customer_phone       VARCHAR,
                employee_ids         TEXT,
                status_order_detail  VARCHAR,
                note_order_detail    TEXT,
                vehicle_id           UUID,
                license_plate        VARCHAR,
                image_url            TEXT,
                brand_id             INT,
                brand_code           VARCHAR,
                brand_name           VARCHAR,
                model_id             INT,
                model_code           VARCHAR,
                model_name           VARCHAR,
                model_size           VARCHAR,
                service_id           INT,
                service_code         VARCHAR,
                service_name         VARCHAR,
                service_type_code    VARCHAR,
                service_catalog_id   INT,
                service_catalog_code VARCHAR,
                service_price        NUMERIC,
                service_size         VARCHAR
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
               o.delete_flag        AS order_delete_flag,
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
        SELECT v.id,
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

--

CREATE OR REPLACE FUNCTION generate_osd_code()
    RETURNS TEXT AS
$$
DECLARE
    today_code    VARCHAR(10);
    seq_number    INT;
    padded_number TEXT;
BEGIN
    today_code := TO_CHAR(CURRENT_DATE, 'DDMMYYYY');
    INSERT INTO daily_sequence(date_code, current_number)
    VALUES (today_code, 1)
    ON CONFLICT (date_code)
        DO UPDATE SET current_number = daily_sequence.current_number + 1;
    SELECT current_number
    INTO seq_number
    FROM daily_sequence
    WHERE date_code = today_code;
    padded_number := LPAD(seq_number::TEXT, 3, '0');
    RETURN 'OSD' || today_code || padded_number;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_daily_revenue_by_service_type_full_range(p_start_date DATE, p_end_date DATE, p_order_status VARCHAR)
    RETURNS TABLE
            (
                order_date        DATE,
                service_type_code VARCHAR(20),
                service_name      VARCHAR,
                service_type_name VARCHAR,
                net_revenue       NUMERIC,
                gross_revenue     NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT DATE(o.date)                    AS order_date,
               st.code                         AS service_type_code,
               s.service_name                  AS service_name,
               st.service_type_name            AS service_type_name,
               SUM(COALESCE(o.total_price, 0)) AS net_revenue,
               SUM(
                           COALESCE(o.total_price, 0)
                           - COALESCE(o.discount, 0)
                   )                           AS gross_revenue
        FROM orders o
                 JOIN order_detail od ON od.order_code = o.code AND od.delete_flag = FALSE
                 JOIN order_service_dtl osd ON osd.order_detail_code = od.code AND osd.delete_flag = FALSE
                 JOIN service_catalog sc ON sc.code = osd.service_catalog_code AND sc.delete_flag = FALSE
                 JOIN service s ON s.code = sc.service_code AND s.delete_flag = FALSE
                 JOIN service_type st ON st.code = s.service_type_code AND st.delete_flag = FALSE
        WHERE DATE(o.date) BETWEEN p_start_date AND p_end_date
          AND o.delete_flag = FALSE
          AND o.payment_status = p_order_status
        GROUP BY DATE(o.date), st.code, st.service_type_name, s.service_name
        ORDER BY order_date, service_type_code;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_favorite_service(p_start_date DATE, p_end_date DATE)
    RETURNS TABLE
            (
                service_code  VARCHAR,
                service_name  VARCHAR,
                usage_count   BIGINT,
                total_revenue NUMERIC
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT s.code         AS service_code,
               s.service_name AS service_name,
               COUNT(1)       AS usage_count,
               SUM(sc.price)  AS total_revenue
        FROM order_service_dtl osd
                 JOIN order_detail od
                      ON od.code = osd.order_detail_code
                 JOIN service_catalog sc
                      ON sc.code = osd.service_catalog_code
                          AND sc.delete_flag = FALSE
                 JOIN service s
                      ON s.code = sc.service_code
                          AND s.delete_flag = FALSE
                 JOIN orders o
                      ON o.code = od.order_code
                          AND o.delete_flag = FALSE
                          AND o.payment_status = 'DONE'
        WHERE DATE(o.date) BETWEEN p_start_date AND p_end_date
          AND osd.delete_flag = FALSE
        GROUP BY s.code, s.service_name
        ORDER BY usage_count DESC,
                 total_revenue DESC
        LIMIT 5;
END;
$$ LANGUAGE plpgsql;

-- create sequence for service code generation

CREATE OR REPLACE FUNCTION generate_service_sequence_code(p_code TEXT)
    RETURNS TEXT AS
$$
DECLARE
    next_val INT;
BEGIN
    SELECT current_value + 1
    INTO next_val
    FROM service_sequence_code
    WHERE code = p_code
        FOR UPDATE;

    IF next_val > (SELECT max_value FROM service_sequence_code WHERE code = p_code) THEN
        RAISE EXCEPTION 'Code limit reached for type %', p_code;
    END IF;

    UPDATE service_sequence_code
    SET current_value = next_val
    WHERE code = p_code;

    RETURN p_code || LPAD(next_val::TEXT, 4, '0');
END;
$$ LANGUAGE plpgsql;


--

-- CREATE SEQUENCE seq_service_code
--     START WITH 17
--     INCREMENT BY 1
--     MINVALUE 1
--     MAXVALUE 9999
--     CYCLE;
--
-- CREATE OR REPLACE FUNCTION generate_service_code()
--     RETURNS TEXT AS
-- $$
-- DECLARE
--     next_val INTEGER;
-- BEGIN
--     next_val := nextval('seq_service_code');
--     RETURN 'S' || LPAD(next_val::TEXT, 4, '0');
-- END;
-- $$ LANGUAGE plpgsql;
--
-- --
--
-- CREATE SEQUENCE seq_service_catalog_code
--     START WITH 49
--     INCREMENT BY 1
--     MINVALUE 1
--     MAXVALUE 9999
--     CYCLE;
--
-- CREATE OR REPLACE FUNCTION generate_service_catalog_code()
--     RETURNS TEXT AS
-- $$
-- DECLARE
--     next_val INTEGER;
-- BEGIN
--     next_val := nextval('seq_service_catalog_code');
--     RETURN 'SC' || LPAD(next_val::TEXT, 4, '0');
-- END;
-- $$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_basic_services()
    RETURNS TABLE
            (
                service_id           BIGINT,
                service_type_code    VARCHAR,
                service_type_name    VARCHAR,
                service_code         VARCHAR,
                service_name         VARCHAR,
                service_catalog_code VARCHAR,
                price                NUMERIC,
                duration             VARCHAR,
                size                 VARCHAR,
                note                 TEXT
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT sc.id                AS service_id,
               st.code              AS service_type_code,
               st.service_type_name AS service_type_name,
               s.code               AS service_code,
               s.service_name       AS service_name,
               sc.code              AS service_catalog_code,
               sc.price,
               s.duration,
               sc.size,
               s.note::text
        FROM service s
                 JOIN service_type st ON s.service_type_code = st.code
                 JOIN service_catalog sc ON s.code = sc.service_code
        WHERE s.delete_flag = false
          AND st.delete_flag = false
          AND sc.delete_flag = false
        ORDER BY st.code, s.code;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_basic_service_by_code(p_service_code VARCHAR)
    RETURNS TABLE
            (
                service_id           BIGINT,
                service_type_code    VARCHAR,
                service_type_name    VARCHAR,
                service_code         VARCHAR,
                service_name         VARCHAR,
                service_catalog_code VARCHAR,
                price                NUMERIC,
                duration             VARCHAR,
                size                 VARCHAR,
                note                 TEXT
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT sc.id                AS service_id,
               st.code              AS service_type_code,
               st.service_type_name AS service_type_name,
               s.code               AS service_code,
               s.service_name       AS service_name,
               sc.code              AS service_catalog_code,
               sc.price,
               s.duration,
               sc.size,
               s.note::text
        FROM service s
                 JOIN service_type st ON s.service_type_code = st.code
                 JOIN service_catalog sc ON s.code = sc.service_code
        WHERE s.code = p_service_code
          AND s.delete_flag = false
          AND st.delete_flag = false
          AND sc.delete_flag = false
        ORDER BY st.code, s.code;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_customer_vehicle_services_used()
RETURNS TABLE
(
    id            INT,
    license_plate VARCHAR,
    vehicle_name  VARCHAR,
    customer_name VARCHAR,
    customer_id   UUID,
    phone	      VARCHAR,
    service_usage INT,
    note	      TEXT
)
AS
$$
BEGIN
    RETURN QUERY
    select
        ROW_NUMBER() OVER (ORDER BY v.license_plate)::INT AS id,
		v.license_plate,
		CONCAT(b.brand_name, ' ', m.model_name)::VARCHAR as vehicle_name,
		COALESCE(c.customer_name , 'Chưa cập nhật')::VARCHAR AS customer_name,
        c.id AS customer_id,
		COALESCE(c.phone , 'Chưa cập nhật')::VARCHAR AS phone,
		COUNT(od.id)::INT as service_usage,
		v.note
	from vehicle v
	join order_detail od on od.vehicle_id = v.id
	join orders o on od.order_code = o.code
	left join customer c on c.id = o.customer_id
	join model m on v.model_code = m.code
	join brands b on m.brand_code = b.code 
	group by v.license_plate, c.customer_name, c.id, c.phone, m.model_name, b.brand_name, v.note;
END;
$$ LANGUAGE plpgsql;

--

CREATE OR REPLACE FUNCTION get_customer_vehicle_services_used_detail(p_license_plate VARCHAR)
RETURNS TABLE
(
    id   INT,
    service_name VARCHAR,
    date  		 TIMESTAMP
)
AS
$$
BEGIN
    RETURN QUERY
    select
        ROW_NUMBER() OVER (ORDER BY o.date)::INT AS id,
        s.service_name,
        o.date 
    from orders o
	join order_detail od on od.order_code = o.code
	join vehicle v on od.vehicle_id = v.id
	join order_service_dtl osd on od.code = osd.order_detail_code
	join service_catalog sc on osd.service_catalog_code  = sc.code
	join service s on sc.service_code = s.code
	where v.license_plate = p_license_plate;
END;
$$ LANGUAGE plpgsql;