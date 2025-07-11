package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class LeaderAssigController {

    public static void insertAssignment(String ID_Employee, int ID_Table, String shift) {
        String sql = "INSERT INTO Assignment (ID_Assignment, ID_Employee, ID_Table, StartTime, EndTime, Favorite, dateassig, Shift) " +
                "VALUES (ASSIGNMENT_SEQ.NEXTVAL, ?, ?, ?, ?, 0, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate today = LocalDate.now();
            LocalTime start = shiftStart(shift);
            LocalTime end = shiftEnd(shift);

            stmt.setInt(1, Integer.parseInt(ID_Employee));
            stmt.setInt(2, ID_Table);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            stmt.setDate(5, Date.valueOf(today));
            stmt.setString(6, shift);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static LocalTime shiftStart(String shift) {
        return switch (shift) {
            case "Mañana" -> LocalTime.of(7, 0);
            case "Tarde" -> LocalTime.of(13, 0);
            case "Noche" -> LocalTime.of(19, 0);
            default -> LocalTime.of(0, 0);
        };
    }

    private static LocalTime shiftEnd(String shift) {
        return switch (shift) {
            case "Mañana" -> LocalTime.of(13, 0);
            case "Tarde" -> LocalTime.of(19, 0);
            case "Noche" -> LocalTime.of(1, 0); // Siguiente día 1am
            default -> LocalTime.of(0, 0);
        };
    }
}
