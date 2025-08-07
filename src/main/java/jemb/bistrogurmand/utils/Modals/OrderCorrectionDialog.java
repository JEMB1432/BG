package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import jemb.bistrogurmand.utils.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderCorrectionDialog extends Dialog<Map<Integer, Integer>> {

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private final ObservableList<Product> availableProductsList;
    private final TableView<OrderItem> orderTable = new TableView<>();
    private final TableView<Product> productsTable = new TableView<>();
    private final Map<Integer, Integer> originalQuantities = new HashMap<>();

    public OrderCorrectionDialog(List<Product> availableProducts, List<OrderItem> currentItems) {
        setTitle("Solicitar Corrección de Orden");
        setHeaderText("Agregue, modifique o elimine productos de la orden");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );
        getDialogPane().setPrefWidth(700);

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/edit.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Solicitar Corrección", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Inicializar lista de productos disponibles
        availableProductsList = FXCollections.observableArrayList(availableProducts);

        // Cargar items actuales
        for (OrderItem item : currentItems) {
            orderItems.add(new OrderItem(
                    item.getProductId(),
                    item.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity()
            ));
            originalQuantities.put(item.getProductId(), item.getQuantity());
        }

        // Crear formulario
        GridPane grid = createFormGrid();
        getDialogPane().setContent(grid);

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getModifiedItems();
            }
            return null;
        });
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Tabla de productos disponibles
        setupProductsTable();
        grid.add(new Label("Productos Disponibles:"), 0, 0);
        grid.add(productsTable, 0, 1);

        // Botón para agregar producto a la orden
        Button addButton = new Button("Agregar a Orden");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> {
            Product selected = productsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                addToOrder(selected);
            }
        });
        grid.add(addButton, 0, 2);

        // Tabla de la orden actual (a modificar)
        setupOrderTable();
        grid.add(new Label("Orden Actual (Modifique cantidades o elimine productos):"), 0, 3);
        grid.add(orderTable, 0, 4);

        // Ajustar tamaños
        productsTable.setMaxWidth(Double.MAX_VALUE);
        orderTable.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(productsTable, Priority.ALWAYS);
        GridPane.setHgrow(orderTable, Priority.ALWAYS);

        return grid;
    }

    private void setupProductsTable() {
        productsTable.getStyleClass().add("table-view");
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productsTable.setItems(availableProductsList);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTable.getColumns().addAll(nameCol, priceCol);
        productsTable.setPrefHeight(200);
    }

    private void setupOrderTable() {
        orderTable.getStyleClass().add("table-view");
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setItems(orderItems);

        TableColumn<OrderItem, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unitario");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Cantidad");
        quantityCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        quantityCol.setCellFactory(param -> new QuantityCell());

        TableColumn<OrderItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

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

    private Map<Integer, Integer> getModifiedItems() {
        Map<Integer, Integer> modifications = new HashMap<>();

        // Recorrer los productos en la orden actual (pueden ser originales o nuevos)
        for (OrderItem item : orderItems) {
            int productId = item.getProductId();
            int newQuantity = item.getQuantity();

            // Si el producto estaba originalmente, comparamos con la cantidad original
            if (originalQuantities.containsKey(productId)) {
                int originalQty = originalQuantities.get(productId);
                if (newQuantity != originalQty) {
                    modifications.put(productId, newQuantity);
                }
            } else {
                // Es un producto nuevo: se registra la cantidad (si es >0)
                if (newQuantity > 0) {
                    modifications.put(productId, newQuantity);
                }
            }
        }

        // Buscar productos que estaban originalmente pero ya no están (quitados)
        for (Integer productId : originalQuantities.keySet()) {
            boolean found = false;
            for (OrderItem item : orderItems) {
                if (item.getProductId() == productId) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Producto quitado: cantidad 0
                modifications.put(productId, 0);
            }
        }

        return modifications;
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
                OrderItem item = getTableRow().getItem();
                if (item != null && item.getQuantity() > 1) {
                    item.decrementQuantity();
                    quantityLabel.setText(String.valueOf(item.getQuantity()));
                    getTableView().refresh();
                }
            });

            plusBtn.setOnAction(e -> {
                OrderItem item = getTableRow().getItem();
                if (item != null) {
                    item.incrementQuantity();
                    quantityLabel.setText(String.valueOf(item.getQuantity()));
                    getTableView().refresh();
                }
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
                OrderItem item = getTableRow().getItem();
                if (item != null) {
                    orderItems.remove(item);
                }
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

    // Clase interna OrderItem
    public static class OrderItem {
        private final int productId;
        private final String productName;
        private final double unitPrice;
        private int quantity;

        public OrderItem(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public double getTotalPrice() { return unitPrice * quantity; }
        public void incrementQuantity() { quantity++; }
        public void decrementQuantity() { if (quantity > 1) quantity--; }
    }
}