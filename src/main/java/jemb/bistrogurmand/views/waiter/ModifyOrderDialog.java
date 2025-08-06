package jemb.bistrogurmand.views.waiter;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ModifyOrderDialog extends Dialog<Void> {
    private ComboBox<String> cbOrders;
    private ComboBox<String> cbProducts;
    private TextArea txtReason;
    private final String tableId;

    public ModifyOrderDialog(String tableId) {
        this.tableId = tableId;
        setTitle("Modificar pedido – Mesa # " + tableId);

        // Botones OK y Cancelar
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Construir UI
        buildUI();

        // Acción al presionar OK
        setResultConverter(new Callback<>() {
            @Override
            public Void call(ButtonType btn) {
                if (btn == ButtonType.OK) {
                    sendCorrectionRequest();
                }
                return null;
            }
        });
    }

    private void buildUI() {
        cbOrders   = new ComboBox<>(FXCollections.observableArrayList(loadOrders()));
        cbOrders.setPromptText("Selecciona pedido");
        cbProducts = new ComboBox<>();
        cbProducts.setPromptText("Selecciona producto");

        // Al cambiar pedido, recarga productos
        cbOrders.setOnAction(e ->
                cbProducts.setItems(FXCollections.observableArrayList(loadProducts(cbOrders.getValue())))
        );

        txtReason = new TextArea();
        txtReason.setPromptText("Motivo de modificación...");
        txtReason.setWrapText(true);
        txtReason.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Pedido afectado:"), 0, 0);
        grid.add(cbOrders,                    1, 0);
        grid.add(new Label("Producto a modificar:"), 0, 1);
        grid.add(cbProducts,                      1, 1);
        grid.add(new Label("Motivo:"),         0, 2);
        grid.add(txtReason,                    1, 2);

        VBox root = new VBox(grid);
        root.setPadding(new Insets(5));
        getDialogPane().setContent(root);

        getDialogPane().getStylesheets()
                .add(getClass().getResource("/css/modify-order.css").toExternalForm());

    }



    // Carga IDs de pedidos para esta mesa
    private List<String> loadOrders() {
        String sql = "SELECT DISTINCT ID_Sale FROM Sale WHERE Table_Id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(tableId));
            ResultSet rs = ps.executeQuery();
            var list = FXCollections.<String>observableArrayList();
            while (rs.next()) {
                list.add(rs.getLong("ID_Sale") + "");
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }

    // Carga productos que ya forman parte de ese pedido
    private List<String> loadProducts(String orderId) {
        String sql =
                "SELECT si.ID_Product, p.Name " +
                        "  FROM SaleInfo si " +
                        "  JOIN Product p ON si.ID_Product = p.ID_Product " +
                        " WHERE si.ID_Sale = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(orderId));
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

    // Inserta la petición de corrección en ORDER_CORRECTION
    private void sendCorrectionRequest() {
        String orderIdVal = cbOrders.getValue();
        String productVal = cbProducts.getValue();
        String reason     = txtReason.getText().trim();

        if (orderIdVal == null || productVal == null || reason.isEmpty()) {
            showAlert("Debes seleccionar pedido, producto y escribir un motivo.");
            return;
        }

        long orderId   = Long.parseLong(orderIdVal);
        long productId = Long.parseLong(productVal.split(" - ")[0]);
        long employee  = Long.parseLong(UserSession.getCurrentUser().getUserID());

        String sql = """
        INSERT INTO Order_Correction (
          ID_Correction,
          ID_Employee,
          ID_Sale,
          ID_Product,
          Approved
        ) VALUES (
          ORDER_CORRECTION_SEQ.NEXTVAL, ?, ?, ?, ?
        )
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, employee);
            ps.setLong(2, orderId);
            ps.setLong(3, productId);
            ps.setInt(4, 0);  // 0 = pendiente de aprobación

            ps.executeUpdate();
            showAlert("Solicitud enviada con éxito.");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error al enviar solicitud: " + ex.getMessage());
        }
    }


    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.CLOSE)
                .showAndWait();
    }
}
