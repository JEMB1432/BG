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
            String sql = "select ID_Product, NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION from PRODUCT";
            ps = conn.prepareStatement(sql);
            response = ps.executeQuery();
            while (response.next()) {
                int id = response.getInt("ID_Product");
                String name = response.getString("NAME");
                double price = response.getDouble("PRICE");
                String avaible = response.getString("AVAILABLE");
                String imageUrl = response.getString("IMAGE_URL");
                String description = response.getString("DESCRIPTION");

                products.add(new Product(id, name, price, avaible, imageUrl, description));
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

    public boolean updateProduct(Product product) {
        String sql = "UPDATE Product SET Name = ?, Price = ?, Available = ?, Image_URL = ?, Description = ?" +
                " WHERE ID_Product = ?";
        try{
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getAvailable());
            ps.setString(4, product.getUrlImage());
            ps.setString(5, product.getDescription());
            ps.setInt(6, product.getID_Product());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO PRODUCT (NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION )" +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getAvailable());
            ps.setString(4, product.getUrlImage());
            ps.setString(5, product.getDescription());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
