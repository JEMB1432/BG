package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class DayViewController {

    public double getAverageRating(int waiterId) {
        double rating = 0.0;
        String sql = "SELECT AVG(RATING) as avg_rating FROM SALE WHERE ID_Employee = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rating = rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rating;
    }

    public int getActiveOrdersCount(int waiterId) {
        int count = 0;
        //STATUS 0 representa finalizado, 1 Activo
        String sql = "SELECT COUNT(*) AS TOTAL_ORDERS FROM SALE " +
                " WHERE ID_EMPLOYEE = ? AND STATUS  = 1";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("TOTAL_ORDERS");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getAssignedTablesCount(int waiterId) {
        int count = 0;
        LocalDate today = LocalDate.now();
        String sql = "SELECT COUNT(*) AS table_count FROM Assignment WHERE ID_Employee = ? AND DATEASSIG = ? ";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, waiterId);
            stmt.setDate(2, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("table_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public String getShiftTime(int waiterId) {
        String sql = "SELECT SHIFT, TO_CHAR(StartTime, 'HH24:MI') AS StartTime, TO_CHAR(EndTime, 'HH24:MI') AS EndTime " +
                "FROM Assignment WHERE ID_Employee = ? AND DATEASSIG = ? " +
                "ORDER BY StartTime"; // Ordenamos por hora de inicio

        LocalTime now = LocalTime.now();
        String currentShift = "Sin turno asignado";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, waiterId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String shift = rs.getString("SHIFT");
                LocalTime startTime = LocalTime.parse(rs.getString("StartTime"));
                LocalTime endTime = LocalTime.parse(rs.getString("EndTime"));

                // Manejo especial para turno de noche que cruza medianoche
                if (endTime.isBefore(startTime)) {
                    if (now.isAfter(startTime) || now.isBefore(endTime)) {
                        currentShift = String.format("Turno: %s (%s - %s)",
                                capitalizeShift(shift),
                                rs.getString("StartTime"),
                                rs.getString("EndTime"));
                        break;
                    }
                }
                // Para turnos normales
                else if (now.isAfter(startTime) && now.isBefore(endTime)) {
                    currentShift = String.format("Turno: %s (%s - %s)",
                            capitalizeShift(shift),
                            rs.getString("StartTime"),
                            rs.getString("EndTime"));
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener turno";
        }

        return currentShift;
    }

    // Método auxiliar para capitalizar el nombre del turno
    private String capitalizeShift(String shift) {
        if (shift == null || shift.isEmpty()) {
            return shift;
        }
        return shift.substring(0, 1).toUpperCase() + shift.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(new DayViewController().getAverageRating(41));
        System.out.println(new DayViewController().getActiveOrdersCount(41));
        System.out.println(new DayViewController().getAssignedTablesCount(41));
        System.out.println(new DayViewController().getShiftTime(41));
    }
}
