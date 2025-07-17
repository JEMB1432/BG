package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DayController {

    public double getAverageRating(int waiterId) {
        double rating = 0;
        //Pendiente a corregir
        String sql = "SELECT AVG( ) AS average FROM orders WHERE waiter_id = ? AND status = 'completed'";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        //Pendiente a cambios
        String sql = "SELECT COUNT(*) AS total FROM orders WHERE ID_EMPLOYEE = ? AND status = 'active'";

        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
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
        String sql = "SELECT COUNT(*) AS TOTAL FROM assignment WHERE ID_EMPLOYEE = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("TOTAL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
}

