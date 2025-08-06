package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
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
                        rs.getDate("SALEDATE"),
                        rs.getInt("RATING"),
                        rs.getInt("STATUS")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

    public List<Sale> getSalebyAssignment(LocalDate date, int idAssignment) {
        List<Sale> sales = new ArrayList<Sale>();
        String sql = "SELECT ID_SALE, ID_ASSIGNMENT, ID_EMPLOYEE, TOTAL, " +
                "SALEDATE, RATING, STATUS FROM sale WHERE SALEDATE = ? AND ID_ASSIGNMENT = ?";
        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            ps.setInt(2, idAssignment);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(rs.getInt("ID_SALE"),
                        rs.getInt("ID_ASSIGNMENT"),
                        rs.getInt("ID_EMPLOYEE"),
                        rs.getFloat("TOTAL"),
                        rs.getDate("SALEDATE"),
                        rs.getInt("RATING"),
                        rs.getInt("STATUS")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }

}
