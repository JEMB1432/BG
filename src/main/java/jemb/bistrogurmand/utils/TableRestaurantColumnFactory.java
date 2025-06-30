package jemb.bistrogurmand.utils;

import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;

public class TableRestaurantColumnFactory {

    public static TableColumn<TableRestaurant, Void> createIndexColumn( Pagination pagination, int rowsPerPage) {
        TableColumn<TableRestaurant, Void> indexColumn = new TableColumn<>("#");
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

    public static TableColumn<TableRestaurant, String> createNumberColumn() {
        TableColumn<TableRestaurant, String> column = new TableColumn<>("Número");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty("Mesa "+cellData.getValue().getNumberTable()));
        return column;
    }

    public static TableColumn<TableRestaurant, String> createNumberSeatsColumn() {
        TableColumn<TableRestaurant, String> column = new TableColumn<>("Capacidad");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumberSeats()+ " personas"));
        return column;
    }

    public static TableColumn<TableRestaurant, String> createLocationColumn() {
        TableColumn<TableRestaurant, String> column = new TableColumn<>("Ubicación");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        return column;
    }

    public static TableColumn<TableRestaurant, String> createStateColumn() {
        TableColumn<TableRestaurant, String> column = new TableColumn<>("Estado");
        //column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getState()));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    String status = estado.equals("1") ? "Activo" : "Inactivo";
                    Label label = new Label(status);
                    label.getStyleClass().add(estado.equals("1") ? "badge" : "badge-red");
                    setGraphic(label);
                }
            }
        });

        return column;
    }
}

