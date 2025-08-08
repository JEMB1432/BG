package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class HistoryCorrectionColumnFactory {
    public static TableColumn<SaleCorrectionSummary, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<SaleCorrectionSummary, Void> indexColumn = new TableColumn<>("#");
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
                    int rowIndex = getIndex(); // Índice dentro de la página
                    int globalIndex = (pageIndex * rowsPerPage) + rowIndex + 1;
                    setText(String.valueOf(globalIndex));
                }
            }
        });

        return indexColumn;
    }

    public static TableColumn<SaleCorrectionSummary, String> createDateColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Fecha");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        col.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getSaleDate().format(formatter)
                )
        );

        return col;
    }

    public static TableColumn<SaleCorrectionSummary, String> createStatusColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Estado");
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



}
