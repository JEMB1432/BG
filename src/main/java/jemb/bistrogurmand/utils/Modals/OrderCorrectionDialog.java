package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderCorrectionDialog extends Dialog<Map<Integer, Integer>> {

    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private final TableView<OrderItem> orderTable = new TableView<>();
    private final Map<Integer, Integer> originalQuantities = new HashMap<>();

    public OrderCorrectionDialog(List<OrderItem> currentItems) {
        setTitle("Solicitar Corrección de Orden");
        setHeaderText("Modifique las cantidades de los productos");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/edit.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Solicitar Corrección", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid(currentItems);
        getDialogPane().setContent(grid);

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getModifiedItems();
            }
            return null;
        });
    }

    private GridPane createFormGrid(List<OrderItem> currentItems) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

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

        setupOrderTable();

        // Diseño
        grid.add(new Label("Modifique las cantidades:"), 0, 0);
        grid.add(orderTable, 0, 1);

        return grid;
    }

    private void setupOrderTable() {
        orderTable.getStyleClass().add("table-view");
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setItems(orderItems);

        TableColumn<OrderItem, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unit.");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Cantidad");
        quantityCol.setCellValueFactory(cellData ->
                cellData.getValue().quantityProperty().asObject());
        quantityCol.setCellFactory(param -> new QuantityCell());

        TableColumn<OrderItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        orderTable.getColumns().addAll(nameCol, priceCol, quantityCol, totalCol);
        orderTable.setPrefHeight(300);
    }

    private Map<Integer, Integer> getModifiedItems() {
        Map<Integer, Integer> modifications = new HashMap<>();
        for (OrderItem item : orderItems) {
            int originalQty = originalQuantities.getOrDefault(item.getProductId(), 0);
            if (item.getQuantity() != originalQty) {
                modifications.put(item.getProductId(), item.getQuantity());
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
                if (item != null) {
                    item.decrementQuantity();
                    quantityLabel.setText(String.valueOf(item.getQuantity()));
                }
            });

            plusBtn.setOnAction(e -> {
                OrderItem item = getTableRow().getItem();
                if (item != null) {
                    item.incrementQuantity();
                    quantityLabel.setText(String.valueOf(item.getQuantity()));
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
}