-- name: getAllOrderRaw
SELECT o.id    AS order_id,
       o.code  AS order_code,
       o.date,
       o.checkin_time,
       o.checkout_time,
       o.payment_status,
       o.payment_type,
       o.tip,
       o.vat,
       o.discount,
       o.total_price,
       o.note  AS order_note,
       o.delete_flag,
       c.id    AS customer_id,
       c.customer_name,
       c.phone,
       od.code AS order_detail_code,
       od.status,
       od.note AS order_detail_note,
       od.employee_id,
       v.id    AS vehicle_id,
       v.license_plate,
       v.image_url,
       b.id    AS brand_id,
       b.brand_name,
       b.code  AS brand_code,
       m.id    AS model_id,
       m.model_name,
       m.code  AS model_code,
       m.size,
       s.id    AS service_id,
       s.code  AS service_code,
       s.service_name,
       s.service_type_code,
       sc.id   AS service_catalog_id,
       sc.code AS service_catalog_code,
       sc.price,
       sc.size AS service_catalog_size
FROM orders o
         JOIN customer c ON c.id = o.customer_id
         JOIN order_detail od ON od.order_code = o.code
         JOIN vehicle v ON v.id = od.vehicle_id
         JOIN brands b ON b.code = v.brand_code
         JOIN model m ON m.code = v.model_code
         JOIN order_service_dtl osd ON osd.order_detail_code = od.code
         JOIN service_catalog sc ON sc.code = osd.service_catalog_code
         JOIN service s ON s.code = sc.service_code
ORDER BY o.created_at DESC;