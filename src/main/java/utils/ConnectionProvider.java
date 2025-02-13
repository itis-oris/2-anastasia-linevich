package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProvider {

    private HikariDataSource dataSource;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/maze";
    private static final String DB_USERNAME = "anastasia";
    private static final String DB_PASSWORD = "201710202814003621";

    private ConnectionProvider() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(2000);
            config.setLeakDetectionThreshold(5000);

            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Ошибка при настройке пула соединений: " + e.getMessage());
        }
    }

    private static class SingletonHelper {
        private static final ConnectionProvider INSTANCE = new ConnectionProvider();
    }

    public static ConnectionProvider getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            System.err.println("Ошибка при получении соединения: " + ex.getMessage());
            throw ex;
        }
    }

    public void close() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Exception e) {
                System.err.println("Ошибка при закрытии пула соединений: " + e.getMessage());
            }
        }
    }
}



