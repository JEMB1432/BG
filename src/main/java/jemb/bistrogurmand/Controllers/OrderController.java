// src/jemb/bistrogurmand/Controllers/OrderController.java
package jemb.bistrogurmand.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.modals.Category;
import jemb.bistrogurmand.utils.modals.OrderItem;
import jemb.bistrogurmand.utils.modals.Product;
import jemb.bistrogurmand.utils.UserSession;

import java.sql.*;
import java.util.*;

public class OrderController {
    private final ObservableList<OrderItem> currentOrder = FXCollections.observableArrayList();
    private final Map<String, OrderItem> lookup       = new HashMap<>();
    private final Map<String, String> observations    = new HashMap<>();
    private final String tableId;

    // Constructor que recibe el ID de la mesa
    public OrderController(String tableId) {
        this.tableId = tableId;
    }

    public ObservableList<OrderItem> getCurrentOrder() {
        return currentOrder;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT ID_Category, Name FROM Category ORDER BY Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getString("ID_Category"),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Product> getProductsByCategory(String categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT ID_Product, Name, Price FROM Product WHERE ID_Category = ? ORDER BY Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getString("ID_Product"),
                            rs.getString("Name"),
                            rs.getDouble("Price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void addItem(Product p, int qty) {
        String key = p.getId();
        if (lookup.containsKey(key)) {
            OrderItem item = lookup.get(key);
            item.setQuantity(item.getQuantity() + qty);
        } else {
            OrderItem item = new OrderItem(p, qty);
            lookup.put(key, item);
            currentOrder.add(item);
        }
    }

    public void addObservation(String productId, String obs) {
        observations.put(productId, obs);
    }

    public void removeItem(OrderItem item) {
        lookup.remove(item.getProduct().getId());
        observations.remove(item.getProduct().getId());
        currentOrder.remove(item);
    }

    public void sendOrder() {
        String insertOrderSql  = "INSERT INTO ORDERS (ID_Table, ID_Employee, DATE_Order) VALUES (?, ?, ?)";
        String insertDetailSql = "INSERT INTO ORDER_DETAILS (ID_Order, ID_Product, Quantity, Observation) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1) Insertar cabecera y obtener ID generado
            long orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, tableId);
                ps.setString(2, UserSession.getCurrentUser().getUserID());
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getLong(1);
                    } else {
                        throw new SQLException("No se generó ID de pedido");
                    }
                }
            }

            // 2) Insertar detalles
            try (PreparedStatement ps2 = conn.prepareStatement(insertDetailSql)) {
                for (OrderItem item : currentOrder) {
                    ps2.setLong   (1, orderId);
                    ps2.setString (2, item.getProduct().getId());
                    ps2.setInt    (3, item.getQuantity());
                    ps2.setString (4, observations.get(item.getProduct().getId()));
                    ps2.addBatch();
                }
                ps2.executeBatch();
            }

            conn.commit();
            // 3) Limpiar pedido en memoria
            currentOrder.clear();
            lookup.clear();
            observations.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public String getCurrentTable() {
        return tableId;
    }
}
