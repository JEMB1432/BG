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
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map; // Importar Map
import java.util.Set;
import java.util.stream.Collectors;

public class AssignmentDialog extends Stage {
    private ComboBox<User> cbEmployees;
    private ComboBox<String> cbShifts;
    private ComboBox<TableRestaurant> cbTables;

    private List<User> allUsers;
    private List<TableRestaurant> allTables;

    // --- CAMBIO CLAVE AQUÍ: Usar un Map para contar asignaciones por mesero ---
    private Map<Integer, Long> assignedWaiterCounts; // Map<ID_Mesero, Cantidad_Mesas_Asignadas>
    private Set<Integer> assignedTableNumbers; // Esto se mantiene igual

    private final int MAX_TABLES_PER_WAITER = 3; // Límite de mesas por mesero

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

        // --- Modificación en CellFactory de Empleados ---
        cbEmployees.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName());
                    int employeeId = Integer.parseInt(item.getUserID());
                    long currentAssignments = assignedWaiterCounts != null ? assignedWaiterCounts.getOrDefault(employeeId, 0L) : 0L;

                    // Deshabilitar y cambiar estilo si el mesero ya tiene el máximo de mesas asignadas
                    if (currentAssignments >= MAX_TABLES_PER_WAITER) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #9a9a9a;"); // Gris para indicar deshabilitado
                    } else {
                        setDisable(false);
                        setStyle(null); // Restaurar estilo normal
                    }
                }
            }
        });

        cbEmployees.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });

        // --- CellFactory de Mesas (se mantiene igual, solo se actualiza la referencia a assignedTableNumbers) ---
        cbTables.setCellFactory(param -> new ListCell<TableRestaurant>() {
            @Override
            protected void updateItem(TableRestaurant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle(null);
                } else {
                    setText("Mesa " + item.getNumberTable());
                    // Deshabilitar y cambiar estilo si la mesa está asignada
                    if (assignedTableNumbers != null && assignedTableNumbers.contains(item.getNumberTable())) { // Usar getID_Table() aquí
                        setDisable(true);
                        setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #9a9a9a;"); // Gris para indicar deshabilitado
                    } else {
                        setDisable(false);
                        setStyle(null);
                    }
                }
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
        // Agregamos un listener para que al cambiar el turno, actualicemos los ComboBox de meseros y mesas
        cbShifts.valueProperty().addListener((obs, oldShift, newShift) -> {
            if (newShift != null) {
                updateAvailableResources(newShift);
            } else {
                // Si no hay turno seleccionado, limpiar las listas de asignados y reiniciar ComboBoxes
                assignedWaiterCounts = null; // Limpiar el mapa
                assignedTableNumbers = null;
                cbEmployees.getItems().setAll(allUsers); // Volver a mostrar todos
                cbTables.getItems().setAll(allTables); // Volver a mostrar todos
            }
            cbEmployees.getSelectionModel().clearSelection(); // Limpiar selección al cambiar turno
            cbTables.getSelectionModel().clearSelection();      // Limpiar selección al cambiar turno
        });


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
        VBox layout = new VBox(15,topBox, shiftBox, employeeBox, tableBox, buttons);
        layout.setPadding(new Insets(5));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/modalcard.css").toExternalForm());
        this.setScene(scene);
        loadAllData();
    }

    private void loadAllData() {
        allUsers = TableDAO.EmployeeDAO.getAllWaiters();
        allTables = TableDAO.getAllTables();

        // Inicialmente, no hay turno seleccionado, así que mostramos todos los usuarios/mesas
        cbEmployees.getItems().setAll(allUsers);
        cbTables.getItems().setAll(allTables);
    }

    private void updateAvailableResources(String selectedShift) {
        // Obtener las asignaciones para el turno y la fecha actual
        List<PlanificationRestaurant> currentAssignments =
                LeaderAssigController.getAssignmentsForShiftAndDate(selectedShift, LocalDate.now());

        // --- NUEVA LÓGICA: Contar asignaciones por mesero ---
        assignedWaiterCounts = currentAssignments.stream()
                .collect(Collectors.groupingBy(PlanificationRestaurant::getID_Employee, Collectors.counting()));

        // Extraer los IDs de meseros y números de mesa de las asignaciones
        assignedTableNumbers = currentAssignments.stream()
                .map(PlanificationRestaurant::getTableNumber) // Usar getID_Table()
                .collect(Collectors.toSet());


        // --- LÓGICA DE ORDENACIÓN PARA MESEROS ---
        List<User> sortedUsers = new ArrayList<>(allUsers);
        sortedUsers.sort(Comparator.comparing((User user) -> {
            long count = assignedWaiterCounts.getOrDefault(Integer.parseInt(user.getUserID()), 0L);
            return count >= MAX_TABLES_PER_WAITER ? 1 : 0; // Poner al final los que ya tienen 3 asignaciones
        }).thenComparing(User::getFirstName));

        cbEmployees.setItems(FXCollections.observableArrayList(sortedUsers));
//Cambio2

        // --- LÓGICA DE ORDENACIÓN PARA MESAS (se mantiene igual, solo se usa getID_Table()) ---
        List<TableRestaurant> sortedTables = new ArrayList<>(allTables);
        sortedTables.sort(Comparator.comparing((TableRestaurant table) -> assignedTableNumbers.contains(table.getNumberTable()) ? 1 : 0) // Usar getID_Table()
                .thenComparing(TableRestaurant::getNumberTable));

        cbTables.setItems(FXCollections.observableArrayList(sortedTables));
    }


    private void saveAssignment() {
        User employee = cbEmployees.getValue();
        String shift = cbShifts.getValue();
        TableRestaurant table = cbTables.getValue();

        if (employee != null && shift != null && table != null) {
            int employeeId = Integer.parseInt(employee.getUserID());
            int tableId = Integer.parseInt(table.getID_Table()); // Usar getID_Table()

            // --- NUEVA VERIFICACIÓN: Límite de mesas por mesero ---
            long currentAssignmentsForWaiter = assignedWaiterCounts != null ? assignedWaiterCounts.getOrDefault(employeeId, 0L) : 0L;
            if (currentAssignmentsForWaiter >= MAX_TABLES_PER_WAITER) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Límite de Asignaciones");
                alert.setHeaderText("Mesero con límite alcanzado");
                alert.setContentText("Este mesero ya tiene el máximo de " + MAX_TABLES_PER_WAITER + " mesas asignadas para este turno.");
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm());
                alert.showAndWait();
                return;
            }

            // Verificar si la combinación Mesero-Mesa-Turno-Día ya está asignada (doble verificación)
            // Esta verificación es crucial para evitar duplicados exactos en la DB.
            boolean isDuplicateAssignment = LeaderAssigController.checkIfAssignmentExists(employeeId, tableId, shift, LocalDate.now());
            if (isDuplicateAssignment) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Asignación Existente");
                alert.setHeaderText("Asignación duplicada");
                alert.setContentText("Este mesero ya está asignado a esta mesa para el turno y día seleccionados.");
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm());
                alert.showAndWait();
                return;
            }

            boolean success = LeaderAssigController.insertAssignment(employeeId, tableId, shift);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Asignación Exitosa");
                alert.setHeaderText(null);
                alert.setContentText("Asignación guardada correctamente.");
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
                );
                alert.showAndWait();
                // Al cerrar el modal, asegúrate de que la vista principal se actualice si es necesario
                this.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Asignación");
                alert.setHeaderText("No se pudo guardar la asignación");
                alert.setContentText("Ha ocurrido un error en la base de datos. Verifique los logs.");
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
                );
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos incompletos");
            alert.setContentText("Por favor seleccione mesero, turno y mesa");
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
            );
            alert.showAndWait();
        }
    }

}