package com.alphawash.infrastructure;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlphaQuery {
    private final EntityManager entityManager;
    private final QueryLoader queryLoader;

    public <T> List<T> executeList(String queryName, Map<String, Object> params, Class<T> resultClass) {
        TypedQuery<T> query = createTypedQuery(queryName, params, resultClass);
        return query.getResultList();
    }

    public <T> Optional<T> execute(String queryName, Map<String, Object> params, Class<T> resultClass) {
        TypedQuery<T> query = createTypedQuery(queryName, params, resultClass);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private <T> TypedQuery<T> createTypedQuery(String queryName, Map<String, Object> params, Class<T> resultClass) {
        String sql = queryLoader.get(queryName);
        TypedQuery<T> query = entityManager.createQuery(sql, resultClass);

        if (params != null && !params.isEmpty()) {
            params.forEach(query::setParameter);
        }

        return query;
    }
}
