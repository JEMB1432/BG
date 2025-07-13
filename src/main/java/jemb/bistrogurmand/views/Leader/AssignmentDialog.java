package jemb.bistrogurmand.views.Leader;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jemb.bistrogurmand.Controllers.LeaderAssigController;
import jemb.bistrogurmand.Controllers.TableDAO;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.User;

import java.util.List;

public class AssignmentDialog extends Stage {

    private ComboBox<User> cbEmployees;
    private ComboBox<String> cbShifts;
    private ComboBox<TableRestaurant> cbTables;
    private List<User> users = TableDAO.EmployeeDAO.getUnassignedWaitersForToday();
    private List<TableRestaurant> tables = TableDAO.getUnassignedTablesForToday();

    public AssignmentDialog() {
        this.setTitle("Asignar mesa a mesero");
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);

        // Crear los labels
        Label lblEmployees = new Label("Meseros:");
        Label lblShifts = new Label("Turnos:");
        Label lblTables = new Label("Mesas:");

        // Configurar estilo de los labels (opcional)
        lblEmployees.setStyle("-fx-font-weight: bold;");
        lblShifts.setStyle("-fx-font-weight: bold;");
        lblTables.setStyle("-fx-font-weight: bold;");

        cbEmployees = new ComboBox<>();
        cbShifts = new ComboBox<>();
        cbTables = new ComboBox<>();

        // Configurar los cell factories para mostrar texto personalizado
        cbEmployees.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });

        cbEmployees.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });

        cbTables.setCellFactory(param -> new ListCell<TableRestaurant>() {
            @Override
            protected void updateItem(TableRestaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Mesa " + item.getNumberTable());
            }
        });

        cbTables.setButtonCell(new ListCell<TableRestaurant>() {
            @Override
            protected void updateItem(TableRestaurant item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Mesa " + item.getNumberTable());
            }
        });

        cbShifts.getItems().addAll("Mañana", "Tarde", "Noche");


        Button btnSave = new Button("Confirmar asignación");
        btnSave.getStyleClass().add("button-confirmar");
        btnSave.setOnAction(e -> saveAssignment());

        Button btnCancel = new Button("Cancelar");
        btnCancel.getStyleClass().add("button-cancelar");
        btnCancel.setOnAction(e -> this.close());

        HBox buttons = new HBox(10, btnSave, btnCancel);
        buttons.setAlignment(Pos.CENTER);


        // Crear contenedores para cada label + combobox
        VBox employeeBox = new VBox(5, lblEmployees, cbEmployees);
        employeeBox.getStyleClass().add("vbox > Label");
        VBox shiftBox = new VBox(5, lblShifts, cbShifts);
        shiftBox.getStyleClass().add("vbox > Label");
        VBox tableBox = new VBox(5, lblTables, cbTables);
        tableBox.getStyleClass().add("vbox > Label");

        // Configurar el layout principal
        HBox topBox = new HBox(5);
        topBox.getStyleClass().add("header-bar");
        topBox.setAlignment(Pos.CENTER);
        VBox layout = new VBox(15,topBox, employeeBox, shiftBox, tableBox, buttons);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/modalcard.css").toExternalForm());
        this.setScene(scene);
        loadData();
    }

    private void loadData() {
        cbEmployees.getItems().setAll(users);
        cbTables.getItems().setAll(tables);
    }

    private void saveAssignment() {
        User employee = cbEmployees.getValue();
        String shift = cbShifts.getValue();
        TableRestaurant table = cbTables.getValue();

        if (employee != null && shift != null && table != null) {
            LeaderAssigController.insertAssignment(employee.getUserID(), table.getNumberTable(), shift);
            this.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos incompletos");
            alert.setContentText("Por favor seleccione empleado, turno y mesa");
            alert.showAndWait();
        }
    }
}