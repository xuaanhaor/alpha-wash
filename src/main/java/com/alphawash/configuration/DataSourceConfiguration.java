package com.alphawash.configuration;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSourceConfiguration {

    private final DataSource dataSource;

    @PostConstruct
    public void logConnectionInfo() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("👾 JDBC Driver: {}", conn.getMetaData().getDriverName());
            log.info("🌐 URL: {}", conn.getMetaData().getURL());
            log.info("🔄 AutoCommit: {}", conn.getAutoCommit());
            log.info("🧩 Isolation Level: {}", conn.getTransactionIsolation());
            log.info("🔑 User: {}", conn.getMetaData().getUserName());
            log.info("🔗 Connection established successfully!");
        } catch (SQLException e) {
            log.error("❌ Failed to establish a connection: {}", e.getMessage());
        }
    }
}
