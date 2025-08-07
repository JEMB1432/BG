package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.Controllers.ProductController;
import jemb.bistrogurmand.utils.Modals.TakeOrderDialog;
import jemb.bistrogurmand.utils.Modals.ViewSalesDialog;

import java.util.List;
import java.util.Optional;

public class AssignmentColumnFactory {
    private static ProductController productController;
    private static OrderController orderController;
    private static int currentWaiterId;

    public static void initialize(ProductController pc, OrderController oc, int waiterId) {
        productController = pc;
        orderController = oc;
        currentWaiterId = waiterId;
    }

    public static TableColumn<Assignment, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<Assignment, Void> indexColumn = new TableColumn<>("#");
        indexColumn.setPrefWidth(20);
        indexColumn.setStyle("-fx-alignment: center-left;");
        indexColumn.getStyleClass().add("index-column");

        indexColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    int pageIndex = pagination.getCurrentPageIndex();
                    int rowIndex = getIndex();
                    int globalIndex = (pageIndex * rowsPerPage) + rowIndex + 1;
                    setText(String.valueOf(globalIndex));
                }
            }
        });

        return indexColumn;
    }

    public static TableColumn<Assignment, String> createTableColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Mesa");
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTableAssign()));
        return column;
    }

    public static TableColumn<Assignment, String> createEmployeeColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Mesero");
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeAssign()));
        return column;
    }

    public static TableColumn<Assignment, String> createTimeColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Horario");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTimeStartAssign() + " - " + cellData.getValue().getTimeEndAssign()));
        return column;
    }

    public static TableColumn<Assignment, String> createShiftColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Turno");
        column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShiftAssign()));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String shift, boolean empty) {
                super.updateItem(shift, empty);
                if (empty || shift == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(shift);
                    switch (shift.toLowerCase()) {
                        case "mañana" -> label.getStyleClass().add("shift-morning");
                        case "tarde" -> label.getStyleClass().add("shift-evening");
                        case "noche" -> label.getStyleClass().add("shift-night");
                    }
                    setGraphic(label);
                }
            }
        });

        return column;
    }

    public static TableColumn<Assignment, String> createButtonsColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>(" ");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellFactory(param -> new TableCell<>() {
            private final Button takeButton = new Button("");
            private final Button seeButton = new Button("");
            private final HBox buttonsContainer = new HBox(5, takeButton, seeButton);

            {
                // Estilo de los botones
                ImageView takeImage = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/take.png").toString()));
                takeImage.setFitHeight(16);
                takeImage.setFitWidth(16);
                takeButton.setGraphic(takeImage);
                takeButton.getStyleClass().add("take-button");

                ImageView seeImage = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/see.png").toString()));
                seeImage.setFitHeight(16);
                seeImage.setFitWidth(16);
                seeButton.setGraphic(seeImage);
                seeButton.getStyleClass().add("see-button");

                buttonsContainer.setAlignment(Pos.CENTER);

                // Eventos de los botones
                takeButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    takeOrder(assignment);
                });

                seeButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());

                    // Abrir diálogo de ventas
                    ViewSalesDialog dialog = new ViewSalesDialog(assignment.getId(), currentWaiterId);
                    dialog.showAndWait();
                });
            }

            private void takeOrder(Assignment assignment) {
                // 1. Obtener lista de productos disponibles
                List<Product> availableProducts = productController.getAvailableProducts();

                // 2. Mostrar diálogo para seleccionar productos
                TakeOrderDialog dialog = new TakeOrderDialog(availableProducts);
                Optional<List<OrderItem>> result = dialog.showAndWait();

                result.ifPresent(orderItems -> {
                    if (!orderItems.isEmpty()) {
                        // 3. Crear venta inicial SOLO si el usuario confirmó
                        int saleId = orderController.createInitialSale(assignment.getId(), currentWaiterId);

                        if (saleId <= 0) {
                            showAlert("Error", "No se pudo iniciar la venta", Alert.AlertType.ERROR);
                            return;
                        }

                        // 4. Agregar items a la venta
                        if (orderController.addItemsToSale(saleId, orderItems)) {
                            showAlert("Productos Agregados",
                                    "Los productos se han añadido correctamente",
                                    Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Error",
                                    "No se pudieron agregar los productos a la venta",
                                    Alert.AlertType.ERROR);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsContainer);
                }
            }
        });

        return column;
    }

    private static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}