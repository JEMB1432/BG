package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.views.Admin.User;

import java.sql.*;

public class LoginController {
    public User tryLogin(String username, String password) {
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
            
            if (response.next()) {
                String userID = response.getString("ID_EMPLOYEE");
                String firstName = response.getString("NAME");
                String lastName = response.getString("LASTNAME");
                String phone = response.getString("CELPHONE");
                String email = response.getString("EMAIL");
                String rolUser = response.getString("ROL");
                String userImage = response.getString("IMAGE_URL");
                String state = response.getString("STATE");

                return new User(userID,firstName,lastName,phone,email,rolUser, userImage, state);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (response != null) response.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            DatabaseConnection.closeConnection(conn);
        }
        return null;
    }
}
