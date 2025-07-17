package jemb.bistrogurmand.controllers;

import jemb.bistrogurmand.dbconnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DayController {

    private final Connection conn;

    public DayController() {
        this.conn = DatabaseConnection.getConnection();
    }

    public double getAverageRating(int waiterId) {
        double rating = 0;
        String sql = "SELECT AVG(rating) AS average FROM orders WHERE waiter_id = ? AND status = 'completed'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rating = rs.getDouble("average");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Math.round(rating * 10.0) / 10.0; // redondeo a 1 decimal
    }

    public int getActiveOrdersCount(int waiterId) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM orders WHERE waiter_id = ? AND status = 'active'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getAssignedTablesCount(int waiterId) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM assignments WHERE waiter_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
}

