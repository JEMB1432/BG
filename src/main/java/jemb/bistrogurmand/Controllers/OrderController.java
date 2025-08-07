package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.OrderItem;

import java.sql.*;
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

    public boolean applyCorrection(int correctionId) {
        // 1. Obtener los datos de la corrección
        String getSql = "SELECT * FROM ORDER_CORRECTION WHERE ID_CORRECTION = ?";
        String updateSql = "UPDATE SALEINFO SET AMOUNT = ? WHERE ID_SALE = ? AND ID_PRODUCT = ?";
        String approveSql = "UPDATE ORDER_CORRECTION SET APPROVED = 1 WHERE ID_CORRECTION = ?";
        String deleteSql = "DELETE FROM SALEINFO WHERE ID_SALE = ? AND ID_PRODUCT = ? AND AMOUNT = 0";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Obtener datos de corrección
            try (PreparedStatement getStmt = conn.prepareStatement(getSql)) {
                getStmt.setInt(1, correctionId);
                ResultSet rs = getStmt.executeQuery();

                if (rs.next()) {
                    int saleId = rs.getInt("ID_SALE");
                    int productId = rs.getInt("ID_PRODUCT");
                    int newAmount = rs.getInt("NEW_AMOUNT");

                    // Actualizar cantidad en SALEINFO
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newAmount);
                        updateStmt.setInt(2, saleId);
                        updateStmt.setInt(3, productId);
                        updateStmt.executeUpdate();
                    }

                    // Si la nueva cantidad es 0, eliminar el producto
                    if (newAmount == 0) {
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, saleId);
                            deleteStmt.setInt(2, productId);
                            deleteStmt.executeUpdate();
                        }
                    }

                    // Marcar corrección como aprobada
                    try (PreparedStatement approveStmt = conn.prepareStatement(approveSql)) {
                        approveStmt.setInt(1, correctionId);
                        approveStmt.executeUpdate();
                    }

                    conn.commit();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectCorrection(int correctionId) {
        String sql = "UPDATE ORDER_CORRECTION SET APPROVED = 2 WHERE ID_CORRECTION = ?"; // 2 = rechazado

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, correctionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}