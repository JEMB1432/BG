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
import java.util.Set;
import java.util.stream.Collectors;

public class AssignmentDialog extends Stage {
        private ComboBox<User> cbEmployees;
        private ComboBox<String> cbShifts;
        private ComboBox<TableRestaurant> cbTables;

        private List<User> allUsers;
        private List<TableRestaurant> allTables;

        // Sets para almacenar los IDs de los meseros y números de mesa asignados en el turno actual
        private Set<Integer> assignedWaiterIds;
        private Set<Integer> assignedTableNumbers;

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

            // --- Modificación clave aquí para CellFactory de Empleados ---
            cbEmployees.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setDisable(false); // Asegurarse de que no esté deshabilitado si está vacío
                        setStyle(null); // Limpiar estilo
                    } else {
                        setText(item.getFirstName() + " " + item.getLastName());
                        // Deshabilitar y cambiar estilo si el mesero está asignado
                        if (assignedWaiterIds != null && assignedWaiterIds.contains(Integer.parseInt(item.getUserID()))) {
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

            // --- Modificación clave aquí para CellFactory de Mesas ---
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
                        if (assignedTableNumbers != null && assignedTableNumbers.contains(item.getNumberTable())) {
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
                protected void updateItem(TableRestaurant item, boolean empty) { // **NOTA:** Aquí debería ser TableRestaurant, no User.
                    // Asegúrate de que el tipo de item en setButtonCell
                    // coincida con el tipo de ComboBox (TableRestaurant).
                    // Si tu IDE lo marca como error, cámbialo a TableRestaurant.
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
                    assignedWaiterIds = null;
                    assignedTableNumbers = null;
                    cbEmployees.getItems().setAll(allUsers); // Volver a mostrar todos
                    cbTables.getItems().setAll(allTables); // Volver a mostrar todos
                }
                cbEmployees.getSelectionModel().clearSelection(); // Limpiar selección al cambiar turno
                cbTables.getSelectionModel().clearSelection();     // Limpiar selección al cambiar turno
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
            layout.setPadding(new Insets(20));
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

            // Extraer los IDs de meseros y números de mesa de las asignaciones
            assignedWaiterIds = currentAssignments.stream()
                    .map(PlanificationRestaurant::getID_Employee)
                    .collect(Collectors.toSet());

            assignedTableNumbers = currentAssignments.stream()
                    .map(PlanificationRestaurant::getID_Table)
                    .collect(Collectors.toSet());


            // --- LÓGICA DE ORDENACIÓN PARA MESEROS ---
            List<User> sortedUsers = new ArrayList<>(allUsers);
            sortedUsers.sort(Comparator.comparing((User user) -> assignedWaiterIds.contains(Integer.parseInt(user.getUserID())) ? 1 : 0)
                    .thenComparing(User::getFirstName));

            cbEmployees.setItems(FXCollections.observableArrayList(sortedUsers));


            // --- LÓGICA DE ORDENACIÓN PARA MESAS (CAMBIO AQUÍ) ---
            List<TableRestaurant> sortedTables = new ArrayList<>(allTables);
            sortedTables.sort(Comparator.comparing((TableRestaurant table) -> assignedTableNumbers.contains(table.getNumberTable()) ? 1 : 0)
                    .thenComparing(TableRestaurant::getNumberTable));

            cbTables.setItems(FXCollections.observableArrayList(sortedTables));
        }


        private void saveAssignment() {
            User employee = cbEmployees.getValue();
            String shift = cbShifts.getValue();
            TableRestaurant table = cbTables.getValue();

            if (employee != null && shift != null && table != null) {
                // Verificar si la combinación ya está asignada visualmente (doble verificación)
                if (assignedWaiterIds != null && assignedWaiterIds.contains(Integer.parseInt(employee.getUserID())) &&
                        assignedTableNumbers != null && assignedTableNumbers.contains(table.getNumberTable())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Asignación Existente");
                    alert.setHeaderText("Asignación duplicada");
                    alert.setContentText("Este mesero ya está asignado a esta mesa para el turno y día seleccionados.");
                    alert.showAndWait();
                    return; // No intentar la inserción en la base de datos
                }

                boolean success = LeaderAssigController.insertAssignment(Integer.parseInt(employee.getUserID()), table.getNumberTable(), shift);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Asignación Exitosa");
                    alert.setHeaderText(null);
                    alert.setContentText("Asignación guardada correctamente.");
                    // --- APLICAR ESTILO AL ALERT DE ÉXITO ---
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
                    alert.setContentText("Puede que ya exista una asignación idéntica o ha ocurrido un error en la base de datos.");
                    // --- APLICAR ESTILO AL ALERT DE ERROR ---
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
                // --- APLICAR ESTILO AL ALERT DE ERROR (Datos incompletos) ---
                alert.getDialogPane().getStylesheets().add(
                        getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
                );
                alert.showAndWait();
            }
        }

}