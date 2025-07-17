package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class ProductColumnFactory {

    public static TableColumn<Product, Void> createIndexColumn(Pagination pagination, int rowsPerPage) {
        TableColumn<Product, Void> indexColumn = new TableColumn<>("#");
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

    public static TableColumn<Product, String> createNameColumn() {
        TableColumn<Product, String> column = new TableColumn<>("Nombre");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        return column;
    }

    public static TableColumn<Product, String> createDescriptionColumn() {
        TableColumn<Product, String> column = new TableColumn<>("Descripción");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        return column;
    }

    public static TableColumn<Product, String> createPriceColumn() {
        TableColumn<Product, String> column = new TableColumn<>("Precio");
        //column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice() + ""));
        return column;
    }

    public static TableColumn<Product, String> createStateColumn() {
        TableColumn<Product, String> column = new TableColumn<>("Estado");
        //column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailable()));

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
