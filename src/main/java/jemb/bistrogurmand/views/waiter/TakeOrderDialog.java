package jemb.bistrogurmand.views.waiter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.SaleItem;
import jemb.bistrogurmand.utils.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TakeOrderDialog extends Dialog<Void> {

    private ComboBox<String> cbCategory;
    private ComboBox<String> cbProduct;
    private Spinner<Integer> spQuantity;
    private TextField tfObservation;
    private TableView<SaleItem> table;
    private ObservableList<SaleItem> items = FXCollections.observableArrayList();
    private String tableId;

    public TakeOrderDialog(String tableId) {
        this.tableId = tableId;
        setTitle("Tomar pedido - Mesa # " + tableId);

        // Botones OK / CANCEL
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Construir formulario de selección
        buildSelector();

        // Construir tabla de items
        buildTable();

        // Layout general
        VBox root = new VBox(10, createSelectorPane(), table);
        root.setPadding(new Insets(15));
        getDialogPane().setContent(root);

        // Acción al cerrar con OK
        setResultConverter(new Callback<>() {
            @Override
            public Void call(ButtonType btn) {
                if (btn == ButtonType.OK) {
                    persistItems();
                }
                return null;
            }
        });
        getDialogPane().getStylesheets().add(
                getClass().getResource("/css/take-order.css").toExternalForm()
        );
    }


    private void buildSelector() {
        cbCategory  = new ComboBox<>(FXCollections.observableArrayList(loadCategories()));
        cbCategory.setPromptText("Categoría");

        cbProduct   = new ComboBox<>();
        cbProduct.setPromptText("Producto");

        spQuantity  = new Spinner<>(1, 20, 1);
        tfObservation = new TextField();
        tfObservation.setPromptText("Observaciones");

        // Al cambiar categoría, recargar productos
        cbCategory.setOnAction(e -> {
            String cat = cbCategory.getValue();
            cbProduct.setItems(FXCollections.observableArrayList(loadProducts(cat)));
        });
    }

    private GridPane createSelectorPane() {
        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);

        Button btnAdd = new Button("+ Agregar");
        btnAdd.setOnAction(e -> {
            if (cbProduct.getValue() == null) {
                showAlert("Selecciona un producto");
                return;
            }
            SaleItem item = new SaleItem(
                    Long.parseLong(cbProduct.getValue().split(" - ")[0]),  // asume "id - nombre"
                    cbProduct.getValue().split(" - ")[1],
                    fetchPrice(Long.parseLong(cbProduct.getValue().split(" - ")[0])),
                    spQuantity.getValue(),
                    tfObservation.getText().trim()
            );
            items.add(item);
        });

        gp.add(new Label("Categoría:"), 0, 0);
        gp.add(cbCategory, 1, 0);
        gp.add(new Label("Producto:"), 0, 1);
        gp.add(cbProduct, 1, 1);
        gp.add(new Label("Cantidad:"), 0, 2);
        gp.add(spQuantity, 1, 2);
        gp.add(new Label("Obs.:"), 0, 3);
        gp.add(tfObservation, 1, 3);
        gp.add(btnAdd, 2, 1);
        return gp;
    }

    private void buildTable() {
        table = new TableView<>(items);
        table.setPlaceholder(new Label("No hay ítems agregados"));

        TableColumn<SaleItem, String> colName    = new TableColumn<>("Producto");
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<SaleItem, Double> colPrice   = new TableColumn<>("Precio");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SaleItem, Integer> colQty    = new TableColumn<>("Cantidad");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SaleItem, String> colObs     = new TableColumn<>("Observación");
        colObs.setCellValueFactory(new PropertyValueFactory<>("observation"));

        table.getColumns().addAll(colName, colPrice, colQty, colObs);
    }

    // Carga categorías desde la BD
    private List<String> loadCategories() {
        try (Connection c = DatabaseConnection.getConnection();
             var st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT ID_Category, Name FROM Category WHERE State_Category = 1"
             )) {
            var list = FXCollections.<String>observableArrayList();
            while (rs.next()) {
                list.add(rs.getLong("ID_Category") + " - " + rs.getString("Name"));
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    // Carga productos según categoría
    private List<String> loadProducts(String catValue) {
        long catId = Long.parseLong(catValue.split(" - ")[0]);
        String sql = """
            SELECT p.ID_Product, p.Name
              FROM Product p
              JOIN Category_Product cp
                ON p.ID_Product = cp.ID_Product
             WHERE cp.ID_Category = ? AND p.State_Product = 1
            """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, catId);
            ResultSet rs = ps.executeQuery();
            var list = FXCollections.<String>observableArrayList();
            while (rs.next()) {
                list.add(rs.getLong("ID_Product") + " - " + rs.getString("Name"));
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    // Obtiene precio en línea
    private double fetchPrice(long productId) {
        String sql = "SELECT Price FROM Product WHERE ID_Product = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("Price");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0;
    }

    // Inserta todos los ítems en SaleInfo
    private void persistItems() {
        // Solo ID_Info, ID_Sale, ID_Product y Amount
        String sql =
                "INSERT INTO SaleInfo (" +
                        "  ID_Info, ID_Sale, ID_Product, Amount" +
                        ") VALUES (" +
                        "  SALEINFO_SEQ.NEXTVAL, ?, ?, ?" +
                        ")";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (SaleItem it : items) {
                ps.setLong(1, Long.parseLong(tableId));    // ID_Sale
                ps.setLong(2, it.getProductId());          // ID_Product
                ps.setInt(3, it.getQuantity());            // Amount
                ps.addBatch();
            }

            ps.executeBatch();
            showAlert("Pedido registrado con " + items.size() + " ítems.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error al grabar pedido: " + ex.getMessage());
        }
    }


    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.CLOSE)
                .showAndWait();
    }
}
