package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.TableRestaurant;

public class AddTableDialog extends Dialog<TableRestaurant> {
    private TextField numberField;
    private TextField seatsField;
    private TextField locationField;
    private ToggleGroup stateToggleGroup;

    public AddTableDialog() {
        setTitle("Agregar Nueva Mesa");
        setHeaderText("Complete los datos de la nueva mesa");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/table.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid();
        getDialogPane().setContent(grid);

        // Validar campos antes de habilitar el botón Guardar
        validateFields();

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createTableFromForm();
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

        // Campos del formulario
        numberField = createTextField("");
        seatsField = createTextField("");
        locationField = createTextField("");

        // Grupo de radio buttons para estado (Activo/Inactivo)
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, "1"); // Por defecto Activo

        // Añadir controles al grid
        grid.add(new Label("Número de Mesa:"), 1, 0);
        grid.add(numberField, 2, 0);

        grid.add(new Label("Número de Asientos:"), 1, 1);
        grid.add(seatsField, 2, 1);

        grid.add(new Label("Ubicación:"), 1, 2);
        grid.add(locationField, 2, 2);

        grid.add(new Label("Estado:"), 1, 3);
        grid.add(stateHBox, 2, 3);

        return grid;
    }

    private TextField createTextField(String initialValue) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().add("text-field");
        return textField;
    }

    private HBox createStateRadioButtons(ToggleGroup toggleGroup, String defaultState) {
        RadioButton activeRadio = new RadioButton("Activo");
        activeRadio.setUserData("1");
        activeRadio.getStyleClass().add("radio-button");

        RadioButton inactiveRadio = new RadioButton("Inactivo");
        inactiveRadio.setUserData("0");
        inactiveRadio.getStyleClass().add("radio-button");

        activeRadio.setToggleGroup(toggleGroup);
        inactiveRadio.setToggleGroup(toggleGroup);

        if (defaultState.equals("1")) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        HBox hbox = new HBox(5, activeRadio, inactiveRadio);
        return hbox;
    }

    private void validateFields() {
        // Deshabilitar el botón Guardar inicialmente
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));
        saveButton.setDisable(true);

        // Listener para validar campos en tiempo real
        numberField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        seatsField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        locationField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
    }

    private void validateForm(Button saveButton) {
        boolean isValid = !numberField.getText().trim().isEmpty()
                && !seatsField.getText().trim().isEmpty()
                && !locationField.getText().trim().isEmpty()
                && isNumeric(numberField.getText())
                && isNumeric(seatsField.getText());

        saveButton.setDisable(!isValid);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private TableRestaurant createTableFromForm() {
        TableRestaurant newTable = new TableRestaurant();
        try {
            newTable.setNumberTable(Integer.parseInt(numberField.getText()));
            newTable.setNumberSeats(Integer.parseInt(seatsField.getText()));
            newTable.setLocation(locationField.getText());
            newTable.setState(stateToggleGroup.getSelectedToggle().getUserData().toString());
            return newTable;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
