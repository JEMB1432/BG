package jemb.bistrogurmand.utils.Modals;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.utils.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditWaiterDialog extends Dialog<User> {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField phoneField;
    private ToggleGroup stateToggleGroup;
    private ComboBox<String> rolComboBox;

    // Patrones de validación consistentes con AddWaiterDialog
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{1,3}?\\s?[0-9]{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Mapa para pausas de validación
    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();

    public EditWaiterDialog(User selected) {
        setTitle("Editar Mesero");
        setHeaderText("Editando mesero: " + selected.getFirstName() + " " + selected.getLastName());

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/user.png")));

        // Botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid(selected);
        getDialogPane().setContent(grid);

        // Configurar validaciones
        setupValidationPauses();

        // Validar campos inicialmente
        validateFields();

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateUserFromForm(selected);
            }
            return null;
        });
    }

    private GridPane createFormGrid(User selected) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario con placeholders y clases CSS específicas
        firstNameField = createTextField(selected.getFirstName(), "Ej: Juan");
        lastNameField = createTextField(selected.getLastName(), "Ej: Pérez López");
        emailField = createTextField(selected.getEmail(), "Ej: juan@example.com");
        phoneField = createTextField(selected.getPhone(), "Ej: +52 5512345678");

        // Grupo de radio buttons para estado (Activo/Inactivo)
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, selected.getStateUser());

        // ComboBox para rol
        rolComboBox = createRolComboBox(selected.getRolUser());

        // Etiquetas con estilos
        Label firstNameLabel = new Label("Nombre:");
        firstNameLabel.getStyleClass().add("form-label");

        Label lastNameLabel = new Label("Apellidos:");
        lastNameLabel.getStyleClass().add("form-label");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("form-label");

        Label phoneLabel = new Label("Teléfono:");
        phoneLabel.getStyleClass().add("form-label");

        Label stateLabel = new Label("Estado:");
        stateLabel.getStyleClass().add("form-label");

        Label rolLabel = new Label("Rol:");
        rolLabel.getStyleClass().add("form-label");

        // Añadir controles al grid
        grid.add(firstNameLabel, 1, 0);
        grid.add(firstNameField, 2, 0);

        grid.add(lastNameLabel, 4, 0);
        grid.add(lastNameField, 5, 0);

        grid.add(emailLabel, 1, 1);
        grid.add(emailField, 2, 1);

        grid.add(phoneLabel, 4, 1);
        grid.add(phoneField, 5, 1);

        grid.add(stateLabel, 1, 2);
        grid.add(stateHBox, 2, 2);

        grid.add(rolLabel, 4, 2);
        grid.add(rolComboBox, 5, 2);

        return grid;
    }

    private TextField createTextField(String initialValue, String promptText) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().addAll("text-field", "table-field");
        textField.setPromptText(promptText);
        return textField;
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

        return new HBox(5, activeRadio, inactiveRadio);
    }

    private ComboBox<String> createRolComboBox(String currentRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getStyleClass().add("combo-box");
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");
        rolComboBox.getStyleClass().add("combo-box");

        switch (currentRol.toLowerCase()) {
            case "admin" -> rolComboBox.setValue("Admin");
            case "lider" -> rolComboBox.setValue("Lider");
            default -> rolComboBox.setValue("Mesero");
        }

        return rolComboBox;
    }

    private void setupValidationPauses() {
        initValidationPause(firstNameField, NAME_PATTERN, 250);
        initValidationPause(lastNameField, NAME_PATTERN, 250);
        initValidationPause(emailField, EMAIL_PATTERN, 250);
        initValidationPause(phoneField, PHONE_PATTERN, 250);
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            validateField(field, pattern);
            validateFields();
        });
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            field.getStyleClass().removeAll("input-error", "input-valid");
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validateField(TextField field, Pattern pattern) {
        String text = field.getText().trim();
        boolean isValid = !text.isEmpty() && pattern.matcher(text).matches();

        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) {
            return;
        }

        if (isValid) {
            field.getStyleClass().add("input-valid");
        } else {
            field.getStyleClass().add("input-error");
        }
    }

    private void validateFields() {
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));

        boolean firstNameValid = !firstNameField.getText().trim().isEmpty() &&
                NAME_PATTERN.matcher(firstNameField.getText().trim()).matches();

        boolean lastNameValid = !lastNameField.getText().trim().isEmpty() &&
                NAME_PATTERN.matcher(lastNameField.getText().trim()).matches();

        boolean emailValid = !emailField.getText().trim().isEmpty() &&
                EMAIL_PATTERN.matcher(emailField.getText().trim()).matches();

        boolean phoneValid = !phoneField.getText().trim().isEmpty() &&
                PHONE_PATTERN.matcher(phoneField.getText().trim()).matches();

        boolean stateValid = stateToggleGroup.getSelectedToggle() != null;
        boolean rolValid = rolComboBox.getValue() != null;

        boolean isValid = firstNameValid && lastNameValid && emailValid && phoneValid && stateValid && rolValid;
        saveButton.setDisable(!isValid);
    }

    private User updateUserFromForm(User user) {
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPhone(phoneField.getText().trim());
        user.setStateUser(stateToggleGroup.getSelectedToggle().getUserData().toString());
        user.setRolUser(rolComboBox.getValue());

        return user;
    }
}