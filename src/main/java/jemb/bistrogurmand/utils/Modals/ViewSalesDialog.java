package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.Controllers.SaleController;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Sale;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewSalesDialog extends Dialog<Void> {

    private final SaleController saleController = new SaleController();
    private final OrderController orderController = new OrderController();
    private final TableView<Sale> salesTable = new TableView<>();
    private final int currentWaiterId;

    public ViewSalesDialog(int assignmentId, int currentWaiterId) {
        this.currentWaiterId = currentWaiterId;

        setTitle("Ventas Activas");
        setHeaderText("Ventas asociadas a la mesa");


        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/see.png")));

        // Configurar botones
        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);

        // Obtener ventas activas
        List<Sale> activeSales = saleController.getActiveSalesByAssignment(assignmentId);
        setupSalesTable(activeSales);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(salesTable, 0, 0);

        getDialogPane().setContent(grid);
    }

    private void setupSalesTable(List<Sale> sales) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ObservableList<Sale> salesList = FXCollections.observableArrayList(sales);
        salesTable.setItems(salesList);

        TableColumn<Sale, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idSale"));
        //idCol.setPrefWidth(60);

        TableColumn<Sale, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getSaleDate();
            if (timestamp != null) {
                Date date = new Date(timestamp.getTime());
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
                return new ReadOnlyStringWrapper(formattedDate);
            }
            return new ReadOnlyStringWrapper("");
        });
        //dateCol.setPrefWidth(150);

        TableColumn<Sale, Float> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        //totalCol.setPrefWidth(80);

        TableColumn<Sale, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setCellFactory(param -> new ActionsCell());
        //actionCol.setPrefWidth(250);

        salesTable.getColumns().addAll(idCol, dateCol, totalCol, actionCol);
        salesTable.setPrefHeight(300);
        salesTable.setPrefWidth(600);
        salesTable.setMinWidth(500);

        grid.add(salesTable, 0, 0);
        GridPane.setHgrow(salesTable, Priority.ALWAYS);
        salesTable.setMaxWidth(Double.MAX_VALUE);

    }

    private class ActionsCell extends TableCell<Sale, Void> {
        private final Button editButton = new Button("Solicitar Corrección");
        private final Button closeButton = new Button("Cerrar Venta");
        private final HBox container = new HBox(5, editButton, closeButton);

        {
            // Estilo de los botones
            editButton.getStyleClass().add("edit-button-d");
            closeButton.getStyleClass().add("close-button-d");

            // Evento de editar
            editButton.setOnAction(event -> {
                Sale sale = getTableView().getItems().get(getIndex());
                requestCorrection(sale);
            });

            // Evento de cerrar venta
            closeButton.setOnAction(event -> {
                Sale sale = getTableView().getItems().get(getIndex());
                closeSale(sale);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(container);
            }
        }
    }

    private void requestCorrection(Sale sale) {
        // Obtener los productos actuales de la venta
        List<OrderItem> currentItems = getSaleItems(sale.getIdSale());

        // Mostrar diálogo de corrección
        OrderCorrectionDialog dialog = new OrderCorrectionDialog(currentItems);
        Optional<Map<Integer, Integer>> result = dialog.showAndWait();

        result.ifPresent(modifications -> {
            // Guardar las correcciones en la base de datos
            saveCorrectionRequests(sale.getIdSale(), modifications);

            showAlert("Solicitud Enviada",
                    "La solicitud de corrección ha sido enviada al líder de meseros",
                    Alert.AlertType.INFORMATION);
        });
    }

    private List<OrderItem> getSaleItems(int saleId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT p.ID_PRODUCT, p.NAME, p.PRICE, si.AMOUNT " +
                "FROM SALEINFO si " +
                "JOIN PRODUCT p ON p.ID_PRODUCT = si.ID_PRODUCT " +
                "WHERE si.ID_SALE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getInt("ID_PRODUCT"),
                        rs.getString("NAME"),
                        rs.getDouble("PRICE"),
                        rs.getInt("AMOUNT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void saveCorrectionRequests(int saleId, Map<Integer, Integer> modifications) {
        String sql = "INSERT INTO ORDER_CORRECTION (ID_EMPLOYEE, ID_SALE, ID_PRODUCT, NEW_AMOUNT, APPROVED) " +
                "VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Map.Entry<Integer, Integer> entry : modifications.entrySet()) {
                stmt.setInt(1, currentWaiterId); // ID del mesero actual
                stmt.setInt(2, saleId);
                stmt.setInt(3, entry.getKey());  // ID_PRODUCT
                stmt.setInt(4, entry.getValue()); // NEW_AMOUNT
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeSale(Sale sale) {
        // Mostrar diálogo de calificación
        Optional<Double> rating = showRatingDialog();
        rating.ifPresent(r -> {
            if (orderController.finalizeSale(sale.getIdSale(), r)) {
                showAlert("Venta Cerrada", "La venta se ha cerrado correctamente", Alert.AlertType.INFORMATION);
                // Actualizar tabla
                salesTable.getItems().remove(sale);
            } else {
                showAlert("Error", "No se pudo cerrar la venta", Alert.AlertType.ERROR);
            }
        });
    }

    private Optional<Double> showRatingDialog() {
        // Mismo método de calificación que ya existe
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Calificar Venta");
        dialog.setHeaderText("Por favor califique la experiencia del cliente");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Slider slider = new Slider(0, 5, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setBlockIncrement(0.5);
        slider.setSnapToTicks(true);

        Label valueLabel = new Label(String.format("Calificación: %.1f", slider.getValue()));
        slider.valueProperty().addListener((obs, oldVal, newVal) ->
                valueLabel.setText(String.format("Calificación: %.1f", newVal)));

        VBox content = new VBox(10, slider, valueLabel);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return slider.getValue();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}