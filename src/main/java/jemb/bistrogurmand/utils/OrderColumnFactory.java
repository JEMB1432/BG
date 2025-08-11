package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import jemb.bistrogurmand.Controllers.OrderChangeController;
import jemb.bistrogurmand.utils.Modals.CorrectionDetailsDialog;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderColumnFactory {

    public static TableColumn<OrderRestaurant, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<OrderRestaurant, Void> indexColumn = new TableColumn<>("#");
        indexColumn.setPrefWidth(20);
        indexColumn.setStyle("-fx-alignment: center-right;");
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

    public static TableColumn<OrderRestaurant, String> createID_EmployeeColumn() {
        TableColumn<OrderRestaurant, String> column = new TableColumn<>("Empleado");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        return column;
    }

    /*public static TableColumn<OrderRestaurant, String> createID_SaleColumn() {
        TableColumn<OrderRestaurant, String> column = new TableColumn<>("Venta");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get));
        return column;
    }*/

    public static TableColumn<OrderRestaurant, String> createDateColumn() {
        TableColumn<OrderRestaurant, String> col = new TableColumn<>("Fecha");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        col.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getSaleDate().format(formatter)
                )
        );

        return col;
    }

    public static TableColumn<OrderRestaurant, String> createID_ProductColumn() {
        TableColumn<OrderRestaurant, String> column = new TableColumn<>("Total actual:");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        return column;
    }

    public static TableColumn<OrderRestaurant, String> createStateColumn() {
        TableColumn<OrderRestaurant, String> column = new TableColumn<>("Estado");
        //column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getApproved())));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    //String status = estado.equals("1") ? "Aprobado" : "Pendiente";
                    String status = switch (estado) {
                        case "1" -> "Aprobado";
                        case "0" -> "Pendiente";
                        case "2" -> "No Aprobado";
                        default -> "Desconocido";
                    };
                    Label label = new Label(status);
                    //label.getStyleClass().add(estado.equals("1") ? "badge" : "badge-red");
                    label.getStyleClass().add(switch (estado) {
                        case "1" -> "badge";
                        case "0" -> "badge-yellow";
                        case "2" -> "badge-red";
                        default -> "badge-red";
                    });
                    setGraphic(label);
                }
            }
        });

        return column;
    }

    public static TableColumn<OrderRestaurant, String> createStatusColumn() {
        TableColumn<OrderRestaurant, String> col = new TableColumn<>("Estado");
        col.setStyle("-fx-alignment: center");
        col.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(status);
                    switch (status) {
                        case "Pendiente" -> label.getStyleClass().add("badge-yellow");
                        case "Aprobado" -> label.getStyleClass().add("badge");
                        case "Rechazado" -> label.getStyleClass().add("badge-red");
                        default -> label.getStyleClass().add("badge-red");
                    }
                    setGraphic(label);
                }
            }
        });
        col.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatus()));
        return col;
    }

    public static  TableColumn<OrderRestaurant, String> createCountColumn() {
        TableColumn<OrderRestaurant, String> col = new TableColumn<>("Cambios");
        //col.setPrefWidth(20);
        col.setStyle("-fx-alignment: center");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCorrectionCount())));
        return col;
    }

    public static  TableColumn<OrderRestaurant, String> createOriginalTotalColumn() {
        TableColumn<OrderRestaurant, String> col = new TableColumn<>("Total Original");
        col.setStyle("-fx-alignment: center");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getOriginalTotal())));
        return col;
    }

    public static  TableColumn<OrderRestaurant, String> createNewTotalColumn() {
        TableColumn<OrderRestaurant, String> col = new TableColumn<>("Nuevo Total");
        col.setStyle("-fx-alignment: center");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getNewTotal())));
        return col;
    }

}


