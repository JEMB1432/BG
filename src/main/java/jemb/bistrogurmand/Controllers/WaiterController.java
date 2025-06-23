package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.views.Admin.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WaiterController {

    public List<User> getWaitersList() {
        Connection conn = null;
        ResultSet response = null;
        List<User> currentWaiters = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ID_Employee, Name, LastName, CelPhone, Email, Rol, Image_URL FROM EMPLOYEE";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                String userID = response.getString("ID_EMPLOYEE");
                String firstName = response.getString("NAME");
                String lastName = response.getString("LASTNAME");
                String phone = response.getString("CELPHONE");
                String email = response.getString("EMAIL");
                String rolUser = response.getString("ROL");
                String userImage = response.getString("IMAGE_URL");

                currentWaiters.add(new User(userID, firstName, lastName, phone, email, rolUser, userImage));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de meseros: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return currentWaiters;
    }
}