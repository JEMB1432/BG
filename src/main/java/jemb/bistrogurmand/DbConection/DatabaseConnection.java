package jemb.bistrogurmand.DbConection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL      = dotenv.get("DB_URL");
    private static final String USER     = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String WALLET_PATH = dotenv.get("DB_WALLET_PATH");

    public static Connection getConnection() throws SQLException {
        System.setProperty("oracle.net.tns_admin", WALLET_PATH);
        System.setProperty("oracle.net.ssl_server_dn_match", "true");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
