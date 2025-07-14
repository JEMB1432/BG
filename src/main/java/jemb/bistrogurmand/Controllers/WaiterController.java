package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            String sql = "SELECT ID_Employee, Name, LastName, CelPhone, Email, Rol, Image_URL, STATE FROM EMPLOYEE ORDER BY STATE DESC";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                String userID = response.getString("ID_EMPLOYEE");
                String firstName = response.getString("NAME");
                String lastName = response.getString("LASTNAME");
                String phone = response.getString("CELPHONE");
                String email = response.getString("EMAIL");
                String rolUser = response.getString("ROL");
                String userImage = response.getString("IMAGE_URL");
                String state = response.getString("STATE");

                currentWaiters.add(new User(userID, firstName, lastName, phone, email, rolUser, userImage, state));
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

    public boolean updateWaiter(User user) {
        String sql = "UPDATE EMPLOYEE SET NAME = ?, LASTNAME = ?, CELPHONE = ?, "
                + "EMAIL = ?, ROL = ?, STATE = ? WHERE ID_EMPLOYEE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRolUser());
            ps.setString(6, user.getStateUser());
            ps.setString(7, user.getUserID());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertWaiter(User user, String password) {
        String sql = "INSERT INTO EMPLOYEE (NAME, LASTNAME, CELPHONE, EMAIL, ROL, PASSWORD, CREATIONDATE, IMAGE_URL, STATE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            String imageURL = "/jemb/bistrogurmand/Icons/user.png";
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRolUser());
            ps.setString(6, password);
            ps.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            ps.setString(8, imageURL);
            ps.setString(9, user.getStateUser());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        WaiterController wc = new WaiterController();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("123456789");
        user.setEmail("John@restaurant.com");
        user.setRolUser("Mesero");
        user.setStateUser("0");
        System.out.println(wc.insertWaiter(user, "1234"));
    }

}