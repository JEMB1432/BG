package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.Controllers.ProductController;
import jemb.bistrogurmand.Controllers.SaleController;
import jemb.bistrogurmand.DbConection.DatabaseConnection;
import jemb.bistrogurmand.utils.Product;
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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/sale.png")));

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
        GridPane.setHgrow(salesTable, Priority.ALWAYS);
        salesTable.setMaxWidth(Double.MAX_VALUE);
        getDialogPane().setPrefWidth(600);
        getDialogPane().setContent(grid);
    }

    private void setupSalesTable(List<Sale> sales) {
        ObservableList<Sale> salesList = FXCollections.observableArrayList(sales);
        salesTable.setItems(salesList);

        salesTable.getColumns().clear();

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

        // Total actualizado desde BD
        TableColumn<Sale, Float> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> {
            int saleId = cellData.getValue().getIdSale();
            float realTotal = getRealTimeTotal(saleId);
            return new ReadOnlyObjectWrapper<>(realTotal);
        });

        TableColumn<Sale, Void> actionCol = new TableColumn<>("Acciones");
        actionCol.setCellFactory(param -> new ActionsCell());

        salesTable.getColumns().addAll(dateCol, totalCol, actionCol);
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private float getRealTimeTotal(int saleId) {
        String sql = "SELECT TOTAL FROM SALE WHERE ID_SALE = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getFloat("TOTAL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class ActionsCell extends TableCell<Sale, Void> {
        private final Button editButton = new Button("Solicitar Corrección");
        private final Button closeButton = new Button("Cerrar Venta");
        private final HBox container = new HBox(5, editButton, closeButton);

        {
            editButton.getStyleClass().add("edit-button-d");
            closeButton.getStyleClass().add("close-button-d");

            editButton.setOnAction(event -> {
                Sale sale = getTableView().getItems().get(getIndex());
                requestCorrection(sale);
            });

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
        ProductController productController = new ProductController();
        List<Product> availableProducts = productController.getAvailableProducts();
        List<OrderCorrectionDialog.OrderItem> currentItems = getSaleItems(sale.getIdSale());

        OrderCorrectionDialog dialog = new OrderCorrectionDialog(availableProducts, currentItems);
        Optional<Map<Integer, Integer>> result = dialog.showAndWait();

        result.ifPresent(modifications -> {
            saveCorrectionRequests(sale.getIdSale(), modifications);
            showAlert("Solicitud Enviada",
                    "La solicitud de corrección ha sido enviada al líder de meseros",
                    Alert.AlertType.INFORMATION);
        });
    }

    private List<OrderCorrectionDialog.OrderItem> getSaleItems(int saleId) {
        List<OrderCorrectionDialog.OrderItem> items = new ArrayList<>();
        // Consulta que considera correcciones aprobadas
        String sql = "SELECT si.ID_PRODUCT, p.NAME, p.PRICE, "
                + "COALESCE(oc.NEW_AMOUNT, si.AMOUNT) AS ACTUAL_AMOUNT "
                + "FROM SALEINFO si "
                + "JOIN PRODUCT p ON p.ID_PRODUCT = si.ID_PRODUCT "
                + "LEFT JOIN ("
                + "  SELECT ID_PRODUCT, NEW_AMOUNT "
                + "  FROM ORDER_CORRECTION "
                + "  WHERE ID_SALE = ? AND APPROVED = 1"
                + ") oc ON oc.ID_PRODUCT = si.ID_PRODUCT "
                + "WHERE si.ID_SALE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.setInt(2, saleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new OrderCorrectionDialog.OrderItem(
                        rs.getInt("ID_PRODUCT"),
                        rs.getString("NAME"),
                        rs.getDouble("PRICE"),
                        rs.getInt("ACTUAL_AMOUNT")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void saveCorrectionRequests(int saleId, Map<Integer, Integer> modifications) {
        String sql = "INSERT INTO ORDER_CORRECTION (ID_EMPLOYEE, ID_SALE, ID_PRODUCT, NEW_AMOUNT, APPROVED) "
                + "VALUES (?, ?, ?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Map.Entry<Integer, Integer> entry : modifications.entrySet()) {
                int productId = entry.getKey();
                int newAmount = entry.getValue();
                int currentAmount = getCurrentProductAmount(saleId, productId);

                // Guardar solo si hay cambio real
                if (currentAmount != newAmount) {
                    stmt.setInt(1, currentWaiterId);
                    stmt.setInt(2, saleId);
                    stmt.setInt(3, productId);
                    stmt.setInt(4, newAmount);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentProductAmount(int saleId, int productId) {
        String sql = "SELECT AMOUNT FROM SALEINFO WHERE ID_SALE = ? AND ID_PRODUCT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.setInt(2, productId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("AMOUNT") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
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
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Calificar Venta");
        dialog.setHeaderText("Por favor califique la experiencia del cliente");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/star_full.png")));

        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);

        Slider slider = new Slider(0, 5, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.5);
        slider.setSnapToTicks(true);

        slider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return value % 1 == 0 ? String.valueOf(value.intValue()) : "";
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        });

        Label valueLabel = new Label(String.format("Calificación: %.1f", slider.getValue()));
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Runnable updateStars = () -> {
            starsBox.getChildren().clear();
            double val = Math.round(slider.getValue() * 2) / 2.0;
            int fullStars = (int) val;
            boolean halfStar = (val - fullStars) >= 0.5;
            int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

            for (int i = 0; i < fullStars; i++) {
                starsBox.getChildren().add(createStarIcon("/jemb/bistrogurmand/Icons/star_full.png"));
            }
            if (halfStar) {
                starsBox.getChildren().add(createStarIcon("/jemb/bistrogurmand/Icons/star_half.png"));
            }
            for (int i = 0; i < emptyStars; i++) {
                starsBox.getChildren().add(createStarIcon("/jemb/bistrogurmand/Icons/star_empty.png"));
            }
            valueLabel.setText(String.format("Calificación: %.1f", val));
        };

        slider.valueProperty().addListener((obs, oldVal, newVal) -> updateStars.run());
        updateStars.run();

        VBox content = new VBox(10, starsBox, slider, valueLabel);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton ->
                dialogButton == okButtonType ? Math.round(slider.getValue() * 2) / 2.0 : null
        );

        return dialog.showAndWait();
    }

    private ImageView createStarIcon(String path) {
        return new ImageView(new Image(getClass().getResourceAsStream(path)));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}