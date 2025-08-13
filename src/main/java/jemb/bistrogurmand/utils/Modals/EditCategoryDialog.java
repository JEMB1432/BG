package jemb.bistrogurmand.utils.Modals;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.Controllers.CategoryController;
import jemb.bistrogurmand.utils.Category;
import jemb.bistrogurmand.utils.Product;

import java.util.Optional;

public class EditCategoryDialog extends Dialog<Void> {

    private final CategoryController categoryController = new CategoryController();
    private TableView<Category> categoriesTable;

    public EditCategoryDialog() {
        setTitle("Editar Categorías");
        setHeaderText("Seleccione una categoría para editar");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/category.png")));

        // Configurar botones
        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(closeButtonType);

        // Crear tabla de categorías
        createCategoriesTable();
        loadCategories();

        // Contenedor principal
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.getChildren().add(categoriesTable);

        getDialogPane().setPrefWidth(600);
        getDialogPane().setContent(content);
    }

    private void createCategoriesTable() {
        categoriesTable = new TableView<>();
        categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        categoriesTable.setPrefHeight(400);

        // Columna ID
        TableColumn<Category, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID_Category"));
        idColumn.setStyle("-fx-alignment: CENTER;");

        // Columna Nombre
        TableColumn<Category, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Columna Estado
        TableColumn<Category, String> stateColumn = new TableColumn<>("Estado");
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateColumn.setStyle("-fx-alignment: CENTER;");
        stateColumn.setCellFactory(column -> new TableCell<Category, String>() {
            @Override
            protected void updateItem(String state, boolean empty) {
                super.updateItem(state, empty);
                if (empty || state == null) {
                    setGraphic(null);
                } else {
                    String status = state.equals("1") ? "Activo" : "Inactivo";
                    Label label = new Label(status);
                    label.getStyleClass().add(state.equals("1") ? "badge" : "badge-red");
                    setGraphic(label);
                }
            }
        });


        // Columna Acciones
        TableColumn<Category, Void> actionsColumn = new TableColumn<>("Acciones");
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.setCellFactory(param -> new TableCell<Category, Void>() {
            private final Button editButton = new Button("Editar");

            {
                editButton.getStyleClass().add("btn-detils");
                editButton.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    showEditDialog(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        categoriesTable.getColumns().addAll(nameColumn, stateColumn, actionsColumn);
    }

    private void loadCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList(
                categoryController.getAllCategories()
        );
        categoriesTable.setItems(categories);
    }

    private void showEditDialog(Category category) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Editar Categoría");
        dialog.setHeaderText("Modifique los datos de la categoría");

        // Configurar icono
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/category.png")));

        stage.getScene().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        stage.getScene().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm()
        );

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createEditForm(category);
        dialog.getDialogPane().setContent(grid);

        // Configurar conversor de resultados
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getCategoryData(grid);
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(categoryData -> {
            if (categoryData != null && categoryData.length == 2) {
                category.setName(categoryData[0]);
                category.setState(categoryData[1]);

                if (categoryController.updateCategory(category)) {
                    showAlert("Categoría Actualizada",
                            "La categoría se ha actualizado correctamente",
                            Alert.AlertType.INFORMATION);
                    loadCategories(); // Actualizar tabla
                } else {
                    showAlert("Error",
                            "Ocurrió un error al actualizar la categoría",
                            Alert.AlertType.ERROR);
                }
            }
        });
    }

    private GridPane createEditForm(Category category) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campo de nombre
        TextField nameField = new TextField(category.getName());
        nameField.getStyleClass().add("text-field");
        nameField.setPromptText("Nombre de la categoría");

        // Grupo de radio buttons para estado
        ToggleGroup stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(
                category.getState(),
                stateToggleGroup
        );

        // Guardar referencias para acceso posterior
        nameField.setUserData("nameField");
        stateHBox.setUserData("stateHBox");

        // Añadir controles al grid
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Estado:"), 0, 1);
        grid.add(stateHBox, 1, 1);

        return grid;
    }

    private HBox createStateRadioButtons(String currentState, ToggleGroup toggleGroup) {
        RadioButton activeRadio = new RadioButton("Activo");
        activeRadio.setUserData("1");
        activeRadio.getStyleClass().add("radio-button");

        RadioButton inactiveRadio = new RadioButton("Inactivo");
        inactiveRadio.setUserData("0");
        inactiveRadio.getStyleClass().add("radio-button");

        activeRadio.setToggleGroup(toggleGroup);
        inactiveRadio.setToggleGroup(toggleGroup);

        if ("1".equals(currentState)) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(10, activeRadio, inactiveRadio);
    }

    private String[] getCategoryData(GridPane grid) {
        String[] categoryData = new String[2];

        // Obtener campo de nombre
        grid.getChildren().forEach(node -> {
            if ("nameField".equals(node.getUserData()) && node instanceof TextField) {
                categoryData[0] = ((TextField) node).getText().trim();
            }
        });

        // Obtener estado seleccionado
        grid.getChildren().forEach(node -> {
            if ("stateHBox".equals(node.getUserData()) && node instanceof HBox) {
                HBox hbox = (HBox) node;
                hbox.getChildren().forEach(radioNode -> {
                    if (radioNode instanceof RadioButton) {
                        RadioButton radio = (RadioButton) radioNode;
                        if (radio.isSelected()) {
                            categoryData[1] = radio.getUserData().toString();
                        }
                    }
                });
            }
        });

        return categoryData;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Aplicar estilo CSS si es necesario
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getScene().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        alert.showAndWait();
    }
}