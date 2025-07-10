package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.OrderRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderChangeController {
    public List<OrderRestaurant> getOrderRestaurants() {
        Connection conn = null;
        ResultSet response = null;
        List<OrderRestaurant> orders = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM OrderRestaurant ORDER BY ID_Correction";
            response = conn.createStatement().executeQuery(sql);

            while (response.next()) {
                int ID_Correction = response.getInt("ID_Correction");
                int ID_Employee = response.getInt("ID_Employee");
                int ID_Sale = response.getInt("ID_Sale");
                int ID_Product = response.getInt("ID_Product");

                orders.add(new OrderRestaurant(ID_Correction, ID_Employee, ID_Sale,ID_Product));
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
}