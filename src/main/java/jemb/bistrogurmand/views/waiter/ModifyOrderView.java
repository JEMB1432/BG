package jemb.bistrogurmand.views.waiter;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.Controllers.OrderController;

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
        root.setLeft(new SidebarMesero().getView());

        // 2) Contenido central
        VBox main = new VBox(15);
        main.setPadding(new Insets(20));
        main.setAlignment(Pos.TOP_LEFT);

        // — Mesa #
        Label lblMesa = new Label("Mesa: # " + tableId);
        lblMesa.setFont(Font.font("System", FontWeight.BOLD, 24));

        // — Pedidos existentes en esta mesa
        Label lblAffected = new Label("Pedido afectado:");
        cbOrders = new ComboBox<>(
                FXCollections.observableArrayList(
                        // Obtiene lista de IDs (o descripciones) de pedidos
                        orderController.getOrdersForTable()
                )
        );