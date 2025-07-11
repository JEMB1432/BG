package jemb.bistrogurmand.views.Leader;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jemb.bistrogurmand.Controllers.LeaderAssigController;
import jemb.bistrogurmand.Controllers.TableDAO;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.User;

public class AssignmentDialog extends Stage {

    private ComboBox<User> cbEmployees;
    private ComboBox<String> cbShifts;
    private ComboBox<TableRestaurant> cbTables;

    public AssignmentDialog() {
        this.setTitle("Asignar mesa a mesero");
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);

        cbEmployees = new ComboBox<>();
        cbShifts = new ComboBox<>();
        cbTables = new ComboBox<>();

        cbShifts.getItems().addAll("Mañana", "Tarde", "Noche");

        Button btnSave = new Button("Asignar");
        btnSave.setOnAction(e -> saveAssignment());

        VBox layout = new VBox(10, cbEmployees, cbShifts, cbTables, btnSave);
        layout.setPadding(new Insets(20));

        this.setScene(new Scene(layout, 300, 250));
        loadData();
    }

    private void loadData() {
        cbEmployees.getItems().setAll(TableDAO.EmployeeDAO.getActiveWaiters());
        cbTables.getItems().setAll(TableDAO.getUnassignedTablesForToday());
        cbEmployees.setItems(FXCollections.observableArrayList(TableDAO.EmployeeDAO.getActiveWaiters()));
        cbTables.setItems(FXCollections.observableArrayList(TableDAO.getUnassignedTablesForToday()));

    }

    private void saveAssignment() {
        User employee = cbEmployees.getValue();
        String shift = cbShifts.getValue();
        TableRestaurant table = cbTables.getValue();

        if (employee != null && shift != null && table != null) {
            LeaderAssigController.insertAssignment(employee.getUserID(), table.getNumberTable(), shift);
            this.close();
        } else {
            // Mensaje de error
        }
    }
}
