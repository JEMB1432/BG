package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AddCategoryDialog extends Dialog<String[]> {

    private TextField nameField;
    private ToggleGroup stateToggleGroup;

    public AddCategoryDialog() {
        setTitle("Agregar Nueva Categoría");
        setHeaderText("Complete los datos de la nueva categoría");

        // Configurar estilos (ajusta la ruta según tu estructura de proyecto)
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/category.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid();
        getDialogPane().setContent(grid);

        // Validar campos antes de habilitar el botón Guardar
        validateFields();

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getCategoryData();
            }
            return null;
        });
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campo de nombre
        nameField = new TextField();
        nameField.getStyleClass().add("text-field");
        nameField.setPromptText("Nombre de la categoría");

        // Grupo de radio buttons para estado
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons("1"); // Por defecto Activo

        // Añadir controles al grid
        grid.add(new Label("Nombre:"), 1, 0);
        grid.add(nameField, 2, 0);

        grid.add(new Label("Estado:"), 1, 1);
        grid.add(stateHBox, 2, 1);

        return grid;
    }

    private HBox createStateRadioButtons(String defaultState) {
        RadioButton activeRadio = new RadioButton("Activo");
        activeRadio.setUserData("1");
        activeRadio.getStyleClass().add("radio-button");

        RadioButton inactiveRadio = new RadioButton("Inactivo");
        inactiveRadio.setUserData("0");
        inactiveRadio.getStyleClass().add("radio-button");

        activeRadio.setToggleGroup(stateToggleGroup);
        inactiveRadio.setToggleGroup(stateToggleGroup);

        if ("1".equals(defaultState)) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(10, activeRadio, inactiveRadio);
    }

    private void validateFields() {
        // Deshabilitar el botón Guardar inicialmente
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));
        saveButton.setDisable(true);

        // Validar mientras se escribe
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });
    }

    private String[] getCategoryData() {
        String[] categoryData = new String[2];
        categoryData[0] = nameField.getText().trim(); // Nombre
        categoryData[1] = stateToggleGroup.getSelectedToggle().getUserData().toString(); // Estado
        return categoryData;
    }
}