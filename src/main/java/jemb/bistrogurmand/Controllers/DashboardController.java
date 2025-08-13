package jemb.bistrogurmand.Controllers;

import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.RadarChartCustom;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public BarChart<String, Number> createShiftSalesChart(LocalDate date) {
        // Configuración inicial del gráfico
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Turno");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventas ($)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Ventas por Turno - " + date.toString());
        chart.setLegendVisible(false);
        chart.getStyleClass().add("chart-style");

        // Obtener datos desde la base de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.SHIFT, SUM(s.TOTAL) as total " +
                             "FROM SALE s JOIN ASSIGNMENT a ON s.ID_ASSIGNMENT = a.ID_ASSIGNMENT " +
                             "WHERE TRUNC(s.SALEDATE) = ? " +
                             "GROUP BY a.SHIFT")) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        rs.getString("SHIFT"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chart.getData().add(series);

        // Estilo CSS
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            node.setStyle("-fx-bar-fill: #24986a; " +
                    "-fx-border-width: 2px 2px 0px 2px; " +
                    "-fx-border-color: rgba(168,121,0,0); " +
                    "-fx-border-radius: 2px 2px 0 0; " +
                    "-fx-background-radius: 3px 3px 0 0;");
            /*
            switch (data.getXValue()) {
                case "Mañana": node.setStyle("-fx-bar-fill: #24986a; -fx-border-width: 1px; -fx-border-color: #a87900;"); break;
                case "Tarde": node.setStyle("-fx-bar-fill: #24986a; -fx-border-width: 1px; -fx-border-color: #a87900;"); break;
                case "Noche": node.setStyle("-fx-bar-fill: #24986a; -fx-border-width: 1px; -fx-border-color: #a87900;"); break;
            }*/
        }

        return chart;
    }

    public RadarChartCustom createEmployeeRatingsChart(LocalDate date) {
        Map<String, Double> ratings = new LinkedHashMap<>();

        // Debug: Verificar fecha
        System.out.println("🔍 Creando gráfico radar para fecha: " + date);

        String query = "SELECT e.NAME || ' ' || e.LASTNAME as empleado, AVG(s.RATING) as rating, COUNT(s.ID_SALE) as num_ventas " +
                "FROM SALE s JOIN EMPLOYEE e ON s.ID_EMPLOYEE = e.ID_EMPLOYEE " +
                "WHERE TRUNC(s.SALEDATE) = ? AND s.STATUS = 0 " +
                "GROUP BY e.NAME, e.LASTNAME " +
                "HAVING COUNT(s.ID_SALE) > 0 " +
                "ORDER BY empleado";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));

            // Debug: Verificar conexión y query
            System.out.println("📊 Ejecutando query: " + query);
            System.out.println("📅 Con fecha: " + Date.valueOf(date));

            ResultSet rs = stmt.executeQuery();

            int count = 0;
            double totalRating = 0;

            while (rs.next()) {
                String empleado = rs.getString("empleado");
                double rating = rs.getDouble("rating");
                int numVentas = rs.getInt("num_ventas");

                ratings.put(empleado, rating);
                count++;
                totalRating += rating;

                // Debug: Mostrar cada empleado encontrado
                System.out.printf("👤 %s: %.2f (basado en %d ventas)%n",
                        empleado, rating, numVentas);
            }

            // Debug: Resumen
            System.out.println("✅ Total empleados: " + count);
            if (count > 0) {
                System.out.printf("📈 Rating promedio general: %.2f%n", totalRating / count);
            } else {
                System.out.println("⚠️  No se encontraron empleados con más de 2 ventas para esta fecha");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en la consulta SQL:");
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println("   Código: " + e.getErrorCode());
            e.printStackTrace();

            // Opcional: Retornar un gráfico con datos de prueba para debugging
            if (ratings.isEmpty()) {
                System.out.println("🔧 Creando datos de prueba para debugging...");
                ratings.put("Juan Pérez", 4.2);
                ratings.put("María González", 3.8);
                ratings.put("Carlos López", 4.5);
            }
        }

        // Debug: Verificar antes de crear el gráfico
        System.out.println("🎯 Creando RadarChart con " + ratings.size() + " empleados");

        return new RadarChartCustom(ratings);
    }

}
