package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.utils.modals.Category;
import jemb.bistrogurmand.utils.modals.OrderItem;
import jemb.bistrogurmand.utils.modals.Product;
import jemb.bistrogurmand.utils.UserSession;
import java.util.List;

public class TakeorderView {
    private final BorderPane root;
    private final OrderController orderController;
    private TableView<OrderItem> orderTable;

    // Ahora REQUIERE el ID de la mesa
    public TakeorderView(String tableId) {
        this.orderController = new OrderController(tableId);
        this.root            = new BorderPane();
        buildUI();
    }

    private void buildUI() {
        root.setId("tomar-pedido-root");

        // Main
        VBox main = new VBox(15);
        main.setPadding(new Insets(20));
        main.setAlignment(Pos.TOP_LEFT);

        // Encabezado
        Label lblMesa = new Label("Mesa # " + orderController.getCurrentTable());
        lblMesa.setFont(Font.font("System", FontWeight.BOLD, 24));
        main.getChildren().add(lblMesa);

        // Mesero
        Label lblMesero = new Label("Mesero: " +
                UserSession.getCurrentUser().getFirstName() + " " +
                UserSession.getCurrentUser().getLastName());
        lblMesero.setFont(Font.font("System", FontWeight.NORMAL, 16));
        main.getChildren().add(lblMesero);

        // Categorías
        FlowPane categoryPane = new FlowPane(10, 10);
        categoryPane.setId("category-pane");
        List<Category> cats = orderController.getCategories();
        cats.forEach(cat -> {
            Button btn = new Button(cat.getName());
            btn.getStyleClass().add("category-button");
            btn.setOnAction(e -> openCategoryDialog(cat));
            categoryPane.getChildren().add(btn);
        });
        main.getChildren().add(categoryPane);

        // Resumen de pedido
        Label lblResumen = new Label("Resumen de pedido");
        lblResumen.setFont(Font.font("System", FontWeight.BOLD, 18));
        main.getChildren().add(lblResumen);

        orderTable = new TableView<>(orderController.getCurrentOrder());
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<OrderItem, Number> qtyCol = new TableColumn<>("Cantidad");
        qtyCol.setCellValueFactory(c -> c.getValue().quantityProperty());
        TableColumn<OrderItem, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(c -> c.getValue().productNameProperty());
        TableColumn<OrderItem, Number> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(c -> c.getValue().priceProperty());
        orderTable.getColumns().addAll(qtyCol, nameCol, priceCol);
        main.getChildren().add(orderTable);

        // Acciones
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button btnRemove = new Button("Quitar producto");
        btnRemove.getStyleClass().add("btn-remove");
        btnRemove.setOnAction(e -> {
            OrderItem sel = orderTable.getSelectionModel().getSelectedItem();
            if (sel != null) orderController.removeItem(sel);
        });
        Button btnSend = new Button("Enviar pedido");
        btnSend.getStyleClass().add("btn-send");
        btnSend.setOnAction(e -> orderController.sendOrder());
        actions.getChildren().addAll(btnRemove, btnSend);
        main.getChildren().add(actions);

        root.setCenter(main);
    }

    private void openCategoryDialog(Category cat) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Categoría: " + cat.getName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.initOwner(root.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("modal-content");

        List<Product> products = orderController.getProductsByCategory(cat.getId());
        for (Product p : products) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("modal-item-row");

            Label name = new Label(p.getName());
            name.setPrefWidth(200);
            Label price = new Label("$" + p.getPrice());
            price.setPrefWidth(60);
            Spinner<Integer> qtySpinner = new Spinner<>(1, 20, 1);
            qtySpinner.setPrefWidth(60);
            TextField obsField = new TextField();
            obsField.setPromptText("Observaciones");
            obsField.setPrefWidth(200);
            Button addBtn = new Button("+ Agregar");
            addBtn.getStyleClass().add("btn-add");
            addBtn.setOnAction(evt -> {
                orderController.addItem(p, qtySpinner.getValue());
                String obs = obsField.getText().trim();
                if (!obs.isEmpty()) orderController.addObservation(p.getId(), obs);
            });

            row.getChildren().addAll(name, price, qtySpinner, obsField, addBtn);
            content.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("modal-scroll");
        dialog.getDialogPane().setContent(scroll);
        dialog.showAndWait();
    }

    public BorderPane getView() {
        return root;
    }
}
