package jemb.bistrogurmand.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AssignmentColumnFactory {

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

    public static TableColumn<Assignment, String> createButtonsColumn() {
        TableColumn<Assignment, String> column = new TableColumn<>(" ");
        column.setStyle("-fx-alignment: center");
        column.getStyleClass().add("text-column");

        column.setCellFactory(param -> new TableCell<>() {
            private final Button takeButton = new Button("");
            private final Button seeButton = new Button("");
            private final Button editButton = new Button("");
            private final Button deleteButton = new Button("");
            private final HBox buttonsContainer = new HBox(5,takeButton, seeButton, editButton, deleteButton);

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
                    System.out.println("Tomar orden: " + assignment); // Aquí tu lógica de edición
                    // Puedes llamar a un método para editar el assignment
                });

                editButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("Editar: " + assignment); // Aquí tu lógica de edición
                    // Puedes llamar a un método para editar el assignment
                });

                deleteButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("Eliminar: " + assignment); // Aquí tu lógica de eliminación
                    // Puedes llamar a un método para eliminar el assignment
                });

                seeButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    System.out.println("See: " + assignment);
                });
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

}
