package jemb.bistrogurmand.utils;

import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;

public class PlanificationColumnFactory {

    public static TableColumn<PlanificationRestaurant, Void> createIndexColumn( Pagination pagination, int rowsPerPage) {
        TableColumn<PlanificationRestaurant, Void> indexColumn = new TableColumn<>("#");
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

    public static TableColumn<PlanificationRestaurant, String> createNumberPColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Numero");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(""+cellData.getValue().getID_Assignment()));
        return column;
    }

    public static TableColumn<PlanificationRestaurant, String> createEmployeeColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesero");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty("Mesero "+cellData.getValue().getID_Employee()));
        return column;
    }

    public static TableColumn<PlanificationRestaurant, String> createTableColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesas");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty("Mesa "+cellData.getValue().getID_Table()));
        return column;
    }

    public static TableColumn<PlanificationRestaurant, String> createShiftColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Turno");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));

        column.setCellFactory(col -> new TableCell<>() {
            private void updateItem(String hora) {
                if (hora.startsWith("08") || hora.startsWith("09")) {
                    String shift = "Mañana";
                    Label label = new Label(shift);
                    //label.getStyleClass().add(estado.equals("1") ? "badge" : "badge-red");
                    setGraphic(null);
                }
            }
        })
        ;return column;
    }


    /*public static TableColumn<PlanificationRestaurant, String> createShiftColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Turno");
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
    }*/

}

