DROP FUNCTION get_brand_with_model();

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

SELECT *
FROM get_brand_with_model();

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

DROP FUNCTION IF EXISTS get_models_by_brand_code(VARCHAR(20));

CREATE OR REPLACE FUNCTION get_models_by_brand_code(branchCode VARCHAR(20))
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
                 JOIN brands b ON m.code = b.code
        WHERE b.code = branchCode;
END;
$$ LANGUAGE plpgsql;