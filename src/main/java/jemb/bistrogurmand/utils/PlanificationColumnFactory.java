package jemb.bistrogurmand.utils;

import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import jemb.bistrogurmand.Controllers.TableDAO;

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
    public static TableColumn<PlanificationRestaurant, String> createEmployeeColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesero");
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmployeeName()));
        return column;
    }

    public static TableColumn<PlanificationRestaurant, String> createTableColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesas");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty("Mesa " + cellData.getValue().getTableNumber()));
        return column;
    }
/*
    public static TableColumn<PlanificationRestaurant, String> createEmployeeColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesero");
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> {
            // Obtener el empleado completo desde la base de datos usando el ID
            int employeeId = cellData.getValue().getID_Employee();
            String employeeName = TableDAO.EmployeeDAO.getEmployeeNameById(employeeId); // Método que debes implementar
            return new SimpleStringProperty(employeeName);
        });

        return column;
    }

    public static TableColumn<PlanificationRestaurant, String> createTableColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Mesas");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> {
            // Obtener la mesa completa desde la base de datos usando el ID
            int tableId = cellData.getValue().getID_Table();
            String tableNumber = TableDAO.getTableNumberById(tableId);// Método que debes implementar
            return new SimpleStringProperty("Mesa " + tableNumber);
        });

        return column;
    }
*/



    public static TableColumn<PlanificationRestaurant, String> createShiftColumn() {
        TableColumn<PlanificationRestaurant, String> column = new TableColumn<>("Turno");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShift()));
return column;
    }


}

