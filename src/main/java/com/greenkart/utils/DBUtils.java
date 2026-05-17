package com.greenkart.utils;

import com.greenkart.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database utility for running queries against a MySQL database.
 * Connection details come from config.properties.
 * The driver class handles connection lifecycle; callers must invoke close().
 */
public class DBUtils {

    private static final Logger log = LogManager.getLogger(DBUtils.class);
    private Connection connection;

    public void connect() throws SQLException {
        String url  = ConfigReader.get("db.url",      "jdbc:mysql://localhost:3306/greenkart");
        String user = ConfigReader.get("db.username", "root");
        String pass = ConfigReader.get("db.password", "");
        connection = DriverManager.getConnection(url, user, pass);
        log.info("DB connection established to: {}", url);
    }

    /**
     * Executes a SELECT query and returns rows as a list of column→value maps.
     */
    public List<Map<String, String>> executeQuery(String sql) throws SQLException {
        List<Map<String, String>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnLabel(i), rs.getString(i));
                }
                results.add(row);
            }
        }
        log.info("Query returned {} rows: {}", results.size(), sql);
        return results;
    }

    /**
     * Executes an INSERT/UPDATE/DELETE query and returns rows affected.
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            log.info("Update affected {} rows: {}", rows, sql);
            return rows;
        }
    }

    /**
     * Returns a single cell value from a query result.
     */
    public String getSingleValue(String sql, String columnLabel) throws SQLException {
        List<Map<String, String>> rows = executeQuery(sql);
        if (!rows.isEmpty()) return rows.get(0).get(columnLabel);
        return null;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                log.info("DB connection closed");
            } catch (SQLException e) {
                log.warn("Error closing DB connection", e);
            }
        }
    }
}
