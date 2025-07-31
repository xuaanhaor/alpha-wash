package com.alphawash.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class SequenceRepository {
    @PersistenceContext
    private EntityManager em;

    public String generateSeqCode(String code) {
        var result = em.createNativeQuery("SELECT generate_service_sequence_code(:p_code)")
                .setParameter("p_code", code)
                .getSingleResult();
        return (String) result;
    }
}
