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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddWaiterDialog extends Dialog<User> {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField phoneField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ToggleGroup stateToggleGroup;
    private ComboBox<String> rolComboBox;

    // Patrones de validación consistentes
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{1,3}?\\s?[0-9]{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$"); // mínimo 6 caracteres

    // Mapa para pausas de validación
    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();

    public AddWaiterDialog() {
        setTitle("Agregar Nuevo Mesero");
        setHeaderText("Complete los datos del nuevo mesero");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/user.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
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
                return createUserFromForm();
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
        firstNameField = createTextField("", "Ej: Juan");
        lastNameField = createTextField("", "Ej: Pérez López");
        emailField = createTextField("", "Ej: juan@example.com");
        phoneField = createTextField("", "Ej: +52 5512345678");

        passwordField = new PasswordField();
        passwordField.getStyleClass().addAll("text-field", "password-field");
        passwordField.setPromptText("Mínimo 6 caracteres");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().addAll("text-field", "password-field");
        confirmPasswordField.setPromptText("Confirme la contraseña");

        // Grupo de radio buttons para estado (Activo/Inactivo)
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, "1");

        // ComboBox para rol
        rolComboBox = createRolComboBox("Mesero");

        // Etiquetas con estilos
        Label firstNameLabel = new Label("Nombre:");
        firstNameLabel.getStyleClass().add("form-label");

        Label lastNameLabel = new Label("Apellidos:");
        lastNameLabel.getStyleClass().add("form-label");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("form-label");

        Label phoneLabel = new Label("Teléfono:");
        phoneLabel.getStyleClass().add("form-label");

        Label passwordLabel = new Label("Contraseña:");
        passwordLabel.getStyleClass().add("form-label");

        Label confirmPasswordLabel = new Label("Confirmar Contraseña:");
        confirmPasswordLabel.getStyleClass().add("form-label");

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

        grid.add(passwordLabel, 1, 2);
        grid.add(passwordField, 2, 2);

        grid.add(confirmPasswordLabel, 4, 2);
        grid.add(confirmPasswordField, 5, 2);

        grid.add(stateLabel, 1, 3);
        grid.add(stateHBox, 2, 3);

        grid.add(rolLabel, 4, 3);
        grid.add(rolComboBox, 5, 3);

        return grid;
    }

    private TextField createTextField(String initialValue, String promptText) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().addAll("text-field", "table-field");
        textField.setPromptText(promptText);
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

        if ("1".equals(defaultState)) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(5, activeRadio, inactiveRadio);
    }

    private ComboBox<String> createRolComboBox(String defaultRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getStyleClass().add("combo-box");
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");
        rolComboBox.getStyleClass().add("combo-box");
        rolComboBox.setValue(defaultRol);
        return rolComboBox;
    }

    private void setupValidationPauses() {
        initValidationPause(firstNameField, NAME_PATTERN, 250);
        initValidationPause(lastNameField, NAME_PATTERN, 250);
        initValidationPause(emailField, EMAIL_PATTERN, 250);
        initValidationPause(phoneField, PHONE_PATTERN, 250);
        initValidationPause(passwordField, PASSWORD_PATTERN, 250);
        initValidationPause(confirmPasswordField, PASSWORD_PATTERN, 250);
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

        // Validación especial para confirmación de contraseña
        if (field == confirmPasswordField || field == passwordField) {
            validatePasswordMatch();
        }
    }

    private void validatePasswordMatch() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordField.getStyleClass().removeAll("input-valid");
            confirmPasswordField.getStyleClass().add("input-error");
        } else if (!confirmPasswordField.getText().isEmpty()) {
            confirmPasswordField.getStyleClass().removeAll("input-error");
            confirmPasswordField.getStyleClass().add("input-valid");
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

        boolean passwordValid = !passwordField.getText().trim().isEmpty() &&
                PASSWORD_PATTERN.matcher(passwordField.getText().trim()).matches();

        boolean confirmPasswordValid = passwordValid &&
                !confirmPasswordField.getText().trim().isEmpty() &&
                passwordField.getText().equals(confirmPasswordField.getText());

        boolean stateValid = stateToggleGroup.getSelectedToggle() != null;
        boolean rolValid = rolComboBox.getValue() != null;

        boolean isValid = firstNameValid && lastNameValid && emailValid && phoneValid &&
                passwordValid && confirmPasswordValid && stateValid && rolValid;

        saveButton.setDisable(!isValid);
    }

    private User createUserFromForm() {
        // Validación final antes de crear el usuario
        List<String> errors = new ArrayList<>();

        if (firstNameField.getText().trim().isEmpty()) {
            errors.add("El nombre es requerido");
        } else if (!NAME_PATTERN.matcher(firstNameField.getText().trim()).matches()) {
            errors.add("Nombre inválido");
        }

        if (lastNameField.getText().trim().isEmpty()) {
            errors.add("Los apellidos son requeridos");
        } else if (!NAME_PATTERN.matcher(lastNameField.getText().trim()).matches()) {
            errors.add("Apellidos inválidos");
        }

        if (emailField.getText().trim().isEmpty()) {
            errors.add("El email es requerido");
        } else if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            errors.add("Email inválido");
        }

        if (phoneField.getText().trim().isEmpty()) {
            errors.add("El teléfono es requerido");
        } else if (!PHONE_PATTERN.matcher(phoneField.getText().trim()).matches()) {
            errors.add("Teléfono inválido");
        }

        if (passwordField.getText().trim().isEmpty()) {
            errors.add("La contraseña es requerida");
        } else if (!PASSWORD_PATTERN.matcher(passwordField.getText().trim()).matches()) {
            errors.add("La contraseña debe tener al menos 6 caracteres");
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errors.add("Las contraseñas no coinciden");
        }

        if (stateToggleGroup.getSelectedToggle() == null) {
            errors.add("Debe seleccionar un estado");
        }

        if (rolComboBox.getValue() == null) {
            errors.add("Debe seleccionar un rol");
        }

        if (!errors.isEmpty()) {
            showAlert("Errores de validación", String.join("\n", errors), Alert.AlertType.ERROR);
            return null;
        }

        User newUser = new User();
        newUser.setFirstName(firstNameField.getText().trim());
        newUser.setLastName(lastNameField.getText().trim());
        newUser.setEmail(emailField.getText().trim());
        newUser.setPhone(phoneField.getText().trim());
        newUser.setRolUser(rolComboBox.getValue());
        newUser.setStateUser(stateToggleGroup.getSelectedToggle().getUserData().toString());

        return newUser;
    }

    public String getPassword() {
        return passwordField.getText();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}