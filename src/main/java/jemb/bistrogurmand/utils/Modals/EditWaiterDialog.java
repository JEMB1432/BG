package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.User;

public class EditWaiterDialog extends Dialog<User> {

    public EditWaiterDialog(User selected) {
        setTitle("Editar Mesero");
        setHeaderText("Editando mesero: " + selected.getFirstName() + " " + selected.getLastName());

        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/user.png")));

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        GridPane grid = createFormGrid(selected);
        getDialogPane().setContent(grid);

        // Botón de guardar para habilitar/deshabilitar según validaciones
        Node saveButton = getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        TextField firstNameField = (TextField) grid.getChildren().get(1);
        TextField lastNameField = (TextField) grid.getChildren().get(3);
        TextField emailField = (TextField) grid.getChildren().get(5);
        TextField phoneField = (TextField) grid.getChildren().get(7);

        Runnable validate = () -> {
            boolean valid =
                    !firstNameField.getText().trim().isEmpty() &&
                            !lastNameField.getText().trim().isEmpty() &&
                            emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") &&
                            phoneField.getText().matches("^\\d{10}$");
            saveButton.setDisable(!valid);
        };

        firstNameField.textProperty().addListener((obs, o, n) -> validate.run());
        lastNameField.textProperty().addListener((obs, o, n) -> validate.run());
        emailField.textProperty().addListener((obs, o, n) -> validate.run());
        phoneField.textProperty().addListener((obs, o, n) -> validate.run());

        // Prevalidar con datos iniciales
        validate.run();

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (saveButton.isDisabled()) return null; // Evita guardar datos inválidos
                return updateUserFromForm(selected, grid);
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

        TextField firstNameField = createTextField(selected.getFirstName());
        TextField lastNameField = createTextField(selected.getLastName());
        TextField emailField = createTextField(selected.getEmail());
        TextField phoneField = createTextField(selected.getPhone());

        ToggleGroup toggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(toggleGroup, selected.getStateUser());

        ComboBox<String> rolComboBox = createRolComboBox(selected.getRolUser());

        grid.add(new Label("Nombre:"), 1, 0);
        grid.add(firstNameField, 2, 0);

        grid.add(new Label("Apellidos:"), 4, 0);
        grid.add(lastNameField, 5, 0);

        grid.add(new Label("Email:"), 1, 1);
        grid.add(emailField, 2, 1);

        grid.add(new Label("Teléfono:"), 4, 1);
        grid.add(phoneField, 5, 1);

        grid.add(new Label("Estado:"), 1, 2);
        grid.add(stateHBox, 2, 2);

        grid.add(new Label("Rol:"), 4, 2);
        grid.add(rolComboBox, 5, 2);

        return grid;
    }

    private TextField createTextField(String initialValue) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().add("text-field");
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

        if (currentState.equals("1")) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(5, activeRadio, inactiveRadio);
    }

    private ComboBox<String> createRolComboBox(String currentRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");

        switch (currentRol.toLowerCase()) {
            case "admin" -> rolComboBox.setValue("Admin");
            case "lider" -> rolComboBox.setValue("Lider");
            default -> rolComboBox.setValue("Mesero");
        }
        return rolComboBox;
    }

    private User updateUserFromForm(User user, GridPane grid) {
        TextField firstNameField = (TextField) grid.getChildren().get(1);
        TextField lastNameField = (TextField) grid.getChildren().get(3);
        TextField emailField = (TextField) grid.getChildren().get(5);
        TextField phoneField = (TextField) grid.getChildren().get(7);
        HBox stateHBox = (HBox) grid.getChildren().get(9);
        ComboBox<String> rolComboBox = (ComboBox<String>) grid.getChildren().get(11);

        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPhone(phoneField.getText().trim());
        user.setRolUser(rolComboBox.getValue());

        ToggleGroup toggleGroup = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup();
        user.setStateUser(toggleGroup.getSelectedToggle().getUserData().toString());

        return user;
    }
}
