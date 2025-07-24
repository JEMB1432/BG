package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Modals.AssignedTable;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AssignedTableController {

    public List<AssignedTable> getAssignedTablesByWaiter(int waiterId) {
        List<AssignedTable> tables = new ArrayList<>();

        String query = "SELECT a.ID_Table, t.Location, a.Shift, a.StartTime, a.EndTime, a.DateAssig, a.Favorite " +
                "FROM Assignment a " +
                "JOIN TableRestaurant t ON a.ID_Table = t.ID_Table " +
                "WHERE a.ID_Employee = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tableNumber = rs.getInt("ID_Table");
                String location = rs.getString("Location");
                String shift = rs.getString("Shift");
                LocalTime start = rs.getTime("StartTime").toLocalTime();
                LocalTime end = rs.getTime("EndTime").toLocalTime();
                LocalDate date = rs.getDate("DateAssig").toLocalDate();
                boolean favorite = rs.getInt("Favorite") == 1;

                tables.add(new AssignedTable(tableNumber, location, shift, start, end, date, favorite));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }
}
