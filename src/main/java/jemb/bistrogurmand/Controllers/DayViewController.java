
package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

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
                "ORDER BY StartTime";

        LocalTime now = LocalTime.now();
        String result = "Sin turno asignado";

        // Variables para tracking
        String currentShift = null;
        String nextShift = null;
        LocalTime nextStartTime = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, waiterId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String shift = rs.getString("SHIFT");
                    LocalTime startTime = LocalTime.parse(rs.getString("StartTime"));
                    LocalTime endTime = LocalTime.parse(rs.getString("EndTime"));

                    boolean isInShift = false;

                    // Verificar si está en turno actualmente
                    if (endTime.isBefore(startTime)) {
                        // Turno nocturno que cruza medianoche
                        isInShift = !now.isBefore(startTime) || !now.isAfter(endTime);
                    } else {
                        // Turno normal
                        isInShift = !now.isBefore(startTime) && !now.isAfter(endTime);
                    }

                    if (isInShift) {
                        currentShift = String.format("Turno activo: %s (%s - %s)",
                                capitalizeShift(shift),
                                rs.getString("StartTime"),
                                rs.getString("EndTime"));
                        break; // Si encontramos turno activo, lo priorizamos
                    }

                    // Si no está en turno, buscar el siguiente turno del día
                    if (currentShift == null && now.isBefore(startTime)) {
                        if (nextShift == null || startTime.isBefore(nextStartTime)) {
                            nextShift = String.format("Próximo turno: %s (%s - %s)",
                                    capitalizeShift(shift),
                                    rs.getString("StartTime"),
                                    rs.getString("EndTime"));
                            nextStartTime = startTime;
                        }
                    }
                }

                // Determinar qué mostrar
                if (currentShift != null) {
                    result = currentShift;
                } else if (nextShift != null) {
                    result = nextShift;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener turno";
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return "Error en formato de tiempo";
        }

        return result;
    }

    private String capitalizeShift(String shift) {
        if (shift == null || shift.isEmpty()) {
            return shift;
        }
        return shift.substring(0, 1).toUpperCase() + shift.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(new DayViewController().getAverageRating(35));
        System.out.println(new DayViewController().getActiveOrdersCount(35));
        System.out.println(new DayViewController().getAssignedTablesCount(35));
        System.out.println(new DayViewController().getShiftTime(35));
    }
}
