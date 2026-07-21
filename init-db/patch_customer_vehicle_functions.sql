-- Patch: Update get_customer_vehicle_by_phone and get_customer_vehicle_by_license_plate
-- to return vehicle_id, image_url, size (11 columns) matching CustomerVehicleFlatDto constructor.
-- Run this once against the live DB.

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
                license_plate VARCHAR,
                vehicle_id    UUID,
                image_url     TEXT,
                size          VARCHAR
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
               v.license_plate,
               v.id AS vehicle_id,
               v.image_url::TEXT,
               m.size::VARCHAR AS size
        FROM customer c
                 LEFT JOIN vehicle v ON c.id = v.customer_id AND v.delete_flag = false
                 LEFT JOIN brands b ON v.brand_code = b.code
                 LEFT JOIN model m ON v.model_code = m.code
        WHERE c.phone ILIKE '%' || REPLACE(REPLACE(p_customer_phone, '%', '\%'), '_', '\_') || '%'
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
                license_plate VARCHAR,
                vehicle_id    UUID,
                image_url     TEXT,
                size          VARCHAR
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
               v.license_plate,
               v.id AS vehicle_id,
               v.image_url::TEXT,
               m.size::VARCHAR AS size
        FROM customer c
                 LEFT JOIN vehicle v ON c.id = v.customer_id AND v.delete_flag = false
                 LEFT JOIN brands b ON v.brand_code = b.code
                 LEFT JOIN model m ON v.model_code = m.code
        WHERE v.license_plate ILIKE '%' || REPLACE(REPLACE(p_customer_license_plate, '%', '\%'), '_', '\_') || '%'
          AND v.delete_flag = false
          AND c.delete_flag = false;
END;
$$ LANGUAGE plpgsql;
