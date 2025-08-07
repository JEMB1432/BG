package jemb.bistrogurmand.Controllers;

import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Category;
import jemb.bistrogurmand.utils.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ProductController {
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT ID_PRODUCT, NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION FROM PRODUCT";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet response = ps.executeQuery()) {

            while (response.next()) {
                products.add(mapResultSetToProduct(response));
            }
        } catch (SQLException e) {
            handleException("Error al obtener la lista de productos", e);
        }
        return products;
    }

    public  List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT ID_PRODUCT, NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION FROM PRODUCT WHERE AVAILABLE = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet response = ps.executeQuery()) {

            while (response.next()) {
                products.add(mapResultSetToProduct(response));
            }
        } catch (SQLException e) {
            handleException("Error al obtener la lista de productos", e);
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        if (!isValidProductForUpdate(product)) {
            return false;
        }

        if (!productExists(product.getID_Product())) {
            System.err.println("El producto con ID " + product.getID_Product() + " no existe");
            return false;
        }

        String sql = "UPDATE PRODUCT SET NAME = ?, PRICE = ?, AVAILABLE = ?, IMAGE_URL = ?, DESCRIPTION = ? WHERE ID_PRODUCT = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setProductParameters(ps, product);
            ps.setInt(6, product.getID_Product());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error al actualizar el producto con ID: " + product.getID_Product(), e);
            return false;
        }
    }

    public boolean addProduct(Product product) {
        if (!isValidProductForInsert(product)) {
            return false;
        }

        String sql = "INSERT INTO PRODUCT (NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setProductParameters(ps, product);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error al agregar el producto: " + product.getName(), e);
            return false;
        }
    }

    public boolean addProductWithCategories(Product product, List<Category> categories) {
        if (!isValidProductForInsert(product)) {
            return false;
        }

        if (categories == null || categories.isEmpty()) {
            System.err.println("La lista de categorías no puede estar vacía");
            return false;
        }

        if (!areValidCategories(categories)) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Insertar producto y obtener ID
                int productId = insertProduct(conn, product);
                if (productId <= 0) {
                    conn.rollback();
                    System.err.println("Error al insertar el producto, no se pudo obtener el ID generado");
                    return false;
                }

                // 2. Insertar relaciones con categorías
                if (!insertProductCategories(conn, productId, categories)) {
                    conn.rollback();
                    System.err.println("Error al insertar las categorías del producto");
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            handleException("Error al agregar producto con categorías: " + product.getName(), e);
            return false;
        }
    }

    public boolean updateProductWithCategories(Product product, List<Category> newCategories) {
        if (!isValidProductForUpdate(product)) {
            return false;
        }

        if (newCategories == null || newCategories.isEmpty()) {
            System.err.println("La lista de categorías no puede estar vacía");
            return false;
        }

        if (!areValidCategories(newCategories)) {
            return false;
        }

        if (!productExists(product.getID_Product())) {
            System.err.println("El producto con ID " + product.getID_Product() + " no existe");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Actualizar información del producto
                if (!updateProduct(conn, product)) {
                    conn.rollback();
                    System.err.println("Error al actualizar la información del producto");
                    return false;
                }

                // 2. Actualizar categorías usando estrategia diferencial
                if (!updateProductCategories(conn, product.getID_Product(), newCategories)) {
                    conn.rollback();
                    System.err.println("Error al actualizar las categorías del producto");
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            handleException("Error al actualizar producto con categorías: " + product.getName(), e);
            return false;
        }
    }

    public List<Category> getProductCategories(int productId) {
        if (productId <= 0) {
            System.err.println("ID de producto inválido: " + productId);
            return Collections.emptyList();
        }

        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.ID_CATEGORY, c.NAME, c.STATE_CATEGORY " +
                "FROM CATEGORY c " +
                "JOIN CATEGORY_PRODUCT cp ON c.ID_CATEGORY = cp.ID_CATEGORY " +
                "WHERE cp.ID_PRODUCT = ? AND c.STATE_CATEGORY = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            handleException("Error al obtener categorías del producto ID: " + productId, e);
        }
        return categories;
    }

    // ========== MÉTODOS PRIVADOS DE VALIDACIÓN ==========
    private boolean isValidProductForUpdate(Product product) {
        if (product == null) {
            System.err.println("El producto no puede ser nulo");
            return false;
        }

        if (product.getID_Product() <= 0) {
            System.err.println("ID de producto inválido: " + product.getID_Product());
            return false;
        }

        return isValidProductData(product);
    }

    private boolean isValidProductForInsert(Product product) {
        if (product == null) {
            System.err.println("El producto no puede ser nulo");
            return false;
        }

        return isValidProductData(product);
    }

    private boolean isValidProductData(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            System.err.println("El nombre del producto es obligatorio");
            return false;
        }

        if (product.getName().length() > 255) {
            System.err.println("El nombre del producto no puede exceder 255 caracteres");
            return false;
        }

        if (product.getPrice() < 0) {
            System.err.println("El precio no puede ser negativo");
            return false;
        }

        return true;
    }

    private boolean areValidCategories(List<Category> categories) {
        for (Category category : categories) {
            if (category == null || category.getID_Category() <= 0) {
                System.err.println("Categoría inválida encontrada");
                return false;
            }
        }
        return true;
    }

    private boolean productExists(int productId) {
        String sql = "SELECT 1 FROM PRODUCT WHERE ID_PRODUCT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            handleException("Error al verificar existencia del producto ID: " + productId, e);
            return false;
        }
    }

    // ========== MÉTODOS PRIVADOS DE MAPEO ==========
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("ID_PRODUCT"),
                rs.getString("NAME"),
                rs.getDouble("PRICE"),
                rs.getString("AVAILABLE"),
                rs.getString("IMAGE_URL"),
                rs.getString("DESCRIPTION")
        );
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("ID_CATEGORY"),
                rs.getString("NAME"),
                rs.getString("STATE_CATEGORY")
        );
    }

    private void setProductParameters(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getName());
        ps.setDouble(2, product.getPrice());
        ps.setString(3, product.getAvailable());
        ps.setString(4, product.getUrlImage());
        ps.setString(5, product.getDescription());
    }

    private void handleException(String message, SQLException e) {
        System.err.println("=== ERROR EN PRODUCTCONTROLLER ===");
        System.err.println("Mensaje: " + message);
        System.err.println("SQL Error: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("=====================================");
        e.printStackTrace();
    }

    // ========== MÉTODOS PRIVADOS DE BASE DE DATOS ==========
    private int insertProduct(Connection conn, Product product) throws SQLException {
        String sql = "INSERT INTO PRODUCT (NAME, PRICE, AVAILABLE, IMAGE_URL, DESCRIPTION) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_PRODUCT"})) {
            setProductParameters(ps, product);

            if (ps.executeUpdate() == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                return generatedKeys.next() ? generatedKeys.getInt(1) : -1;
            }
        }
    }

    private boolean insertProductCategories(Connection conn, int productId, List<Category> categories) throws SQLException {
        String sql = "INSERT INTO CATEGORY_PRODUCT (ID_PRODUCT, ID_CATEGORY) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Category category : categories) {
                ps.setInt(1, productId);
                ps.setInt(2, category.getID_Category());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        }
    }

    private boolean updateProduct(Connection conn, Product product) throws SQLException {
        String sql = "UPDATE PRODUCT SET NAME = ?, PRICE = ?, AVAILABLE = ?, IMAGE_URL = ?, DESCRIPTION = ? " +
                "WHERE ID_PRODUCT = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setProductParameters(ps, product);
            ps.setInt(6, product.getID_Product());
            return ps.executeUpdate() > 0;
        }
    }

    private boolean updateProductCategories(Connection conn, int productId, List<Category> newCategories) throws SQLException {
        // 1. Obtener categorías actuales
        Set<Integer> currentCategoryIds = getCurrentCategoryIds(conn, productId);
        Set<Integer> newCategoryIds = newCategories.stream()
                .map(Category::getID_Category)
                .collect(Collectors.toSet());

        // 2. Calcular diferencias
        Set<Integer> categoriesToAdd = new HashSet<>(newCategoryIds);
        categoriesToAdd.removeAll(currentCategoryIds);

        Set<Integer> categoriesToRemove = new HashSet<>(currentCategoryIds);
        categoriesToRemove.removeAll(newCategoryIds);

        // 3. Eliminar solo las categorías que ya no están
        if (!categoriesToRemove.isEmpty()) {
            String deleteSql = "DELETE FROM CATEGORY_PRODUCT WHERE ID_PRODUCT = ? AND ID_CATEGORY = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                for (Integer categoryId : categoriesToRemove) {
                    ps.setInt(1, productId);
                    ps.setInt(2, categoryId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }

        // 4. Insertar solo las categorías nuevas
        if (!categoriesToAdd.isEmpty()) {
            String insertSql = "INSERT INTO CATEGORY_PRODUCT (ID_PRODUCT, ID_CATEGORY) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Integer categoryId : categoriesToAdd) {
                    ps.setInt(1, productId);
                    ps.setInt(2, categoryId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }

        return true;
    }

    private Set<Integer> getCurrentCategoryIds(Connection conn, int productId) throws SQLException {
        Set<Integer> categoryIds = new HashSet<>();
        String sql = "SELECT ID_CATEGORY FROM CATEGORY_PRODUCT WHERE ID_PRODUCT = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(rs.getInt("ID_CATEGORY"));
                }
            }
        }
        return categoryIds;
    }
}