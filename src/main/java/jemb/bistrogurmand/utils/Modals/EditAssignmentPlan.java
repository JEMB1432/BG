package jemb.bistrogurmand.utils.Modals;

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EditAssignmentPlan extends Stage {
    private ComboBox<User> cbEmployees;
    private ComboBox<String> cbShifts;
    private ComboBox<TableRestaurant> cbTables;

    private List<User> allUsers;
    private List<TableRestaurant> allTables;

    private Map<Integer, Long> assignedWaiterCounts;
    private Set<Integer> assignedTableIds;

    private final int MAX_TABLES_PER_WAITER = 3;
    private PlanificationRestaurant assignmentToEdit; // La asignación que estamos editando

    public EditAssignmentPlan(PlanificationRestaurant assignmentToEdit) {
        this.assignmentToEdit = assignmentToEdit; // Guardamos la asignación original
        this.setTitle("Editar Asignación");
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);

        Label lblEmployees = new Label("Mesero:");
        Label lblShifts = new Label("Turno:");
        Label lblTables = new Label("Mesa:");

        lblEmployees.setStyle("-fx-font-weight: bold;");
        lblShifts.setStyle("-fx-font-weight: bold;");
        lblTables.setStyle("-fx-font-weight: bold;");

        cbEmployees = new ComboBox<>();
        cbShifts = new ComboBox<>();
        cbTables = new ComboBox<>();

        // Configuración de CellFactory para Meseros
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

                    // --- Lógica de deshabilitación ajustada para edición ---
                    // Un mesero se deshabilita si tiene MAX_TABLES_PER_WAITER asignaciones
                    // Y esta asignación NO es la que estamos editando.
                    boolean isCurrentAssignment = (assignmentToEdit != null && assignmentToEdit.getID_Employee() == employeeId);
                    if (currentAssignments >= MAX_TABLES_PER_WAITER && !isCurrentAssignment) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #9a9a9a;");
                    } else if (currentAssignments >= MAX_TABLES_PER_WAITER && isCurrentAssignment) {
                        // Si es el mesero actual y ya tiene 3 asignaciones (incluyendo la actual),
                        // pero no podemos añadir más, solo lo mostramos como habilitado si es el seleccionado
                        // para que pueda permanecer seleccionado, pero no se puedan elegir otros si ya está en el límite
                        setDisable(false); // Permitir que siga seleccionado, pero no seleccionar otros en el límite
                        setStyle(null);
                    } else {
                        setDisable(false);
                        setStyle(null);
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

        // Configuración de CellFactory para Mesas
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
                    // --- Lógica de deshabilitación ajustada para edición ---
                    // Una mesa se deshabilita si está asignada a OTRA asignación que no es la que estamos editando.
                    boolean isCurrentTable = (assignmentToEdit != null && assignmentToEdit.getTableNumber() == item.getNumberTable());
                    if (assignedTableIds != null && assignedTableIds.contains(item.getNumberTable()) && !isCurrentTable) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #9a9a9a;");
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
        cbShifts.valueProperty().addListener((obs, oldShift, newShift) -> {
            if (newShift != null) {
                updateAvailableResources(newShift);
            } else {
                assignedWaiterCounts = null;
                assignedTableIds = null;
                cbEmployees.getItems().setAll(allUsers);
                cbTables.getItems().setAll(allTables);
            }
            // Importante: No limpiar selección si se está editando y el turno es el mismo que el original.
            // La selección inicial se hará después de cargar los datos.
            // cbEmployees.getSelectionModel().clearSelection();
            // cbTables.getSelectionModel().clearSelection();
        });

        Button btnSave = new Button("Actualizar Asignación"); // Texto del botón cambiado
        btnSave.getStyleClass().add("button-confirmar");
        btnSave.setOnAction(e -> updateAssignment()); // Nuevo método para la actualización

        Button btnCancel = new Button("Cancelar");
        btnCancel.getStyleClass().add("button-cancelar");
        btnCancel.setOnAction(e -> this.close());

        HBox buttons = new HBox(10, btnSave, btnCancel);
        buttons.setAlignment(Pos.CENTER);

        VBox employeeBox = new VBox(5, lblEmployees, cbEmployees);
        VBox shiftBox = new VBox(5, lblShifts, cbShifts);
        VBox tableBox = new VBox(5, lblTables, cbTables);

        HBox topBox = new HBox(5);
        topBox.getStyleClass().add("header-bar");
        topBox.setAlignment(Pos.CENTER);
        VBox layout = new VBox(15, topBox, shiftBox, employeeBox, tableBox, buttons);
        layout.setPadding(new Insets(5));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        scene.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/modalcard.css").toExternalForm());
        this.setScene(scene);
        loadInitialDataAndSetSelection(); // Nuevo método para cargar datos y preseleccionar
    }

    private void loadInitialDataAndSetSelection() {
        allUsers = TableDAO.EmployeeDAO.getAllWaiters();
        allTables = TableDAO.getAllTables();

        cbEmployees.getItems().setAll(allUsers);
        cbTables.getItems().setAll(allTables);

        // --- Preseleccionar los valores de la asignación a editar ---
        if (assignmentToEdit != null) {
            cbShifts.getSelectionModel().select(assignmentToEdit.getShift());

            // Seleccionar Mesero
            User selectedEmployee = allUsers.stream()
                    .filter(u -> Integer.parseInt(u.getUserID()) == assignmentToEdit.getID_Employee())
                    .findFirst()
                    .orElse(null);
            cbEmployees.getSelectionModel().select(selectedEmployee);

            // Seleccionar Mesa
            TableRestaurant selectedTable = allTables.stream()
                    .filter(t -> t.getNumberTable() == assignmentToEdit.getTableNumber())
                    .findFirst()
                    .orElse(null);
            cbTables.getSelectionModel().select(selectedTable);

            // Asegurarse de que la lógica de recursos disponibles se actualice con el turno preseleccionado
            // Esto lo maneja el listener del cbShifts, pero podemos forzarlo si es necesario.
            if (assignmentToEdit.getShift() != null) {
                updateAvailableResources(assignmentToEdit.getShift());
            }
        }
    }

    private void updateAvailableResources(String selectedShift) {
        // Obtener las asignaciones para el turno y la fecha actual
        // Excluir la asignación que estamos editando de la lista de "asignaciones actuales"
        // para que no se cuente a sí misma como una restricción.
        List<PlanificationRestaurant> currentAssignments =
                LeaderAssigController.getAssignmentsForShiftAndDate(selectedShift, LocalDate.now()).stream()
                        .filter(a -> assignmentToEdit == null || a.getID_Assignment() != assignmentToEdit.getID_Assignment())
                        .collect(Collectors.toList());


        // Contar asignaciones por mesero (excluyendo la asignación actual)
        assignedWaiterCounts = currentAssignments.stream()
                .collect(Collectors.groupingBy(PlanificationRestaurant::getID_Employee, Collectors.counting()));
//cambios 1
        // Extraer los IDs de las mesas asignadas (excluyendo la asignación actual)
        assignedTableIds = currentAssignments.stream()
                .map(PlanificationRestaurant::getTableNumber)
                .collect(Collectors.toSet());

        // Ordenar Meseros: Meseros con 3+ asignaciones al final.
        List<User> sortedUsers = new ArrayList<>(allUsers);
        sortedUsers.sort(Comparator.comparing((User user) -> {
            long count = assignedWaiterCounts.getOrDefault(Integer.parseInt(user.getUserID()), 0L);
            return count >= MAX_TABLES_PER_WAITER ? 1 : 0;
        }).thenComparing(User::getFirstName));
        cbEmployees.setItems(FXCollections.observableArrayList(sortedUsers));

        // Ordenar Mesas: Mesas asignadas al final.
        List<TableRestaurant> sortedTables = new ArrayList<>(allTables);
        sortedTables.sort(Comparator.comparing((TableRestaurant table) -> assignedTableIds.contains(table.getNumberTable()) ? 1 : 0)
                .thenComparing(TableRestaurant::getNumberTable));
        cbTables.setItems(FXCollections.observableArrayList(sortedTables));
    }


    private void updateAssignment() {
        User employee = cbEmployees.getValue();
        String shift = cbShifts.getValue();
        TableRestaurant table = cbTables.getValue();

        if (employee != null && shift != null && table != null) {
            int employeeId = Integer.parseInt(employee.getUserID());
            int tableId = Integer.parseInt(table.getID_Table());
            int tableNum=table.getNumberTable();

            // Verificar si los datos seleccionados son idénticos a los originales y no hay cambios.
            // Si no hay cambios, no necesitamos hacer nada.
            if (assignmentToEdit.getID_Employee() == employeeId &&
                    assignmentToEdit.getID_Table() == tableId &&
                    assignmentToEdit.getShift().equals(shift)) {
                showAlert("Sin Cambios", "No se detectaron cambios en la asignación.", Alert.AlertType.INFORMATION);
                this.close();
                return;
            }

            // --- Validaciones para la nueva selección ---

            // 1. Límite de mesas por mesero (excluyendo la asignación original si el mesero no cambia)
            long currentAssignmentsForWaiter = assignedWaiterCounts != null ? assignedWaiterCounts.getOrDefault(employeeId, 0L) : 0L;
            if (employeeId != assignmentToEdit.getID_Employee()) { // Si el mesero está cambiando
                if (currentAssignmentsForWaiter >= MAX_TABLES_PER_WAITER) {
                    showAlert("Límite de Asignaciones", "Este mesero ya tiene el máximo de " + MAX_TABLES_PER_WAITER + " mesas asignadas para este turno.", Alert.AlertType.WARNING);
                    return;
                }
            } else { // Si el mesero NO está cambiando, la asignación original ya se excluyó del conteo
                // por lo que 'currentAssignmentsForWaiter' ya refleja las otras asignaciones.
                if (currentAssignmentsForWaiter >= MAX_TABLES_PER_WAITER) {
                    showAlert("Límite de Asignaciones", "Este mesero ya tiene el máximo de " + MAX_TABLES_PER_WAITER + " mesas asignadas (incluyendo esta).", Alert.AlertType.WARNING);
                    return;
                }
            }


            // 2. Mesa ya asignada (excluyendo la asignación original si la mesa no cambia)
            if (tableId != assignmentToEdit.getID_Table()) { // Si la mesa está cambiando
                if (assignedTableIds != null && assignedTableIds.contains(tableId)) {
                    showAlert("Mesa Asignada", "Esta mesa ya está asignada a otro mesero para el turno y día seleccionados.", Alert.AlertType.WARNING);
                    return;
                }
            }


            // 3. Verificar si la combinación Mesero-Mesa-Turno-Día ya existe en DB para OTRA asignación
            // Es decir, si la nueva combinación coincide con una *existente distinta* de la que estamos editando.
            boolean isDuplicateAssignment = LeaderAssigController.checkIfAssignmentExists(employeeId, tableId, shift, LocalDate.now());
            if (isDuplicateAssignment) {
                // Solo si el duplicado es con otra asignación que no es la que estamos editando.
                // Si es la misma, ya lo manejamos con la comprobación de "sin cambios".
                showAlert("Asignación Duplicada", "Esta combinación de mesero, mesa y turno ya existe en otra asignación para hoy.", Alert.AlertType.WARNING);
                return;
            }

            // Si todo es válido, proceder con la actualización
            boolean success = LeaderAssigController.updateAssignment(assignmentToEdit.getID_Assignment(), employeeId, tableId, shift);
            if (success) {
                showAlert("Actualización Exitosa", "Asignación actualizada correctamente.", Alert.AlertType.INFORMATION);
                this.close();
            } else {
                showAlert("Error de Actualización", "No se pudo actualizar la asignación. Verifique los logs.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Datos Incompletos", "Por favor seleccione mesero, turno y mesa.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
        );
        alert.showAndWait();
    }
}