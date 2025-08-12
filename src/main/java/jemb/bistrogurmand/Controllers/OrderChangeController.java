package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.OrderRestaurant;
import jemb.bistrogurmand.utils.SaleCorrectionSummary;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderChangeController {
    public List<OrderRestaurant> getOrderRestaurants() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ID_Correction,ID_Employee,ID_Sale,ID_Product,Approved FROM Order_Correction WHERE Approved=0 ORDER BY ID_Correction";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction = response.getInt("ID_Correction");
                int ID_Employee = response.getInt("ID_Employee");
                int ID_Sale = response.getInt("ID_Sale");
                int ID_Product = response.getInt("ID_Product");
                int State=response.getInt("Approved");

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, ID_Sale,ID_Product,State));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de cambios de pedido " + e.getMessage());
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
        return orders;
    }

    public List<OrderRestaurant> getDailyOrderCorrectionsOG() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT oc.ID_Correction, oc.ID_Employee, oc.ID_Sale, oc.ID_Product, oc.Approved, "
                    + "e.Name AS EmployeeName, e.LastName AS EmployeeLastName, p.Name AS ProductName, "
                    + "s.SaleDate "
                    + "FROM Order_Correction oc "
                    + "JOIN Employee e ON oc.ID_Employee = e.ID_Employee "
                    + "JOIN Product p ON oc.ID_Product = p.ID_Product "
                    + "JOIN Sale s ON oc.ID_Sale = s.ID_Sale "
                    + "WHERE TRUNC(s.SaleDate) = TRUNC(SYSDATE) AND (oc.Approved=0 OR oc.Approved=2) "
                    + "ORDER BY s.SaleDate DESC";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction=response.getInt("ID_Correction");
                int ID_Employee=response.getInt("ID_Employee");
                String EmployeeName=response.getString("EmployeeName") + " " + response.getString("EmployeeLastName");
                int ID_Sale=response.getInt("ID_Sale");
                int ID_Product=response.getInt("ID_Product");
                String ProductName=response.getString("ProductName");
                int State=response.getInt("Approved");
                LocalDateTime SaleDate= response.getTimestamp("SaleDate").toLocalDateTime();

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, EmployeeName,ID_Sale,ID_Product,ProductName,State,SaleDate));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de cambios de pedido " + e.getMessage());
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
        return orders;
    }


    public boolean approveOrderCorrection(int correctionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Order_Correction SET Approved = 1 WHERE ID_Sale = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, correctionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al aprobar la corrección de pedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }


    public boolean rejectOrderCorrection(int saleId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT COUNT(*) FROM Order_Correction WHERE ID_Sale = ? AND Approved = 0";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, saleId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                return false;
            }

            String updateSql = "UPDATE Order_Correction SET Approved = 2 WHERE ID_Sale = ? AND Approved = 0";
            PreparedStatement pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, saleId);
            int rowsAffected = pstmt.executeUpdate();

            conn.commit();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al rechazar correcciones de pedido: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<OrderRestaurant> getDailyOrderCorrections() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT s.ID_Sale, MIN(s.SaleDate) AS SaleDate, MIN(oc.ID_Correction) AS ID_Correction, " +
                    "MIN(oc.ID_Employee) AS ID_Employee, MIN(e.Name || ' ' || e.LastName) AS EmployeeName, " +
                    "MIN(oc.ID_Product) AS ID_Product, MIN(p.Name) AS ProductName, MIN(oc.Approved) AS Approved, " +
                    "COUNT(oc.ID_Correction) AS CorrectionCount, " +
                    "MAX(s.Total) AS OriginalTotal, SUM(p.Price * oc.New_Amount) AS NewTotal, MIN(CASE WHEN oc.Approved = 0 THEN 'Pendiente' " +
                    "WHEN oc.Approved = 1 THEN 'Aprobado' ELSE 'Rechazado' END) AS Status FROM Sale s JOIN Order_Correction oc ON s.ID_Sale = oc.ID_Sale " +
                    "JOIN Product p ON oc.ID_Product = p.ID_Product " +
                    "JOIN Employee e ON oc.ID_Employee = e.ID_Employee " +
                    "AND s.SaleDate >= TRUNC(SYSDATE) - 30 GROUP BY s.ID_Sale ORDER BY SaleDate DESC";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction=response.getInt("ID_Correction");
                int ID_Employee=response.getInt("ID_Employee");
                String EmployeeName=response.getString("EmployeeName");
                int ID_Sale=response.getInt("ID_Sale");
                int ID_Product=response.getInt("ID_Product");
                String ProductName=response.getString("ProductName");
                int State=response.getInt("Approved");
                LocalDateTime SaleDate= response.getTimestamp("SaleDate").toLocalDateTime();
                int CorrectionCount = response.getInt("CorrectionCount");
                        double OriginalTotal=response.getDouble("OriginalTotal");
                        double NewTotal=response.getDouble("NewTotal");
                        String Status=response.getString("Status");

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, EmployeeName, ID_Sale,ID_Product,ProductName,State,SaleDate,CorrectionCount,OriginalTotal,NewTotal,Status));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de cambios de pedido " + e.getMessage());
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
        return orders;
    }

    public static List<OrderRestaurant> getCorrectionDetailsBySaleId2(int saleId) {
        List<OrderRestaurant> details = new ArrayList<>();
        String sql = "SELECT oc.ID_Correction, oc.ID_Employee, oc.ID_Sale, oc.ID_Product, oc.Approved, " +
                "e.Name AS EmployeeName, e.LastName AS EmployeeLastName, p.Name AS ProductName, " +
                "s.SaleDate " +
                "FROM Order_Correction oc " +
                "JOIN Employee e ON oc.ID_Employee = e.ID_Employee " +
                "JOIN Product p ON oc.ID_Product = p.ID_Product " +
                "JOIN Sale s ON oc.ID_Sale = s.ID_Sale " +
                "WHERE oc.ID_Sale = ? " +
                "ORDER BY s.SaleDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                details.add(new OrderRestaurant(
                        rs.getInt("ID_Correction"),
                        rs.getInt("ID_Employee"),
                        rs.getString("EmployeeName") + " " + rs.getString("EmployeeLastName"),
                        rs.getInt("ID_Sale"),
                        rs.getInt("ID_Product"),
                        rs.getString("ProductName"),
                        rs.getInt("Approved"),
                        rs.getTimestamp("SaleDate").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}