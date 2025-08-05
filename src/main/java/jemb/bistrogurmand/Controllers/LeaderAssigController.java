package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.PlanificationRestaurant;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LeaderAssigController {
    public static List<PlanificationRestaurant> getCurrentAssignments() {
        List<PlanificationRestaurant> assignments = new ArrayList<>();
        String query = "SELECT * FROM Assignment WHERE dateassig = ?";
        // Implementación para obtener asignaciones del día actual
        return assignments;
    }

    public static boolean insertAssignment(int ID_Employee, int ID_Table, String shift) {
        String sql = "INSERT INTO Assignment ( ID_Employee, ID_Table, StartTime, EndTime, Favorite, dateassig, Shift) " +
                "VALUES (?, ?, ?, ?, 0, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDate today = LocalDate.now();
            LocalTime start = shiftStart(shift);
            LocalTime end = shiftEnd(shift);

            stmt.setInt(1, (ID_Employee));
            stmt.setInt(2, ID_Table);
            stmt.setTime(3, Time.valueOf(start));
            stmt.setTime(4, Time.valueOf(end));
            stmt.setDate(5, Date.valueOf(today));
            stmt.setString(6, shift);


            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("Error: Ya existe una asignación para este mesero, mesa y turno en el día de hoy.");
            } else {
                System.err.println("Error al insertar asignación: " + e.getMessage());
            }
            return false;
        }
    }

    public static List<PlanificationRestaurant> getAssignmentsForShiftAndDate(String shift, LocalDate date) {
        List<PlanificationRestaurant> assignments = new ArrayList<>();
        // Asume que tu columna de fecha es DATE o TIMESTAMP y que `TRUNC(ASSIGNMENT_DATE)` funcionará
        // para comparar solo la parte de la fecha.
        String sql = "SELECT a.ID_Assignment, a.ID_Employee, a.ID_Table" +
                ", a.StartTime, a.EndTime, a.dateassig, a.Favorite, " +
                "a.Shift,t.NumberTable " +
                "FROM Assignment a JOIN TableRestaurant t ON " +
                "a.ID_Table = t.ID_Table WHERE a.SHIFT = ? " +
                "AND TRUNC(a.dateassig) = TRUNC(TO_DATE(?, 'YYYY-MM-DD')) " +
                "ORDER BY a.ID_Assignment";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, shift);
            pstmt.setString(2, date.toString()); // Convertir LocalDate a String en formato YYYY-MM-DD

            try (ResultSet response = pstmt.executeQuery()) {
                while (response.next()) {
                    Integer ID_Assignment = response.getInt("ID_Assignment");
                    Integer ID_Employee = response.getInt("ID_Employee");
                    Integer ID_Table = response.getInt("ID_Table");
                    //String StartTime = response.getString("StartTime");
                    LocalTime startTime = response.getTime("StartTime").toLocalTime();
                    LocalTime endTime = response.getTime("EndTime").toLocalTime();
                    LocalDate dateAssig = response.getDate("dateassig").toLocalDate();
                    boolean favorite = response.getInt("Favorite") == 1;
                    String shiftA = response.getString("Shift");
                    Integer TableNumber = response.getInt("NumberTable");

                    assignments.add(new PlanificationRestaurant(ID_Assignment, ID_Employee, ID_Table, startTime, endTime, dateAssig, favorite, shiftA, TableNumber));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener asignaciones para el turno '" + shift + e.getMessage());
            // Manejo de errores más robusto
        }
        return assignments;
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

    public static boolean checkIfAssignmentExists(int ID_Employee, int ID_Table, String shift, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Assignment WHERE ID_Employee = ? AND ID_Table = ? AND Shift = ? AND TRUNC(dateassig) = TRUNC(TO_DATE(?, 'YYYY-MM-DD'))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ID_Employee);
            pstmt.setInt(2, ID_Table);
            pstmt.setString(3, shift);
            pstmt.setString(4, date.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor que 0, la asignación existe
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia de la asignación: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateAssignment(int ID_Assignment, int newID_Employee, int newID_Table, String newShift) {
        String sql = "UPDATE Assignment SET ID_Employee = ?, ID_Table = ?, Shift = ? WHERE ID_Assignment = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newID_Employee);
            stmt.setInt(2, newID_Table);
            stmt.setString(3, newShift);
            stmt.setInt(4, ID_Assignment); // Usar el ID de la asignación original para WHERE

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("La nueva combinación ya existe.");
            } else {
                System.err.println("Error al actualizar asignación con ID " + ID_Assignment + ": " + e.getMessage());
            }
            return false;
        }
    }


    public static boolean deleteAssignment(int ID_Assignment) {
        String sql = "DELETE FROM Assignment WHERE ID_Assignment = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ID_Assignment);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar asignación con ID " + ID_Assignment + ": " + e.getMessage());
            return false;
        }
    }
}
