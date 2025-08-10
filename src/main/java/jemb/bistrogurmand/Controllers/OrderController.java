package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.OrderItem;
import jemb.bistrogurmand.utils.OrderRestaurant;
import jemb.bistrogurmand.utils.SaleCorrectionSummary;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    public int createInitialSale(int assignmentId, int employeeId) {
        String sql = "INSERT INTO SALE (ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, SALEDATE, RATING, STATUS) " +
                "VALUES (?, ?, 0, CURRENT_TIMESTAMP, 0, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID_SALE"})) {

            stmt.setInt(1, assignmentId);
            stmt.setInt(2, employeeId);

            if (stmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addItemsToSale(int saleId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) return false;

        String sql = "INSERT INTO SALEINFO (ID_SALE, ID_PRODUCT, AMOUNT) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (OrderItem item : items) {
                stmt.setInt(1, saleId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.addBatch();
            }

            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean finalizeSale(int saleId, double rating) {
        String sql = "UPDATE SALE SET RATING = ?, STATUS = 0 WHERE ID_SALE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, rating);
            stmt.setInt(2, saleId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//comineza cambios
public boolean applyCorrection(int saleId) {
    // Consultas SQL
    String getCorrectionsSql = "SELECT * FROM ORDER_CORRECTION WHERE ID_SALE = ? AND APPROVED = 0";
    String updateSql = "UPDATE SALEINFO SET AMOUNT = ? WHERE ID_SALE = ? AND ID_PRODUCT = ?";
    String insertSql = "INSERT INTO SALEINFO (ID_SALE, ID_PRODUCT, AMOUNT) VALUES (?, ?, ?)";
    String approveSql = "UPDATE ORDER_CORRECTION SET APPROVED = 1 WHERE ID_CORRECTION = ?";
    String deleteSql = "DELETE FROM SALEINFO WHERE ID_SALE = ? AND ID_PRODUCT = ?";
    String checkExistsSql = "SELECT COUNT(*) FROM SALEINFO WHERE ID_SALE = ? AND ID_PRODUCT = ?";

    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        // 1. Obtener todas las correcciones pendientes para esta venta
        List<OrderRestaurant> corrections = new ArrayList<>();
        try (PreparedStatement getStmt = conn.prepareStatement(getCorrectionsSql)) {
            getStmt.setInt(1, saleId);
            ResultSet rs = getStmt.executeQuery();

            while (rs.next()) {
                corrections.add(new OrderRestaurant(
                        rs.getInt("ID_CORRECTION"),
                        rs.getInt("ID_SALE"),
                        rs.getInt("ID_PRODUCT"),
                        rs.getInt("NEW_AMOUNT")
                ));
            }
        }

        if (corrections.isEmpty()) {
            return false; // No hay correcciones pendientes
        }

        // 2. Procesar cada corrección
        for (OrderRestaurant correction : corrections) {
            // Verificar si el producto existe en la venta
            boolean productExists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkExistsSql)) {
                checkStmt.setInt(1, correction.getID_Sale());
                checkStmt.setInt(2, correction.getID_Product());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    productExists = rs.getInt(1) > 0;
                }
            }

            if (productExists) {
                if (correction.getNew_Amount() > 0) {
                    // Actualizar cantidad existente
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, correction.getNew_Amount());
                        updateStmt.setInt(2, correction.getID_Sale());
                        updateStmt.setInt(3, correction.getID_Product());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Eliminar producto si cantidad es 0
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, correction.getID_Sale());
                        deleteStmt.setInt(2, correction.getID_Product());
                        deleteStmt.executeUpdate();
                    }
                }
            } else if (correction.getNew_Amount() > 0) {
                // Insertar nuevo producto
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, correction.getID_Sale());
                    insertStmt.setInt(2, correction.getID_Product());
                    insertStmt.setInt(3, correction.getNew_Amount());
                    insertStmt.executeUpdate();
                }
            }

            // Marcar corrección como aprobada
            try (PreparedStatement approveStmt = conn.prepareStatement(approveSql)) {
                approveStmt.setInt(1, correction.getID_Correction());
                approveStmt.executeUpdate();
            }
        }

        conn.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

    public boolean rejectCorrection(int correctionId) {
        String sql = "UPDATE ORDER_CORRECTION SET APPROVED = 2 WHERE ID_CORRECTION = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, correctionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SaleCorrectionSummary> getCorrectionSummaryByEmployeeId(int employeeId) {
        List<SaleCorrectionSummary> summaries = new ArrayList<>();
        String sql = "SELECT " +
                "  s.ID_Sale, " +
                "  MIN(s.SaleDate) AS SaleDate, " +
                "  COUNT(oc.ID_Correction) AS CorrectionCount, " +
                "  MAX(s.Total) AS OriginalTotal, " +
                "  SUM(p.Price * oc.New_Amount) AS NewTotal, " +
                "  MIN(CASE WHEN oc.Approved = 0 THEN 'Pendiente' " +
                "           WHEN oc.Approved = 1 THEN 'Aprobado' " +
                "           ELSE 'Rechazado' END) AS Status " +
                "FROM Sale s " +
                "JOIN Order_Correction oc ON s.ID_Sale = oc.ID_Sale " +
                "JOIN Product p ON oc.ID_Product = p.ID_Product " +
                "WHERE oc.ID_Employee = ? " +
                "  AND s.SaleDate >= TRUNC(SYSDATE) - 30 " + // Últimos 30 días
                "GROUP BY s.ID_Sale " +
                "ORDER BY SaleDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                summaries.add(new SaleCorrectionSummary(
                        rs.getInt("ID_Sale"),
                        rs.getTimestamp("SaleDate").toLocalDateTime(),
                        rs.getInt("CorrectionCount"),
                        rs.getDouble("OriginalTotal"),
                        rs.getDouble("NewTotal"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    public List<OrderRestaurant> getCorrectionDetailsBySaleId(int saleId) {
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