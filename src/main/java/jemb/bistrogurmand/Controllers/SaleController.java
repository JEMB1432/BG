package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.CorrectionRequest;
import jemb.bistrogurmand.utils.Sale;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleController {
    public List<Sale> getSales(LocalDate date) {
        List<Sale> sales = new ArrayList<Sale>();
        String sql = "SELECT ID_SALE, ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, " +
                "SALEDATE, RATING, STATUS FROM sale WHERE SALEDATE = ?";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(rs.getInt("ID_SALE"),
                        rs.getInt("ID_ASSIGNMENT"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getFloat("TOTAL"),
                        rs.getTimestamp("SALEDATE"),
                        rs.getInt("RATING"),
                        rs.getInt("STATUS")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

    public List<Sale> getActiveSalesbyEmployee( int idEmployee ) {
        List<Sale> sales = new ArrayList<Sale>();
        String sql = "SELECT ID_SALE, ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, " +
                "SALEDATE, RATING, STATUS FROM sale WHERE ID_EMPLOYEE = ? AND STATUS = 1";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEmployee);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(rs.getInt("ID_SALE"),
                        rs.getInt("ID_ASSIGNMENT"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getFloat("TOTAL"),
                        rs.getTimestamp("SALEDATE"),
                        rs.getInt("RATING"),
                        rs.getInt("STATUS")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

    public List<Sale> getActiveSalesByAssignment(int assignmentId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT ID_SALE, ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, " +
                "SALEDATE, RATING, STATUS FROM sale WHERE ID_ASSIGNMENT = ? AND STATUS = 1";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(
                        rs.getInt("ID_SALE"),
                        rs.getInt("ID_ASSIGNMENT"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getFloat("TOTAL"),
                        rs.getTimestamp("SALEDATE"),
                        rs.getFloat("RATING"),
                        rs.getInt("STATUS")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

    public List<CorrectionRequest> getPendingCorrections() {
        List<CorrectionRequest> corrections = new ArrayList<>();
        String sql = "SELECT c.*, p.NAME AS PRODUCT_NAME " +
                "FROM ORDER_CORRECTION c " +
                "JOIN PRODUCT p ON p.ID_PRODUCT = c.ID_PRODUCT " +
                "WHERE c.APPROVED = 0"; // 0 = pendiente

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                corrections.add(new CorrectionRequest(
                        rs.getInt("ID_CORRECTION"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getInt("ID_SALE"),
                        rs.getInt("ID_PRODUCT"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getInt("NEW_AMOUNT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return corrections;
    }

    public List<Sale> getActiveSalesByEmployeeHistory(int assignmentId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT ID_SALE, ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, " +
                "SALEDATE, RATING, STATUS FROM sale WHERE ID_EMPLOYEE = ? AND STATUS = 1 " +
                "ORDER BY SALEDATE DESC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(
                        rs.getInt("ID_SALE"),
                        rs.getInt("ID_ASSIGNMENT"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getFloat("TOTAL"),
                        rs.getTimestamp("SALEDATE"),
                        rs.getFloat("RATING"),
                        rs.getInt("STATUS")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

}
