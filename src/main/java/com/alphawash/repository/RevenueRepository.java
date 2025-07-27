package com.alphawash.repository;

import com.alphawash.dto.FavoriteServiceRevenueDto;
import com.alphawash.dto.RevenueDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class RevenueRepository {
    @PersistenceContext
    private EntityManager em;

    public List<RevenueDto> getRevenue(LocalDate startDate, LocalDate endDate, String orderStatus) {
        List<Tuple> tuples = em.createNativeQuery(
                        "SELECT * FROM get_daily_revenue_by_service_type_full_range(:p_start_date, :p_end_date, :p_order_status)",
                        Tuple.class)
                .setParameter("p_start_date", startDate)
                .setParameter("p_end_date", endDate)
                .setParameter("p_order_status", orderStatus)
                .getResultList();
        return tuples.stream()
                .map(t -> new RevenueDto(
                        ((Date) t.get("order_date")).toLocalDate(),
                        (String) t.get("service_type_code"),
                        (String) t.get("service_name"),
                        (String) t.get("service_type_name"),
                        new BigDecimal(t.get("net_revenue").toString()),
                        new BigDecimal(t.get("gross_revenue").toString())))
                .toList();
    }

    public List<FavoriteServiceRevenueDto> getFavoriteServiceRevenue(LocalDate startDate, LocalDate endDate) {
        List<Tuple> tuples = em.createNativeQuery(
                        "SELECT * FROM get_favorite_service(:p_start_date, :p_end_date)", Tuple.class)
                .setParameter("p_start_date", startDate)
                .setParameter("p_end_date", endDate)
                .getResultList();
        return tuples.stream()
                .map(t -> new FavoriteServiceRevenueDto(
                        (String) t.get("service_code"),
                        (String) t.get("service_name"),
                        (Long) t.get("usage_count"),
                        new BigDecimal(t.get("total_revenue").toString())))
                .toList();
    }
}
