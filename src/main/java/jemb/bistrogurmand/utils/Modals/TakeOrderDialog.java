package jemb.bistrogurmand.utils.Modals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.OrderItem;
import jemb.bistrogurmand.utils.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TakeOrderDialog extends Dialog<List<OrderItem>> {

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private final TableView<OrderItem> orderTable = new TableView<>();

    public TakeOrderDialog(List<Product> availableProducts) {
        setTitle("Tomar Orden");
        setHeaderText("Seleccione los productos para la orden");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        getDialogPane().setPrefWidth(600);

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/take.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid(availableProducts);
        getDialogPane().setContent(grid);

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new ArrayList<>(orderItems);
            }
            return null;
        });
    }

    private GridPane createFormGrid(List<Product> availableProducts) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        TableView<Product> productsTable = createProductsTable(availableProducts);
        setupOrderTable();

        // Botón para agregar producto a la orden
        Button addButton = new Button("Agregar a Orden");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> {
            Product selected = productsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addToOrder(selected);
            }
        });

        // Diseño
        grid.add(new Label("Productos Disponibles:"), 0, 0);
        grid.add(productsTable, 0, 1);
        grid.add(addButton, 0, 2);
        Label lblSelected = new Label("Su Orden:");
        lblSelected.setStyle("-fx-font-weight: bold; -fx-underline: true;");
        grid.add(lblSelected, 0, 3);
        grid.add(orderTable, 0, 4);

        productsTable.setMaxWidth(Double.MAX_VALUE);
        orderTable.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(productsTable, Priority.ALWAYS);
        GridPane.setHgrow(orderTable, Priority.ALWAYS);


        return grid;
    }

    private TableView<Product> createProductsTable(List<Product> products) {
        TableView<Product> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(products));

        TableColumn<Product, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameCol, priceCol);
        table.setPrefHeight(200);

        return table;
    }

    private void setupOrderTable() {
        orderTable.getStyleClass().add("table-view");
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setItems(orderItems);

        TableColumn<OrderItem, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unitario.");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Cantidad");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(param -> new QuantityCell());

        TableColumn<OrderItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<OrderItem, Void> actionCol = new TableColumn<>("Acción");
        actionCol.setCellFactory(param -> new ActionCell());

        orderTable.getColumns().addAll(nameCol, priceCol, quantityCol, totalCol, actionCol);
        orderTable.setPrefHeight(200);
    }

    private void addToOrder(Product product) {
        if (product == null) return;

        // Verificar si el producto ya está en la orden
        for (OrderItem item : orderItems) {
            if (item.getProductId() == product.getID_Product()) {
                item.incrementQuantity();
                orderTable.refresh();
                return;
            }
        }

        // Si no está, agregarlo
        orderItems.add(new OrderItem(
                product.getID_Product(),
                product.getName(),
                product.getPrice(),
                1
        ));
    }

    // Celda para mostrar y modificar cantidad
    private class QuantityCell extends TableCell<OrderItem, Integer> {
        private final HBox container = new HBox(5);
        private final Button minusBtn = new Button("-");
        private final Label quantityLabel = new Label();
        private final Button plusBtn = new Button("+");

        {
            minusBtn.getStyleClass().add("quantity-button");
            plusBtn.getStyleClass().add("quantity-button");

            minusBtn.setOnAction(e -> {
                OrderItem item = getTableView().getItems().get(getIndex());
                if (item.getQuantity() > 1) {
                    item.decrementQuantity();
                    quantityLabel.setText(String.valueOf(item.getQuantity()));
                    getTableView().refresh();
                }
            });

            plusBtn.setOnAction(e -> {
                OrderItem item = getTableView().getItems().get(getIndex());
                item.incrementQuantity();
                quantityLabel.setText(String.valueOf(item.getQuantity()));
                getTableView().refresh();
            });

            container.setAlignment(Pos.CENTER);
            container.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
        }

        @Override
        protected void updateItem(Integer quantity, boolean empty) {
            super.updateItem(quantity, empty);
            if (empty || quantity == null) {
                setGraphic(null);
            } else {
                quantityLabel.setText(String.valueOf(quantity));
                setGraphic(container);
            }
        }
    }

    // Celda para eliminar items de la orden
    private class ActionCell extends TableCell<OrderItem, Void> {
        private final Button deleteBtn = new Button();

        {
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/delete.png")));
            deleteIcon.setFitHeight(16);
            deleteIcon.setFitWidth(16);
            deleteBtn.setGraphic(deleteIcon);
            deleteBtn.getStyleClass().add("delete-button");

            deleteBtn.setOnAction(e -> {
                OrderItem item = getTableView().getItems().get(getIndex());
                orderItems.remove(item);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(deleteBtn);
            }
        }
    }
}