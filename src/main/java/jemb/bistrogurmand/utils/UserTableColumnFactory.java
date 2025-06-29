package jemb.bistrogurmand.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Label;
import jemb.bistrogurmand.views.Admin.User;

public class UserTableColumnFactory {

    public static TableColumn<User, Void> createIndexColumn(
            Pagination pagination,
            int rowsPerPage
    ) {
        TableColumn<User, Void> indexColumn = new TableColumn<>("#");
        indexColumn.setPrefWidth(40);
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
                    int rowIndex = getIndex(); // Índice dentro de la página
                    int globalIndex = (pageIndex * rowsPerPage) + rowIndex + 1;
                    setText(String.valueOf(globalIndex));
                }
            }
        });

        return indexColumn;
    }

    public static TableColumn<User, String> createFirstNameColumn() {
        TableColumn<User, String> column = new TableColumn<>("Nombre");
        column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        return column;
    }

    public static TableColumn<User, String> createLastNameColumn() {
        TableColumn<User, String> column = new TableColumn<>("Apellido");
        column.setPrefWidth(100);
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        return column;
    }

    public static TableColumn<User, String> createPhoneColumn() {
        TableColumn<User, String> column = new TableColumn<>("Teléfono");
        column.setStyle("-fx-alignment: center-right");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        return column;
    }

    public static TableColumn<User, String> createEmailColumn() {
        TableColumn<User, String> column = new TableColumn<>("Email");
        column.setStyle("-fx-alignment: center-left");
        column.getStyleClass().add("text-column-email");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        return column;
    }

    public static TableColumn<User, String> createRolColumn() {
        TableColumn<User, String> column = new TableColumn<>("Rol");
        column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRolUser()));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(rol);
                    switch (rol.toLowerCase()) {
                        case "admin" -> label.getStyleClass().add("role-admin");
                        case "mesero" -> label.getStyleClass().add("role-waiter");
                        case "lider" -> label.getStyleClass().add("role-leader");
                    }
                    setGraphic(label);
                }
            }
        });

        return column;
    }

    public static TableColumn<User, String> createStateColumn() {
        TableColumn<User, String> column = new TableColumn<>("Estado");
        column.setPrefWidth(80);
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStateUser()));

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

