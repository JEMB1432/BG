package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class LoginController {
    public User tryLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet response = null;

        try {
            conn = DatabaseConnection.getConnection();
            // Solo busca por email (no incluyas la contraseña en la consulta)
            String sql = "SELECT * FROM EMPLOYEE WHERE EMAIL = ? AND STATE = '1'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            response = pstmt.executeQuery();

            if (response.next()) {
                // Obtener el hash almacenado
                String storedHash = response.getString("PASSWORD");

                // Verificar la contraseña proporcionada contra el hash
                if (BCrypt.checkpw(password, storedHash)) {
                    String userID = response.getString("ID_EMPLOYEE");
                    String firstName = response.getString("NAME");
                    String lastName = response.getString("LASTNAME");
                    String phone = response.getString("CELPHONE");
                    String email = response.getString("EMAIL");
                    String rolUser = response.getString("ROL");
                    String userImage = response.getString("IMAGE_URL");
                    String state = response.getString("STATE");

                    return new User(userID, firstName, lastName, phone, email,
                            rolUser, userImage, state);
                }
            }

            // Si llegamos aquí, las credenciales son inválidas
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try { if (response != null) response.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            DatabaseConnection.closeConnection(conn);
        }
    }
}