DROP FUNCTION get_brand_with_model();

CREATE OR REPLACE FUNCTION get_brand_with_model()
RETURNS TABLE (
    model_id BIGINT,
    model_name VARCHAR,
    size VARCHAR,
    brand_id BIGINT,
    brand_name VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        m.id::BIGINT,
        m.model_name,
        m.size,
        b.id::BIGINT AS brand_id,
        b.brand_name
    FROM model m
    JOIN brands b ON m.brand_id = b.id;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM get_brand_with_model();

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

--

DROP FUNCTION IF EXISTS get_models_by_brand_id(BIGINT);

CREATE OR REPLACE FUNCTION get_models_by_brand_id(brandId BIGINT)
RETURNS TABLE (
    model_id BIGINT,
    model_name VARCHAR,
    size VARCHAR,
    brand_id BIGINT,
    brand_name VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        m.id::BIGINT,
        m.model_name,
        m.size,
        b.id::BIGINT AS brand_id,
        b.brand_name
    FROM model m
    JOIN brands b ON m.brand_id = b.id
    WHERE b.id = brandId;
END;
$$ LANGUAGE plpgsql;