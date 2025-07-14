package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class AssignmentColumnFactory {

    public static TableColumn<Assignment, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<Assignment, Void> indexColumn = new TableColumn<>("#");
        indexColumn.setPrefWidth(30);
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
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTableAssign()));
        return column;
    }

    public static TableColumn<Assignment, String> createEmployeeColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Mesero");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeAssign() ));
        return column;
    }

    public static TableColumn<Assignment, String> createTimeColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Horario");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeStartAssign() + " - " + cellData.getValue().getTimeEndAssign() ));
        return column;
    }

    public static TableColumn<Assignment, String> createShiftColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>("Horario");
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

}
