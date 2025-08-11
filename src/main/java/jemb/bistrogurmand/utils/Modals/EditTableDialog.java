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

public class EditTableDialog extends Dialog<TableRestaurant> {

    private static final int VALIDATION_DELAY_MS = 250;

    private PauseTransition numberPause;
    private PauseTransition seatsPause;
    private PauseTransition locationPause;

    public EditTableDialog(TableRestaurant selectedTable) {
        setTitle("Editar Mesa");
        setHeaderText("Editando mesa: " + selectedTable.getNumberTable());

        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/table.png")));

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        GridPane grid = createFormGrid(selectedTable);
        getDialogPane().setContent(grid);

        TextField numberField = (TextField) grid.getChildren().get(1);
        TextField seatsField = (TextField) grid.getChildren().get(3);
        TextField locationField = (TextField) grid.getChildren().get(5);
        HBox stateHBox = (HBox) grid.getChildren().get(7);

        Node saveButton = getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Configurar pausas para validación
        numberPause = createValidationPause(numberField, this::validateNumberField);
        seatsPause = createValidationPause(seatsField, this::validateSeatsField);
        locationPause = createValidationPause(locationField, this::validateLocationField);

        // Listeners para activar validación pausada
        numberField.textProperty().addListener((obs, oldVal, newVal) -> {
            removeValidationStyles(numberField);
            numberPause.playFromStart();
        });
        seatsField.textProperty().addListener((obs, oldVal, newVal) -> {
            removeValidationStyles(seatsField);
            seatsPause.playFromStart();
        });
        locationField.textProperty().addListener((obs, oldVal, newVal) -> {
            removeValidationStyles(locationField);
            locationPause.playFromStart();
        });
        // Estado - validación inmediata al cambiar selección
        ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup().selectedToggleProperty()
                .addListener((obs, oldVal, newVal) -> validateForm(numberField, seatsField, locationField, stateHBox, saveButton));

        // Validación inicial
        validateForm(numberField, seatsField, locationField, stateHBox, saveButton);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateTableFromForm(selectedTable, grid);
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

        TextField numberField = createTextField(String.valueOf(table.getNumberTable()));
        TextField seatsField = createTextField(String.valueOf(table.getNumberSeats()));
        TextField locationField = createTextField(table.getLocation());

        ToggleGroup toggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(toggleGroup, table.getState());

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

    private HBox createStateRadioButtons(ToggleGroup toggleGroup, String currentState) {
        RadioButton activeRadio = new RadioButton("Activo");
        activeRadio.setUserData("1");

        RadioButton inactiveRadio = new RadioButton("Inactivo");
        inactiveRadio.setUserData("0");

        activeRadio.setToggleGroup(toggleGroup);
        inactiveRadio.setToggleGroup(toggleGroup);

        if ("1".equals(currentState)) {
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        return new HBox(5, activeRadio, inactiveRadio);
    }


    private PauseTransition createValidationPause(TextField field, Runnable validationMethod) {
        PauseTransition pause = new PauseTransition(Duration.millis(VALIDATION_DELAY_MS));
        pause.setOnFinished(e -> validationMethod.run());
        return pause;
    }

    private void removeValidationStyles(TextField field) {
        field.getStyleClass().removeAll("input-error", "input-valid");
    }

    private void validateNumberField() {
        // Validar número de mesa
        TextField numberField = findTextField(1);
        if (numberField == null) return;

        String text = numberField.getText().trim();
        if (text.isEmpty() || !isIntegerPositive(text)) {
            setFieldError(numberField, true);
        } else {
            setFieldError(numberField, false);
        }
        validateForm(numberField,
                findTextField(3),
                findTextField(5),
                (HBox) findNode(7),
                getDialogPane().lookupButton(new ButtonType("Guardar", ButtonData.OK_DONE)));
    }

    private void validateSeatsField() {
        TextField seatsField = findTextField(3);
        if (seatsField == null) return;

        String text = seatsField.getText().trim();
        if (text.isEmpty() || !isIntegerPositive(text)) {
            setFieldError(seatsField, true);
        } else {
            setFieldError(seatsField, false);
        }
        validateForm(findTextField(1),
                seatsField,
                findTextField(5),
                (HBox) findNode(7),
                getDialogPane().lookupButton(new ButtonType("Guardar", ButtonData.OK_DONE)));
    }

    private void validateLocationField() {
        TextField locationField = findTextField(5);
        if (locationField == null) return;

        String text = locationField.getText().trim();
        if (!text.isEmpty() && text.length() < 2) {
            setFieldError(locationField, true);
        } else {
            setFieldError(locationField, false);
        }
        validateForm(findTextField(1),
                findTextField(3),
                locationField,
                (HBox) findNode(7),
                getDialogPane().lookupButton(new ButtonType("Guardar", ButtonData.OK_DONE)));
    }

    private void setFieldError(TextField field, boolean error) {
        field.getStyleClass().removeAll("input-error", "input-valid");
        if (error) {
            field.getStyleClass().add("input-error");
        } else {
            field.getStyleClass().add("input-valid");
        }
    }

    private void validateForm(TextField numberField, TextField seatsField, TextField locationField, HBox stateHBox, Node saveButton) {
        boolean numberValid = numberField.getStyleClass().contains("input-valid");
        boolean seatsValid = seatsField.getStyleClass().contains("input-valid");
        boolean locationValid = locationField.getStyleClass().contains("input-valid") || locationField.getText().trim().isEmpty();
        boolean stateValid = stateHBox.getChildren().stream()
                .filter(node -> node instanceof RadioButton)
                .map(node -> (RadioButton) node)
                .anyMatch(rb -> rb.isSelected());

        saveButton.setDisable(!(numberValid && seatsValid && locationValid && stateValid));
    }

    private boolean isIntegerPositive(String text) {
        try {
            return Integer.parseInt(text) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private TextField findTextField(int index) {
        if (index < 0 || index >= getDialogPane().getContent().lookupAll(".text-field").size()) return null;
        // Because of how children are added, grid.getChildren() order:
        // 0 Label, 1 numberField, 2 Label, 3 seatsField, 4 Label, 5 locationField, 6 Label, 7 HBox state
        Node node = findNode(index);
        if (node instanceof TextField) return (TextField) node;
        return null;
    }

    private Node findNode(int index) {
        GridPane grid = (GridPane) getDialogPane().getContent();
        return grid.getChildren().stream()
                .filter(node -> GridPane.getColumnIndex(node) == 2 && GridPane.getRowIndex(node) == index/2)
                .findFirst()
                .orElse(null);
    }

    private TableRestaurant updateTableFromForm(TableRestaurant table, GridPane grid) {
        TextField numberField = (TextField) grid.getChildren().get(1);
        TextField seatsField = (TextField) grid.getChildren().get(3);
        TextField locationField = (TextField) grid.getChildren().get(5);
        HBox stateHBox = (HBox) grid.getChildren().get(7);

        table.setNumberTable(Integer.parseInt(numberField.getText()));
        table.setNumberSeats(Integer.parseInt(seatsField.getText()));
        table.setLocation(locationField.getText());

        ToggleGroup toggleGroup = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup();
        table.setState(toggleGroup.getSelectedToggle().getUserData().toString());

        return table;
    }

}
