package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Assignment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssignmentController {
    public List<Assignment> getAssignments(LocalDate dateSelected) {
        Connection conn = null;
        PreparedStatement psmt = null;
        ResultSet response = null;
        List<Assignment> assignments = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT a.ID_Assignment, e.Name || ' ' || e.LastName AS Employee, t.NumberTable AS TableNumber, a.DateAssig, " +
                    "TO_CHAR(StartTime, 'HH24:MI') AS StartTime, TO_CHAR(EndTime, 'HH24:MI') AS EndTime, a.Shift, " +
                    "CASE WHEN a.Favorite = 1 THEN 'Sí' ELSE 'No' END AS Favorite " +
                    "FROM Assignment a JOIN Employee e ON a.ID_Employee = e.ID_Employee " +
                    "JOIN TableRestaurant t ON a.ID_Table = t.ID_Table " +
                    "WHERE DateAssig = ? ORDER BY a.StartTime";

            // 2025-06-29
            psmt = conn.prepareStatement(sql);
            psmt.setDate(1, Date.valueOf(dateSelected));
            response = psmt.executeQuery();

            while (response.next()) {
                String tableAssign ="Mesa " + response.getString("TableNumber");
                String employeeAssign = response.getString("Employee");
                String dateAssign = response.getString("DateAssig");
                String timeStartAssign = response.getString("StartTime");
                String timeEndAssign = response.getString("EndTime");
                String shiftAssign = response.getString("Shift");

                assignments.add(new Assignment(tableAssign,employeeAssign,dateAssign,timeStartAssign,timeEndAssign,shiftAssign));
            }
        }catch (SQLException e) {
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
        return assignments;
    }
}
