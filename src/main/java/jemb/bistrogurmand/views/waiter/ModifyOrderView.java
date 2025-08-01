package jemb.bistrogurmand.views.waiter;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ModifyOrderView {
    private final BorderPane root;
    private final OrderController orderController;
    private ComboBox<String> cbOrders;
    private TextArea txtReason;

    public ModifyOrderView(String tableId) {
        this.orderController = new OrderController(tableId);
        this.root            = new BorderPane();
        buildUI(tableId);
    }

    private void buildUI(String tableId) {
        root.setId("modificar-pedido-root");

        // 1) Sidebar a la izquierda
        root.setLeft(new SidebarWaiter().getView());

        // Contenido central
        VBox main = new VBox(15);
        main.setPadding(new Insets(20));
        main.setAlignment(Pos.TOP_LEFT);

        // Mesa #
        Label lblMesa = new Label("Mesa: # " + tableId);
        lblMesa.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Pedidos existentes en esta mesa
        Label lblAffected = new Label("Pedido afectado:");
        cbOrders = new ComboBox<>(
                FXCollections.observableArrayList(
                        // Obtiene lista de IDs (o descripciones) de pedidos
                        orderController.getOrdersForTable()
                )
        );
        cbOrders.setPromptText("Selecciona un pedido");

        HBox boxAffected = new HBox(10, lblAffected, cbOrders);
        boxAffected.setAlignment(Pos.CENTER_LEFT);

        // Motivo de modificación
        Label lblReason = new Label("Motivo de modificación:");
        txtReason = new TextArea();
        txtReason.setPromptText("Describe el motivo...");
        txtReason.setPrefRowCount(3);
        txtReason.setWrapText(true);

        // Botón de envío
        Button btnSend = new Button("Enviar");
        btnSend.setOnAction(e -> {
            String orderId = cbOrders.getValue();
            String reason  = txtReason.getText().trim();

            if (true) {
                if (orderId == null || orderId.isEmpty()) {
                    showAlert("Selecciona primero el pedido a modificar.");
                    return;
                }
                if (reason.isEmpty()) {
                    showAlert("Debes escribir un motivo de modificación.");
                    return;
                }
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "INSERT INTO ORDER_CORRECTION (ID_CORRECTION, ID_EMPLOYEE, ID_SALE, ID_PRODUCT, APPROVED) " +
                            "VALUES (ORDER_CORRECTION_SEQ.NEXTVAL, ?, ?, ?, ?)";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setLong(1, Long.parseLong(UserSession.getCurrentUser().getUserID())); // ID_EMPLOYEE
                    stmt.setLong(2, Long.parseLong(orderId));              // ID_SALE
                    stmt.setLong(3, -1);                                   // ID_PRODUCT (placeholder)
                    stmt.setInt(4, 0);                                     // APPROVED = 0 (pendiente)

                    stmt.executeUpdate();
                    showAlert("Solicitud enviada correctamente.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Error al enviar solicitud: " + ex.getMessage());
                }
            }
        });
        main.getChildren().addAll(
                lblMesa,
                boxAffected,
                lblReason,
                txtReason,
                btnSend
        );
        root.setCenter(main);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.CLOSE);
        alert.initOwner(root.getScene().getWindow());
        alert.showAndWait();
    }

    public Pane getView() {
        return root;
    }
}
