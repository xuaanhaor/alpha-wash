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