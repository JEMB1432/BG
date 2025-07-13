package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {
    public static List<TableRestaurant> getUnassignedTablesForToday() {
        List<TableRestaurant> tables = new ArrayList<>();
        String sql = """
            SELECT tr.ID_Table, tr.NumberTable, tr.NumberSeats, tr.State, tr.Location
            FROM TableRestaurant tr
            WHERE tr.ID_Table NOT IN (
              SELECT a.ID_Table FROM Assignment a
              WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE)
            ) ORDER BY ID_Table
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TableRestaurant table;
                String ID_Table=(rs.getString("ID_Table"));
                Integer NumberTable=(rs.getInt("NumberTable"));
                Integer NumberSeats=(rs.getInt("NumberSeats"));
                String State=(rs.getString("State"));
                String Location=(rs.getString("Location"));
                tables.add(new TableRestaurant(ID_Table,NumberTable,NumberSeats,State,Location));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    public static String getTableNumberById(int tableId) {
        String query = "SELECT NumberTable FROM TableRestaurant WHERE ID_Table = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt("NumberTable"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "?";
    }

    public class EmployeeDAO {
        public static List<User> getUnassignedWaitersForToday() {
            List<User> waiters = new ArrayList<>();
            String sql = """
        SELECT * FROM Employee e
        WHERE e.Rol = 'Mesero'
          AND e.State = 1
          AND e.ID_Employee NOT IN (
              SELECT a.ID_Employee
              FROM Assignment a
              WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE)
          ) ORDER BY ID_Employee
    """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    User waiter;
                    String UserID=(rs.getString("ID_Employee"));
                    String FirstName=(rs.getString("Name")); // Asumiendo que Name es el campo
                    String LastName=(rs.getString("LastName"));
                    String CelPhone=(rs.getString("CelPhone"));
                    String Email=(rs.getString("Email"));
                    String Rol=(rs.getString("Rol"));
                    String Image=(rs.getString("Image_URL"));

                    String State=(rs.getString("State"));
                    waiters.add(new User(UserID,FirstName,LastName,CelPhone,Email,Rol,Image,State));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return waiters;
        }




        public static String getEmployeeNameById(int employeeId) {
            // Implementación de ejemplo:
            String query = "SELECT Name, LastName FROM Employee WHERE ID_Employee = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("Name") + " " + rs.getString("LastName");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Desconocido";
        }

    }

}


