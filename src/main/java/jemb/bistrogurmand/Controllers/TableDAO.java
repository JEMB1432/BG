package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {
    // Dentro de jemb.bistrogurmand.Controllers.TableDAO

    public static List<TableRestaurant> getAllTables() {
        List<TableRestaurant> tables = new ArrayList<>();
        String sql = "SELECT tr.ID_Table, tr.NumberTable, tr.NumberSeats, tr.State, tr.Location" +
                "            FROM TableRestaurant tr" +
                "            ORDER BY ID_Table"; // Asegúrate de que esta es tu tabla de mesas
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
            System.err.println("Error al obtener todas las mesas: " + e.getMessage());
            // Manejo de errores más robusto aquí
        }
        return tables;
    }

    public static List<TableRestaurant> getUnassignedTablesForToday() {
        List<TableRestaurant> tables = new ArrayList<>();
        String sql = """
            SELECT tr.ID_Table, tr.NumberTable, tr.NumberSeats, tr.State, tr.Location
            FROM TableRestaurant tr
            WHERE tr.ID_Table NOT IN (
              SELECT a.ID_Table FROM Assignment a
              --WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE)
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
        String query = "SELECT tr.NumberTable FROM TableRestaurant tr WHERE tr.ID_Table = ? " +
                "AND tr.ID_Table IN (SELECT a.ID_Table FROM Assignment a " +
                "WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE))";
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
    }/*

    public static List<String> getTableNumberById(int tableId) {
        List<String> tables = new ArrayList<>();
        String query = "SELECT tr.NumberTable FROM TableRestaurant tr WHERE tr.ID_Table = ? " +
                "AND tr.ID_Table IN (SELECT a.ID_Table FROM Assignment a " +
                "WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                tables.add(String.valueOf(rs.getInt("NumberTable")));
            }
            /*
            if (rs.next()) {
                return String.valueOf(rs.getInt("NumberTable"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }*/
/*
    public static List<PlanificationRestaurant> getAllAssignmentsForToday() {
        List<PlanificationRestaurant> assignmentsForToday = new ArrayList<>();
        // Consulta SQL para obtener asignaciones donde la fecha de asignación (dateassig) es HOY.
        // TRUNC(dateassig) y TRUNC(SYSDATE) comparan solo la parte de la fecha, ignorando la hora.
        String sql = "SELECT ID_Assignment, ID_Employee, ID_Table, Shift FROM Assignment WHERE TRUNC(dateassig) = TRUNC(SYSDATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idAssignment = rs.getInt("ID_Assignment");
                int idEmployee = rs.getInt("ID_Employee");
                int idTable = rs.getInt("ID_Table");
                String shift = rs.getString("Shift");

                // Asume que tu clase PlanificationRestaurant tiene un constructor adecuado
                // Si PlanificationRestaurant necesita más campos o el constructor es diferente, ajústalo aquí.
                PlanificationRestaurant plan = new PlanificationRestaurant(idAssignment, idEmployee, idTable, shift);
                assignmentsForToday.add(plan);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener asignaciones para hoy desde TableDAO: " + e.getMessage());
            // Considera lanzar una excepción personalizada o un manejo más robusto.
        }
        return assignmentsForToday;
    }*/


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
              --WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE)
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



//
        public static String getEmployeeNameById(int employeeId) {
            // Implementación de ejemplo:
            String query= "SELECT e.Name, e.LastName FROM Employee e WHERE e.ID_Employee = ? " +
                    "AND e.ID_Employee IN (SELECT a.ID_Employee FROM Assignment a " +
                    "WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE))";
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
        }/*
public static List<String> getEmployeeNameById(int employeeId) {
    List <String> waiters= new ArrayList<>();
    // Implementación de ejemplo:
    String query= "SELECT e.Name, e.LastName FROM Employee e WHERE e.ID_Employee = ? " +
            "AND e.ID_Employee IN (SELECT a.ID_Employee FROM Assignment a " +
            "WHERE TRUNC(a.dateassig) = TRUNC(SYSDATE))";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, employeeId);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            waiters.add((rs.getString("Name") + " " + rs.getString("LastName")));
        }
        /*if (rs.next()) {
            return rs.getString("Name") + " " + rs.getString("LastName");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return waiters;
}*/

        // Dentro de jemb.bistrogurmand.Controllers.TableDAO.EmployeeDAO

        public static List<User> getAllWaiters() {
            List<User> waiters = new ArrayList<>();
            String sql = """
        SELECT * FROM Employee e
        WHERE e.Rol = 'Mesero'
          AND e.State = 1 ORDER BY ID_Employee
    """;
            try (Connection conn = DatabaseConnection.getConnection(); // Asume que tienes una clase DBConnection para manejar la conexión
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

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
                System.err.println("Error al obtener todos los meseros: " + e.getMessage());
                // Manejo de errores más robusto aquí
            }
            return waiters;
        }

    }

}


