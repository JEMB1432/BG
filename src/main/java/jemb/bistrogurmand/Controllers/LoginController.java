package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.*;

public class LoginController {
    public boolean TryLogin(String username, String password) {
        boolean isValid = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet response = null;

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM EMPLOYEE WHERE EMAIL = ? AND PASSWORD = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2,password);
            response = pstmt.executeQuery();

            while (response.next()) {
                isValid = true;
                //System.out.println("HOLA" + response.getString("EMAIL") + " - " + response.getString("PASSWORD"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (response != null) response.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            DatabaseConnection.closeConnection(conn);
        }
        return isValid;
    }
}
