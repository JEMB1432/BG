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
}