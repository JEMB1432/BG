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
            private final Button editButton = new Button("");
            private final Button deleteButton = new Button("");
            private final HBox buttonsContainer = new HBox(5, takeButton, seeButton, editButton, deleteButton);

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

                ImageView editImage = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/edit.png").toString()));
                editImage.setFitHeight(16);
                editImage.setFitWidth(16);
                editButton.setGraphic(editImage);
                editButton.getStyleClass().add("edit-button");

                ImageView deleteImage = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/delete.png").toString()));
                deleteImage.setFitHeight(16);
                deleteImage.setFitWidth(16);
                deleteButton.setGraphic(deleteImage);
                deleteButton.getStyleClass().add("delete-button");

                buttonsContainer.setAlignment(Pos.CENTER);

                // Eventos de los botones
                takeButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    takeOrder(assignment);
                });

                editButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("Editar: " + assignment);
                    // Puedes llamar a un método para editar el assignment
                });

                deleteButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("Eliminar: " + assignment);
                    // Puedes llamar a un método para eliminar el assignment
                });

                seeButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("See: " + assignment);
                });
            }

            private void takeOrder(Assignment assignment) {
                // 1. Obtener lista de productos disponibles
                List<Product> availableProducts = productController.getAvailableProducts();

                // 2. Crear venta inicial
                int saleId = orderController.createInitialSale(assignment.getId(), currentWaiterId);
                if (saleId <= 0) {
                    showAlert("Error", "No se pudo iniciar la venta", Alert.AlertType.ERROR);
                    return;
                }

                // 3. Mostrar diálogo para seleccionar productos
                TakeOrderDialog dialog = new TakeOrderDialog(availableProducts);
                Optional<List<OrderItem>> result = dialog.showAndWait(); // ← Cambio aquí

                result.ifPresent(orderItems -> { // ← Y aquí
                    if (!orderItems.isEmpty()) { // ← Y aquí
                        // 4. Agregar items a la venta
                        if (orderController.addItemsToSale(saleId, orderItems)) { // ← Y aquí
                            // 5. Mostrar diálogo para calificación
                            Optional<Double> rating = showRatingDialog();
                            rating.ifPresent(r -> {
                                // 6. Finalizar venta con calificación
                                if (orderController.finalizeSale(saleId, r)) {
                                    showAlert("Venta Completada",
                                            "La venta se ha registrado correctamente",
                                            Alert.AlertType.INFORMATION);
                                } else {
                                    showAlert("Error",
                                            "No se pudo registrar la calificación",
                                            Alert.AlertType.ERROR);
                                }
                            });
                        } else {
                            showAlert("Error",
                                    "No se pudieron agregar los productos a la venta",
                                    Alert.AlertType.ERROR);
                        }
                    }
                });
            }

            private Optional<Double> showRatingDialog() {
                Dialog<Double> dialog = new Dialog<>();
                dialog.setTitle("Calificar Venta");
                dialog.setHeaderText("Por favor califique la experiencia del cliente");

                // Configurar botones
                ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

                // Crear slider para calificación
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

                // Configurar resultado
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == okButtonType) {
                        return slider.getValue();
                    }
                    return null;
                });

                return dialog.showAndWait();
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