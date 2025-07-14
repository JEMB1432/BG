package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
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

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
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

        // Campos del formulario
        TextField firstNameField = createTextField(selected.getFirstName());
        TextField lastNameField = createTextField(selected.getLastName());
        TextField emailField = createTextField(selected.getEmail());
        TextField phoneField = createTextField(selected.getPhone());

        // Grupo de radio buttons para estado
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(toggleGroup, selected.getStateUser());

        // ComboBox para rol
        ComboBox<String> rolComboBox = createRolComboBox(selected.getRolUser());

        // Añadir controles al grid
        grid.add(new Label("Nombre:"), 1, 0);
        grid.add(firstNameField, 2, 0);

        grid.add(new Label("Apellidos:"), 4, 0);
        grid.add(lastNameField, 5, 0);

        grid.add(new Label("Email:"), 1, 1);
        grid.add(emailField, 2, 1);

        grid.add(new Label("Telefono:"), 4, 1);
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

        HBox hbox = new HBox(5, activeRadio, inactiveRadio);
        return hbox;
    }

    private ComboBox<String> createRolComboBox(String currentRol) {
        ComboBox<String> rolComboBox = new ComboBox<>();
        rolComboBox.getItems().addAll("Admin", "Lider", "Mesero");

        if (currentRol.equalsIgnoreCase("admin")) {
            rolComboBox.setValue("Admin");
        } else if (currentRol.equalsIgnoreCase("lider")) {
            rolComboBox.setValue("Lider");
        } else {
            rolComboBox.setValue("Mesero");
        }

        return rolComboBox;
    }

    private User updateUserFromForm(User user, GridPane grid) {
        // Obtener referencias a los controles del formulario
        TextField firstNameField = (TextField) grid.getChildren().get(1);
        TextField lastNameField = (TextField) grid.getChildren().get(3);
        TextField emailField = (TextField) grid.getChildren().get(5);
        TextField phoneField = (TextField) grid.getChildren().get(7);
        HBox stateHBox = (HBox) grid.getChildren().get(9);
        ComboBox<String> rolComboBox = (ComboBox<String>) grid.getChildren().get(11);

        user.setFirstName(firstNameField.getText());
        user.setLastName(lastNameField.getText());
        user.setEmail(emailField.getText());
        user.setPhone(phoneField.getText());
        user.setRolUser(rolComboBox.getValue());

        ToggleGroup toggleGroup = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup();
        user.setStateUser(toggleGroup.getSelectedToggle().getUserData().toString());

        return user;
    }
}
