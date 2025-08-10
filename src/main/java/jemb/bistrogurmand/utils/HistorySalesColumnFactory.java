package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class HistorySalesColumnFactory {
    public static TableColumn<Sale, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<Sale, Void> indexColumn = new TableColumn<>("#");
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

    public static TableColumn<Sale, String> createDateColumn() {
        TableColumn<Sale, String> col = new TableColumn<>("Fecha");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        col.setCellValueFactory(cd -> {
            Timestamp ts = cd.getValue().getSaleDate(); // java.sql.Timestamp
            if (ts != null) {
                String formatted = ts.toLocalDateTime().format(formatter);
                return new SimpleStringProperty(formatted);
            } else {
                return new SimpleStringProperty("");
            }
        });

        return col;
    }

    public static   TableColumn<Sale, String> createTotalColumn() {
        TableColumn<Sale, String> col = new TableColumn<>("Total");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getTotal())));
        return col;
    }

    public static TableColumn<Sale, String> createRatingColumn() {
        TableColumn<Sale, String> col = new TableColumn<>("Calificación");
        col.setStyle("-fx-alignment: center; -fx-font-size: 14px; -fx-text-fill: #ffb500; -fx-font-weight: bold;");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("★%.1f", cd.getValue().getRating())));
        return col;
    }

    public static TableColumn<Sale, String> createStatusColumn() {
        TableColumn<Sale, String> column = new TableColumn<>("Estado");
        //column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus() +""));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                } else {
                    String status = estado.equals("0") ? "Cerrada" : "Sin cerrar";
                    Label label = new Label(status);
                    label.getStyleClass().add(estado.equals("0") ? "badge" : "badge-red");
                    setGraphic(label);
                }
            }
        });

        return column;
    }

}
