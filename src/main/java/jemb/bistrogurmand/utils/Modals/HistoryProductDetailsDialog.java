package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import jemb.bistrogurmand.Controllers.SaleController;
import jemb.bistrogurmand.utils.OrderItem;
import jemb.bistrogurmand.utils.Sale;

import java.util.List;

public class HistoryProductDetailsDialog extends Dialog<Void> {
    private SaleController saleController = new SaleController();
    private final TableView<OrderItem> productsTable = new TableView<>();
    private final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

    public HistoryProductDetailsDialog(Sale sale) {
        setTitle("Detalles de Venta");
        setHeaderText("Productos incluidos en la venta:");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );
        getDialogPane().setPrefWidth(700);

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/dish.png")));

        // Configurar botón de cierre
        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        // Cargar items de la venta
        orderItems.setAll(saleController.getOrderItemsBySale(sale.getIdSale()));

        // Configurar tabla de productos
        setupProductsTable();

        // Crear layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Agregar información resumen
        Label summaryLabel = new Label(String.format(
                "Total: $%.2f - Estado: %s - Calificación: %s",
                sale.getTotal(),
                sale.getStatus() == 0 ? "Cerrada" : "Abierta",
                sale.getRating() > 0 ? String.format("%.1f ★", sale.getRating()) : "Sin calificar"
        ));
        summaryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        grid.add(summaryLabel, 0, 0);
        grid.add(productsTable, 0, 1);

        // Ajustar tamaños
        productsTable.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(productsTable, Priority.ALWAYS);

        getDialogPane().setContent(grid);
    }

    private void setupProductsTable() {
        productsTable.getStyleClass().add("table-view");
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productsTable.setItems(orderItems);

        TableColumn<OrderItem, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unitario");
        priceCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Cantidad");
        quantityCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        TableColumn<OrderItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        productsTable.getColumns().addAll(nameCol, priceCol, quantityCol, totalCol);
        productsTable.setPrefHeight(300);
    }
}