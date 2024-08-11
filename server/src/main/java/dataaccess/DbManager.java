package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DbManager {
    private static final String DB_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /**
     * Load the database info
     */
    static {
        try {
            try (var streamProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                var properties = new Properties();
                properties.load(streamProperties);
                DB_NAME = properties.getProperty("db.name");
                USER = properties.getProperty("db.user");
                PASSWORD = properties.getProperty("db.password");

                var host = properties.getProperty("db.host");
                var port = Integer.parseInt(properties.getProperty("db.port"));

                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading database properties: " + e.getMessage());
        }
    }

    /**
     * Creates the database if it does not exist
     */
    static void createDatabase() throws DataAccessException {
        try {
            var sqlStatement = String.format("CREATE DATABASE IF NOT EXISTS %s", DB_NAME);
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(sqlStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    /**
     * Connects to the database
     * @return the connection
     * @throws DataAccessException if there is an error connecting to the database
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DB_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database: " + e.getMessage());
        }
    }
}
