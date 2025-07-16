package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlanificationController {
    public List<PlanificationRestaurant> getPlanificationRestaurants2() {
        Connection conn = null;
        ResultSet response = null;
        List<PlanificationRestaurant> tables = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Assignment ORDER BY ID_Assignment";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                Integer ID_Assignment = response.getInt("ID_Assignment");
                Integer ID_Employee = response.getInt("ID_Employee");
                Integer ID_Table = response.getInt("ID_Table");
                //String StartTime = response.getString("StartTime");
                LocalTime startTime = response.getTime("StartTime").toLocalTime();
                LocalTime endTime = response.getTime("EndTime").toLocalTime();
                LocalDate dateAssig = response.getDate("dateassig").toLocalDate();
                boolean favorite = response.getInt("Favorite") == 1;
                String shift = response.getString("Shift");




                tables.add(new PlanificationRestaurant(ID_Assignment,ID_Employee,ID_Table,startTime,endTime,dateAssig,favorite,shift));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de Tablas: " + e.getMessage());
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
        return tables;
    }

    public static List<PlanificationRestaurant> getAllAssignmentsForToday() {
        List<PlanificationRestaurant> assignmentsForToday = new ArrayList<>();
        // Consulta SQL para obtener asignaciones donde la fecha de asignación (dateassig) es HOY.
        // TRUNC(dateassig) y TRUNC(SYSDATE) comparan solo la parte de la fecha, ignorando la hora.
        String sql = "SELECT * FROM Assignment WHERE TRUNC(dateassig) = TRUNC(SYSDATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet response = pstmt.executeQuery()) {

            while (response.next()) {
                Integer ID_Assignment = response.getInt("ID_Assignment");
                Integer ID_Employee = response.getInt("ID_Employee");
                Integer ID_Table = response.getInt("ID_Table");
                //String StartTime = response.getString("StartTime");
                LocalTime startTime = response.getTime("StartTime").toLocalTime();
                LocalTime endTime = response.getTime("EndTime").toLocalTime();
                LocalDate dateAssig = response.getDate("dateassig").toLocalDate();
                boolean favorite = response.getInt("Favorite") == 1;
                String shift = response.getString("Shift");




                assignmentsForToday.add(new PlanificationRestaurant(ID_Assignment,ID_Employee,ID_Table,startTime,endTime,dateAssig,favorite,shift));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener asignaciones para hoy desde TableDAO: " + e.getMessage());
            // Considera lanzar una excepción personalizada o un manejo más robusto.
        }
        return assignmentsForToday;
    }

    // En TableDAO.java o tu clase DAO correspondiente
    public static List<PlanificationRestaurant> getPlanificationRestaurants() {
        List<PlanificationRestaurant> assignments = new ArrayList<>();
        String query = "SELECT a.ID_Assignment, a.ID_Employee, " +
                "e.Name AS EmployeeName, e.LastName AS EmployeeLastName, " +
                "a.ID_Table, t.NumberTable, a.Shift " +
                "FROM Assignment a " +
                "JOIN Employee e ON a.ID_Employee = e.ID_Employee " +
                "JOIN TableRestaurant t ON a.ID_Table = t.ID_Table " +
                "WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE) " +
                "ORDER BY CASE a.Shift WHEN 'morning' THEN 1 " +
                "WHEN 'afternoon' THEN 2 WHEN 'night' THEN 3 ELSE 4 END, e.Name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PlanificationRestaurant planif = new PlanificationRestaurant();
                planif.setID_Assignment(rs.getInt("ID_Assignment"));
                planif.setID_Employee(rs.getInt("ID_Employee"));
                planif.setEmployeeName(rs.getString("EmployeeName") + " " + rs.getString("EmployeeLastName"));
                planif.setID_Table(rs.getInt("ID_Table"));
                planif.setTableNumber(rs.getInt("NumberTable"));
                planif.setShift(rs.getString("Shift"));
                assignments.add(planif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }
}