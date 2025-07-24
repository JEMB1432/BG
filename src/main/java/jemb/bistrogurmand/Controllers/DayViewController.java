package jemb.bistrogurmand.Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jemb.bistrogurmand.DbConection.DatabaseConnection;

public class DayViewController {

    public double getAverageRating(int waiterId) {
        double rating = 0;
        // Cambiado para usar la columna correcta según tu esquema
        String sql = "SELECT AVG(Score) as avg_rating FROM Sale WHERE ID_Employee = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rating = rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rating;
    }

    public int getActiveOrdersCount(int waiterId) {
        int count = 0;
        // Corregido para usar comillas dobles en lugar de backticks
        String sql = "SELECT COUNT(*) AS order_count FROM \"Order\" o " +
                "JOIN Assignment a ON o.ID_Table = a.ID_Table " +
                "WHERE a.ID_Employee = ? AND o.Status != 'Finalizado'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("order_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getAssignedTablesCount(int waiterId) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS table_count FROM Assignment WHERE ID_Employee = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("table_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
}