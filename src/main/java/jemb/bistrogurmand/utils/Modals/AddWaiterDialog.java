package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.User;

public class AddWaiterDialog extends Dialog<User> {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField phoneField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private ToggleGroup stateToggleGroup;
    private ComboBox<String> rolComboBox;

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

        // Validar campos antes de habilitar el botón Guardar
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

        // Campos del formulario
        firstNameField = createTextField("");
        lastNameField = createTextField("");
        emailField = createTextField("");
        phoneField = createTextField("");
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("text-field");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().add("text-field");

        // Grupo de radio buttons para estado
        stateToggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(stateToggleGroup, "1"); // Por defecto activo

        // ComboBox para rol
        rolComboBox = createRolComboBox("Mesero");

        // Añadir controles al grid
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

        if (defaultState.equals("1")) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        HBox hbox = new HBox(5, activeRadio, inactiveRadio);
        return hbox;
    }

    private ComboBox<String> createRolComboBox(String defaultRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");
        rolComboBox.setValue(defaultRol);
        return rolComboBox;
    }

    private void validateFields() {
        // Deshabilitar el botón Guardar inicialmente
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));
        saveButton.setDisable(true);

        // Listener para validar campos en tiempo real
        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        emailField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
    }

    private void validateForm(Button saveButton) {
        boolean isValid = !firstNameField.getText().trim().isEmpty()
                && !lastNameField.getText().trim().isEmpty()
                && !emailField.getText().trim().isEmpty()
                && !phoneField.getText().trim().isEmpty()
                && !passwordField.getText().trim().isEmpty()
                && passwordField.getText().equals(confirmPasswordField.getText());

        saveButton.setDisable(!isValid);

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
        } else {
            confirmPasswordField.setStyle("");
        }
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