package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.OrderRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderChangeController {
    public List<OrderRestaurant> getOrderRestaurants() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT ID_Correction,ID_Employee,ID_Sale,ID_Product,Approved FROM Order_Correction WHERE Approved=0 ORDER BY ID_Correction";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction = response.getInt("ID_Correction");
                int ID_Employee = response.getInt("ID_Employee");
                int ID_Sale = response.getInt("ID_Sale");
                int ID_Product = response.getInt("ID_Product");
                int State=response.getInt("Approved");

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, ID_Sale,ID_Product,State));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de cambios de pedido " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return orders;
    }

    public List<OrderRestaurant> getDailyOrderCorrections() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT oc.ID_Correction, oc.ID_Employee, oc.ID_Sale, oc.ID_Product, oc.Approved, "
                    + "e.Name AS EmployeeName, e.LastName AS EmployeeLastName, p.Name AS ProductName, "
                    + "s.SaleDate "
                    + "FROM Order_Correction oc "
                    + "JOIN Employee e ON oc.ID_Employee = e.ID_Employee "
                    + "JOIN Product p ON oc.ID_Product = p.ID_Product "
                    + "JOIN Sale s ON oc.ID_Sale = s.ID_Sale "
                    + "WHERE TRUNC(s.SaleDate) = TRUNC(SYSDATE) AND (oc.Approved=0 OR oc.Approved=2) "
                    + "ORDER BY s.SaleDate DESC";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction=response.getInt("ID_Correction");
                int ID_Employee=response.getInt("ID_Employee");
                String EmployeeName=response.getString("EmployeeName") + " " + response.getString("EmployeeLastName");
                int ID_Sale=response.getInt("ID_Sale");
                int ID_Product=response.getInt("ID_Product");
                String ProductName=response.getString("ProductName");
                int State=response.getInt("Approved");
                LocalDateTime SaleDate= response.getTimestamp("SaleDate").toLocalDateTime();

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, EmployeeName,ID_Sale,ID_Product,ProductName,State,SaleDate));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de cambios de pedido " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return orders;
    }


    public boolean approveOrderCorrection(int correctionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Order_Correction SET Approved = 1 WHERE ID_Correction = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, correctionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al aprobar la corrección de pedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }


    public boolean rejectOrderCorrection(int correctionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Usamos Approved = 2 para indicar "no aprobado"
            String sql = "UPDATE Order_Correction SET Approved = 2 WHERE ID_Correction = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, correctionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al rechazar la corrección de pedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    //aqui comienza
    public void syncApprovedCorrectionWithSale(int correctionId) throws SQLException {
    String getCorrectionSql = "SELECT ID_Sale, ID_Product, Amount FROM Order_Correction WHERE ID_Correction = ?";
    String checkSaleInfoSql = "SELECT COUNT(*) FROM SaleInfo WHERE ID_Sale = ? AND ID_Product = ?";
    String insertSaleInfoSql = "INSERT INTO SaleInfo (ID_Sale, ID_Product, Amount) VALUES (?, ?, 1)";
    String updateSaleInfoSql = "UPDATE SaleInfo SET Amount = Amount + 1 WHERE ID_Sale = ? AND ID_Product = ?";
    String deleteSaleInfoSql = "DELETE FROM SaleInfo WHERE ID_Sale = ? AND ID_Product = ? AND Amount = 1";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement getCorrectionStmt = conn.prepareStatement(getCorrectionSql)) {

        getCorrectionStmt.setInt(1, correctionId);

        try (ResultSet correctionRs = getCorrectionStmt.executeQuery()) {
            if (correctionRs.next()) {
                int saleId = correctionRs.getInt("ID_Sale");
                int productId = correctionRs.getInt("ID_Product");
                double price = correctionRs.getDouble("Price");

                // Verificar si existe en SaleInfo
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSaleInfoSql)) {
                    checkStmt.setInt(1, saleId);
                    checkStmt.setInt(2, productId);

                    try (ResultSet checkRs = checkStmt.executeQuery()) {
                        if (checkRs.next() && checkRs.getInt(1) > 0) {
                            // Actualizar cantidad si ya existe
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSaleInfoSql)) {
                                updateStmt.setInt(1, saleId);
                                updateStmt.setInt(2, productId);
                                updateStmt.executeUpdate();
                            }
                        } else {
                            // Insertar nuevo registro si no existe
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSaleInfoSql)) {
                                insertStmt.setInt(1, saleId);
                                insertStmt.setInt(2, productId);
                                insertStmt.executeUpdate();
                            }
                        }
                    }
                }

                // Actualizar el total en Sale (el trigger ya se encargará)
                updateSaleTotal(saleId, conn);
            }
        }
    }
}

private void updateSaleTotal(int saleId, Connection conn) throws SQLException {
    String updateTotalSql = "UPDATE Sale SET Total = (SELECT SUM(si.Amount * p.Price) " +
                          "FROM SaleInfo si JOIN Product p ON si.ID_Product = p.ID_Product " +
                          "WHERE si.ID_Sale = ?) WHERE ID_Sale = ?";

    try (PreparedStatement stmt = conn.prepareStatement(updateTotalSql)) {
        stmt.setInt(1, saleId);
        stmt.setInt(2, saleId);
        stmt.executeUpdate();
    }
}
     //Aquitermina
}