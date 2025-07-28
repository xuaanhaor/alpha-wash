package com.alphawash.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryParams {
    private final Map<String, Object> params = new LinkedHashMap<>();

    private QueryParams() {}

    public static QueryParams create(String key, Object value) {
        QueryParams qp = new QueryParams();
        qp.params.put(key, value);
        return qp;
    }

    public QueryParams and(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return params;
    }
}
