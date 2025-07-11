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
            )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String ID_Table = rs.getString("ID_Table");
                Integer NumberTable = rs.getInt("NumberTable");
                Integer NumberSeats = rs.getInt("NumberSeats");
                String State = rs.getString("State");
                String Location  = rs.getString("Location");

                tables.add(new TableRestaurant(ID_Table, NumberTable, NumberSeats, State, Location));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    public class EmployeeDAO {

        public static List<User> getActiveWaiters() {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM Employee WHERE Rol = 'Mesero' AND State = 1";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String userID = rs.getString("ID_EMPLOYEE");
                    String firstName = rs.getString("NAME");
                    String lastName = rs.getString("LASTNAME");
                    String phone = rs.getString("CELPHONE");
                    String email = rs.getString("EMAIL");
                    String rolUser = rs.getString("ROL");
                    String userImage = rs.getString("IMAGE_URL");
                    String state = rs.getString("STATE");

                    list.add(new User(userID, firstName, lastName, phone, email, rolUser, userImage, state));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return list;
        }
    }

}
