package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductController {
    public List<Product> getProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet response = null;
        List<Product> products = new ArrayList<>();

        try{
            conn = DatabaseConnection.getConnection();
            String sql = "select NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION from PRODUCT";
            ps = conn.prepareStatement(sql);
            response = ps.executeQuery();
            while (response.next()) {
                String name = response.getString("NAME");
                double price = response.getDouble("PRICE");
                String avaible = response.getString("AVAILABLE");
                String imageUrl = response.getString("IMAGE_URL");
                String description = response.getString("DESCRIPTION");

                products.add(new Product(name, price, avaible, imageUrl, description));
            }

        }catch (SQLException e) {
            System.err.println("Error al obtener la lista de Tablas: " + e.getMessage());
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

        return products;
    }
}
