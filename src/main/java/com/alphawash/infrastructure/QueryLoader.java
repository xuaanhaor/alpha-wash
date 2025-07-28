package com.alphawash.infrastructure;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class QueryLoader {
    private final Map<String, String> queries = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:queries/*.sql");

            for (Resource resource : resources) {
                loadSql(resource);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL query files", e);
        }
    }

    private void loadSql(Resource resource) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            String queryName = null;
            StringBuilder queryBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("-- name:")) {
                    if (queryName != null && !queryBuilder.isEmpty()) {
                        queries.put(queryName, queryBuilder.toString().trim());
                        queryBuilder.setLength(0);
                    }
                    queryName = line.substring(8).trim();
                } else if (!line.startsWith("--")) {
                    queryBuilder.append(line).append("\n");
                }
            }
            if (queryName != null && !queryBuilder.isEmpty()) {
                queries.put(queryName, queryBuilder.toString().trim());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read SQL file: " + resource.getFilename(), e);
        }
    }

    public String get(String queryName) {
        if (!queries.containsKey(queryName)) {
            throw new IllegalArgumentException("Query not found: " + queryName);
        }
        return queries.get(queryName);
    }
}
