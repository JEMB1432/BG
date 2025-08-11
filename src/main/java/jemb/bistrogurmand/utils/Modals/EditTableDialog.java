package jemb.bistrogurmand.utils.Modals;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EditTableDialog extends Dialog<TableRestaurant> {

    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();
    private static final Pattern TABLE_NUMBER_PATTERN = Pattern.compile("^[1-9]\\d{0,2}$"); // 1-999
    private static final Pattern SEATS_PATTERN = Pattern.compile("^[1-9]\\d?$"); // 1-99
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[\\p{L}][\\p{L}\\p{N} .,-]{1,49}$"); // Letras, números, espacios, puntos, comas, guiones

    public EditTableDialog(TableRestaurant selectedTable) {
        setTitle("Editar Mesa");
        setHeaderText("Editando mesa: " + selectedTable.getNumberTable());

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/table.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid(selectedTable);
        getDialogPane().setContent(grid);

        // Obtener referencias a los campos
        TextField numberField = (TextField) grid.getChildren().get(1);
        TextField seatsField = (TextField) grid.getChildren().get(3);
        TextField locationField = (TextField) grid.getChildren().get(5);
        HBox stateHBox = (HBox) grid.getChildren().get(7);

        // Configurar validaciones en tiempo real
        setupValidationPauses(numberField, seatsField, locationField);

        // Validar campos inicialmente
        validateFields(numberField, seatsField, locationField, stateHBox);

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateTableFromForm(selectedTable, numberField, seatsField, locationField, stateHBox);
            }
            return null;
        });
    }

    private GridPane createFormGrid(TableRestaurant table) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario con placeholders y clases CSS específicas
        TextField numberField = createTextField(String.valueOf(table.getNumberTable()), "Ej: 1, 15, 101");
        TextField seatsField = createTextField(String.valueOf(table.getNumberSeats()), "Ej: 2, 4, 8");
        TextField locationField = createTextField(table.getLocation(), "Ej: Terraza, Salón Principal");

        // Grupo de radio buttons para estado (Activo/Inactivo)
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(toggleGroup, table.getState());

        // Etiquetas con estilos
        Label numberLabel = new Label("Número de Mesa:");
        numberLabel.getStyleClass().add("form-label");

        Label seatsLabel = new Label("Número de Asientos:");
        seatsLabel.getStyleClass().add("form-label");

        Label locationLabel = new Label("Ubicación:");
        locationLabel.getStyleClass().add("form-label");

        Label stateLabel = new Label("Estado:");
        stateLabel.getStyleClass().add("form-label");

        // Añadir controles al grid
        grid.add(numberLabel, 1, 0);
        grid.add(numberField, 2, 0);

        grid.add(seatsLabel, 1, 1);
        grid.add(seatsField, 2, 1);

        grid.add(locationLabel, 1, 2);
        grid.add(locationField, 2, 2);

        grid.add(stateLabel, 1, 3);
        grid.add(stateHBox, 2, 3);

        return grid;
    }

    private TextField createTextField(String initialValue, String promptText) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().addAll("text-field", "table-field");
        textField.setPromptText(promptText);
        return textField;
    }

    private void setupValidationPauses(TextField numberField, TextField seatsField, TextField locationField) {
        initValidationPause(numberField, TABLE_NUMBER_PATTERN, 400);
        initValidationPause(seatsField, SEATS_PATTERN, 400);
        initValidationPause(locationField, LOCATION_PATTERN, 300);
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            validateField(field, pattern);
            validateFields(); // Revalidar el formulario completo
        });
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            // Limpiar estilos previos mientras se escribe
            field.getStyleClass().removeAll("input-error", "input-valid");

            // Reiniciar timer de validación
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validateField(TextField field, Pattern pattern) {
        String text = field.getText().trim();
        boolean isValid = !text.isEmpty() && pattern.matcher(text).matches();

        // Limpiar clases previas
        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) {
            // Campo vacío - sin estilo especial pero es inválido
            return;
        }

        if (isValid) {
            field.getStyleClass().add("input-valid");
        } else {
            field.getStyleClass().add("input-error");
        }
    }

    private HBox createStateRadioButtons(ToggleGroup toggleGroup, String currentState) {
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

        HBox hbox = new HBox(10, activeRadio, inactiveRadio);
        return hbox;
    }

    private void validateFields(TextField numberField, TextField seatsField, TextField locationField, HBox stateHBox) {
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));

        boolean numberValid = !numberField.getText().trim().isEmpty() &&
                TABLE_NUMBER_PATTERN.matcher(numberField.getText().trim()).matches();

        boolean seatsValid = !seatsField.getText().trim().isEmpty() &&
                SEATS_PATTERN.matcher(seatsField.getText().trim()).matches();

        boolean locationValid = !locationField.getText().trim().isEmpty() &&
                LOCATION_PATTERN.matcher(locationField.getText().trim()).matches();

        boolean stateValid = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup().getSelectedToggle() != null;

        boolean isValid = numberValid && seatsValid && locationValid && stateValid;
        saveButton.setDisable(!isValid);
    }

    private void validateFields() {
        GridPane grid = (GridPane) getDialogPane().getContent();
        TextField numberField = (TextField) grid.getChildren().get(1);
        TextField seatsField = (TextField) grid.getChildren().get(3);
        TextField locationField = (TextField) grid.getChildren().get(5);
        HBox stateHBox = (HBox) grid.getChildren().get(7);

        validateFields(numberField, seatsField, locationField, stateHBox);
    }

    private TableRestaurant updateTableFromForm(TableRestaurant table, TextField numberField, TextField seatsField,
                                                TextField locationField, HBox stateHBox) {
        // Validación final antes de actualizar la mesa
        List<String> errors = new ArrayList<>();

        if (numberField.getText().trim().isEmpty()) {
            errors.add("El número de mesa es requerido");
        } else if (!TABLE_NUMBER_PATTERN.matcher(numberField.getText().trim()).matches()) {
            errors.add("Número de mesa inválido (debe ser entre 1-999)");
        }

        if (seatsField.getText().trim().isEmpty()) {
            errors.add("El número de asientos es requerido");
        } else if (!SEATS_PATTERN.matcher(seatsField.getText().trim()).matches()) {
            errors.add("Número de asientos inválido (debe ser entre 1-99)");
        }

        if (locationField.getText().trim().isEmpty()) {
            errors.add("La ubicación es requerida");
        } else if (!LOCATION_PATTERN.matcher(locationField.getText().trim()).matches()) {
            errors.add("Ubicación inválida");
        }

        if (!errors.isEmpty()) {
            showAlert("Errores de validación", String.join("\n", errors), Alert.AlertType.ERROR);
            return null;
        }

        try {
            table.setNumberTable(Integer.parseInt(numberField.getText().trim()));
            table.setNumberSeats(Integer.parseInt(seatsField.getText().trim()));
            table.setLocation(locationField.getText().trim());

            ToggleGroup toggleGroup = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup();
            table.setState(toggleGroup.getSelectedToggle().getUserData().toString());

            return table;
        } catch (NumberFormatException e) {
            showAlert("Error", "Error al procesar los datos numéricos", Alert.AlertType.ERROR);
            return null;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}