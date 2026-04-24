package com.rentalsystem.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                logger.error("config.properties not found!");
                throw new RuntimeException("Configuration file not found");
            }
            props.load(is);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.poolSize", "10")));
            
            // Optimization settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            
            // Auto-initialize schema for H2
            if (props.getProperty("db.url").contains("h2")) {
                initializeSchema();
            }
            
            logger.info("Database connection pool initialized successfully.");
        } catch (IOException e) {
            logger.error("Failed to load database configuration", e);
            throw new RuntimeException(e);
        }
    }

    private static void initializeSchema() {
        try (Connection conn = dataSource.getConnection();
             InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("setup.sql")) {
            if (is == null) {
                // Fallback to project root if resource not found (manual run)
                logger.warn("setup.sql not in resources, skipping auto-init");
                return;
            }
            String schema = new String(is.readAllBytes());
            try (Statement st = conn.createStatement()) {
                st.execute(schema);
                logger.info("Database schema initialized successfully.");
            }
        } catch (Exception e) {
            logger.error("Schema initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Database connection pool shut down.");
        }
    }
}
