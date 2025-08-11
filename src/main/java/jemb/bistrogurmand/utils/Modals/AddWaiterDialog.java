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

public class AddWaiterDialog extends Dialog<User> {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField phoneField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ToggleGroup stateToggleGroup;
    private ComboBox<String> rolComboBox;

    // Patrones de validación como en ProfileView
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{1,3}?\\s?[0-9]{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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

        // Botones
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        GridPane grid = createFormGrid();
        getDialogPane().setContent(grid);

        // Configuración de validaciones
        setupValidationPauses();

        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Habilitar/Deshabilitar botón guardar según validaciones
        validationPauses.values().forEach(pause -> pause.setOnFinished(e -> validateForm(saveButton)));

        // Conversor de resultados
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

        firstNameField = createTextField("");
        lastNameField = createTextField("");
        emailField = createTextField("");
        phoneField = createTextField("");
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("text-field");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().add("text-field");

        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, "1");
        rolComboBox = createRolComboBox("Mesero");

        grid.add(new Label("Nombre:"), 1, 0);
        grid.add(firstNameField, 2, 0);

        grid.add(new Label("Apellidos:"), 4, 0);
        grid.add(lastNameField, 5, 0);

        grid.add(new Label("Email:"), 1, 1);
        grid.add(emailField, 2, 1);

        grid.add(new Label("Teléfono:"), 4, 1);
        grid.add(phoneField, 5, 1);

        grid.add(new Label("Contraseña:"), 1, 2);
        grid.add(passwordField, 2, 2);

        grid.add(new Label("Confirmar Contraseña:"), 4, 2);
        grid.add(confirmPasswordField, 5, 2);

        grid.add(new Label("Estado:"), 1, 3);
        grid.add(stateHBox, 2, 3);

        grid.add(new Label("Rol:"), 4, 3);
        grid.add(rolComboBox, 5, 3);

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

        if ("1".equals(defaultState)) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(5, activeRadio, inactiveRadio);
    }

    private ComboBox<String> createRolComboBox(String defaultRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");
        rolComboBox.setValue(defaultRol);
        return rolComboBox;
    }

    private void setupValidationPauses() {
        initValidationPause(firstNameField, NAME_PATTERN, 250);
        initValidationPause(lastNameField, NAME_PATTERN, 250);
        initValidationPause(phoneField, PHONE_PATTERN, 250);
        initValidationPause(emailField, EMAIL_PATTERN, 250);

        // Validación especial para contraseña
        initValidationPause(passwordField, Pattern.compile("^.{6,}$"), 250); // mínimo 6 caracteres
        initValidationPause(confirmPasswordField, Pattern.compile("^.{6,}$"), 250);
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> validateField(field, pattern));
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
        boolean isValid = pattern.matcher(text).matches();

        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) return;

        if (isValid) {
            field.getStyleClass().add("input-valid");
        } else {
            field.getStyleClass().add("input-error");
        }

        // Caso especial: confirmar contraseña
        if (field == confirmPasswordField) {
            if (!confirmPasswordField.getText().equals(passwordField.getText())) {
                confirmPasswordField.getStyleClass().removeAll("input-valid");
                confirmPasswordField.getStyleClass().add("input-error");
            }
        }
    }

    private void validateForm(Button saveButton) {
        boolean valid =
                NAME_PATTERN.matcher(firstNameField.getText().trim()).matches() &&
                        NAME_PATTERN.matcher(lastNameField.getText().trim()).matches() &&
                        EMAIL_PATTERN.matcher(emailField.getText().trim()).matches() &&
                        PHONE_PATTERN.matcher(phoneField.getText().trim()).matches() &&
                        passwordField.getText().trim().length() >= 6 &&
                        passwordField.getText().equals(confirmPasswordField.getText());

        saveButton.setDisable(!valid);
    }

    private User createUserFromForm() {
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
}
