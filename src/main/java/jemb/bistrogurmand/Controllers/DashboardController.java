package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class DashboardController {

    public BigDecimal getDailySalesTotal(LocalDate today) {
        BigDecimal total = BigDecimal.ZERO;

        String sql = "SELECT NVL(SUM(TOTAL), 0) AS TOTAL FROM SALE WHERE TRUNC(SALEDATE) = TRUNC(TO_DATE(?, 'YYYY-MM-DD'))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Formato ISO para evitar problemas con locales
            ps.setString(1, today.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Usar BigDecimal para precisión en valores monetarios
                    total = rs.getBigDecimal("TOTAL");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el total de ventas diarias: " + e.getMessage());
        }
        return total;
    }

    public int getDailyOrdersTotal(LocalDate today) {
        int total = 0;
        String sql = "SELECT COUNT(*) AS TOTAL FROM SALE WHERE TRUNC(SALEDATE) = TRUNC(TO_DATE(?, 'YYYY-MM-DD'))";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, today.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return total;
    }

}
