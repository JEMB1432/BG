package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.TableRestaurant;

public class EditTableDialog extends Dialog<TableRestaurant> {
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

        // Configurar conversor de resultados
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

        // Campos del formulario
        TextField numberField = createTextField(String.valueOf(table.getNumberTable()));
        TextField seatsField = createTextField(String.valueOf(table.getNumberSeats()));
        TextField locationField = createTextField(table.getLocation());

        // Grupo de radio buttons para estado (ACTIVO/INACTIVO)
        ToggleGroup toggleGroup = new ToggleGroup();
        HBox stateHBox = createStateRadioButtons(toggleGroup, table.getState());

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

    private HBox createStateRadioButtons(ToggleGroup toggleGroup, String currentState) {
        RadioButton activeRadio = new RadioButton("Activo");
        activeRadio.setUserData("1"); // 1 para ACTIVO
        activeRadio.getStyleClass().add("radio-button");

        RadioButton inactiveRadio = new RadioButton("Inactivo");
        inactiveRadio.setUserData("0"); // 0 para INACTIVO
        inactiveRadio.getStyleClass().add("radio-button");

        activeRadio.setToggleGroup(toggleGroup);
        inactiveRadio.setToggleGroup(toggleGroup);

        // Establecer el estado actual
        if (currentState.equals("1")){
            activeRadio.setSelected(true);
        } else {
            inactiveRadio.setSelected(true);
        }

        HBox hbox = new HBox(5, activeRadio, inactiveRadio);
        return hbox;
    }

    private TableRestaurant updateTableFromForm(TableRestaurant table, GridPane grid) {
        // Obtener referencias a los controles del formulario
        TextField numberField = (TextField) grid.getChildren().get(1);
        TextField seatsField = (TextField) grid.getChildren().get(3);
        TextField locationField = (TextField) grid.getChildren().get(5);
        HBox stateHBox = (HBox) grid.getChildren().get(7);

        // Actualizar el objeto TableRestaurant
        try {
            table.setNumberTable(Integer.parseInt(numberField.getText()));
            table.setNumberSeats(Integer.parseInt(seatsField.getText()));
            table.setLocation(locationField.getText());

            // Obtener el estado seleccionado (1=Activo, 0=Inactivo)
            ToggleGroup toggleGroup = ((RadioButton) stateHBox.getChildren().get(0)).getToggleGroup();
            table.setState(toggleGroup.getSelectedToggle().getUserData().toString());

            return table;
        } catch (NumberFormatException e) {
            // Manejar error de conversión numérica
            return null;
        }
    }
}
