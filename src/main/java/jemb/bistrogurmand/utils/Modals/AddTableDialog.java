package jemb.bistrogurmand.utils.Modals;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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

public class AddTableDialog extends Dialog<TableRestaurant> {
    private TextField numberField;
    private TextField seatsField;
    private TextField locationField;
    private ToggleGroup stateToggleGroup;

    // Validaciones en tiempo real
    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();
    private static final Pattern TABLE_NUMBER_PATTERN = Pattern.compile("^[1-9]\\d{0,2}$"); // 1-999
    private static final Pattern SEATS_PATTERN = Pattern.compile("^[1-9]\\d?$"); // 1-99
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[\\p{L}][\\p{L}\\p{N} .,-]{1,49}$"); // Letras, números, espacios, puntos, comas, guiones

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

        // Configurar validaciones en tiempo real
        setupValidationPauses();

        // Validar campos inicialmente
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

        // Campos del formulario con placeholders y clases CSS específicas
        numberField = createTextField("", "Ej: 1, 15, 101");
        seatsField = createTextField("", "Ej: 2, 4, 8");
        locationField = createTextField("", "Ej: Terraza, Salón Principal");

        // Grupo de radio buttons para estado (Activo/Inactivo)
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, "1"); // Por defecto Activo

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

    private void setupValidationPauses() {
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

        HBox hbox = new HBox(10, activeRadio, inactiveRadio);
        return hbox;
    }

    private void validateFields() {
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));

        boolean numberValid = !numberField.getText().trim().isEmpty() &&
                TABLE_NUMBER_PATTERN.matcher(numberField.getText().trim()).matches();

        boolean seatsValid = !seatsField.getText().trim().isEmpty() &&
                SEATS_PATTERN.matcher(seatsField.getText().trim()).matches();

        boolean locationValid = !locationField.getText().trim().isEmpty() &&
                LOCATION_PATTERN.matcher(locationField.getText().trim()).matches();

        boolean isValid = numberValid && seatsValid && locationValid;
        saveButton.setDisable(!isValid);
    }

    private TableRestaurant createTableFromForm() {
        // Validación final antes de crear la mesa
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
            TableRestaurant newTable = new TableRestaurant();
            newTable.setNumberTable(Integer.parseInt(numberField.getText().trim()));
            newTable.setNumberSeats(Integer.parseInt(seatsField.getText().trim()));
            newTable.setLocation(locationField.getText().trim());
            newTable.setState(stateToggleGroup.getSelectedToggle().getUserData().toString());
            return newTable;
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