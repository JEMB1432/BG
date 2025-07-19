package jemb.bistrogurmand.views.Leader;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality; // Importar Modality
import javafx.stage.Stage; // Importar Stage
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.LeaderAssigController;
import jemb.bistrogurmand.Controllers.PlanificationController;
// import jemb.bistrogurmand.Controllers.TableController;
import jemb.bistrogurmand.Controllers.TableDAO;
import jemb.bistrogurmand.utils.*;
import jemb.bistrogurmand.utils.Modals.EditAssignmentPlan;

import java.util.Optional;

import static jemb.bistrogurmand.utils.PlanificationColumnFactory.createShiftColumn;
import static jemb.bistrogurmand.utils.PlanificationColumnFactory.*;

public class PlanificationView {
    private BorderPane view;
    private TableView<PlanificationRestaurant> table;
    private PlanificationController planificationController;
    private TextField searchField;
    private Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private ObservableList<PlanificationRestaurant> masterTableRestaurantList;
    private ObservableList<PlanificationRestaurant> currentDisplayedList;

    public PlanificationView() {
        masterTableRestaurantList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.setPadding(new Insets(20));
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        planificationController = new PlanificationController();

        searchField = new TextField();
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        pagination = new Pagination();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(event -> filterAndPaginateTable());
            pause.playFromStart();
        });

        createTopSection();
        configureTable();
        configurePagination();
        createBottomSection();

        loadInitialData();
    }

    private void createTopSection() {
        VBox globalSection = new VBox();

        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        HBox titleContent = new HBox();
        titleContent.setAlignment(Pos.BOTTOM_LEFT);
        titleContent.setSpacing(10);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/plan-ico.png").toString()));
        iconTitle.setFitWidth(57);
        iconTitle.setFitHeight(57);
        Label title = new Label("Planificación");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        titleContent.getChildren().addAll(iconTitle, title);

        searchField.setPromptText("Buscar Mesero...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(Double.MAX_VALUE);

        ImageView imageViewAdd = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/add.png").toString()));
        imageViewAdd.setFitHeight(16);
        imageViewAdd.setFitWidth(16);
        Button addbutton = new Button("Asignar mesa");
        addbutton.setGraphic(imageViewAdd);
        addbutton.getStyleClass().add("primary-button");
        addbutton.setOnAction(event -> addTableForm());

        ImageView imageViewUpdate = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/update.png").toString()));
        imageViewUpdate.setFitHeight(16);
        imageViewUpdate.setFitWidth(16);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setGraphic(imageViewUpdate);
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(searchField, addbutton, refreshButton);
        globalSection.getChildren().addAll(titleContent, topBox);
        view.setTop(globalSection);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                PlanificationColumnFactory.createIndexColumn(pagination,rowsPerPage),
                //createNumberPColumn(), // Asumo que esto no es necesario si tienes createTableColumn()
                createEmployeeColumn(),
                createTableColumn(),
                createShiftColumn()
        );
    }

    private void configurePagination() {
        paginationInfo = new Label();

        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateTableForPage(newValue.intValue());
            updatePaginationInfo(newValue.intValue());
        });

        VBox paginationBox = new VBox(10);
        paginationBox.getChildren().addAll(table, paginationInfo, pagination);
        view.setCenter(paginationBox);
    }

    private void updatePaginationInfo(int pageIndex) {
        int from  = pageIndex * rowsPerPage +1;
        int to = Math.min((pageIndex + 1) * rowsPerPage, currentDisplayedList.size());
        int total = currentDisplayedList.size();
        paginationInfo.setText(String.format("Mostrando %d-%d de %d resultados", from, to, total));
    }

    private void loadInitialData(){
        refreshTable();
    }

    private void createBottomSection() {
        HBox buttomBox = new HBox(20);
        buttomBox.getStyleClass().add("bottom-section");
        buttomBox.setAlignment(Pos.CENTER_RIGHT);
        buttomBox.setPadding(new Insets(20, 0, 0, 0));

        // --- Botón de Editar ---
        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("secondary-button");
        editButton.setOnAction(event -> editSelectedAssignment()); // Nuevo método para manejar la edición

        // --- Botón de Eliminar (opcional, si lo quieres reactivar) ---
        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteSelectedAssignment()); // Nuevo método para manejar la eliminación

        Label lblInfoDiaria= new Label("La tabla muestra las asignaciones por día");
        lblInfoDiaria.setStyle("-fx-alignment: center");
        lblInfoDiaria.getStyleClass().add("text-column");

        buttomBox.getChildren().addAll(lblInfoDiaria, editButton, deleteButton); // Añadir los botones
        view.setBottom(buttomBox);
    }

    private void refreshTable() {
        masterTableRestaurantList.setAll(planificationController.getPlanificationRestaurants());
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<PlanificationRestaurant> filteredList = FXCollections.observableArrayList();

        if(filter.isEmpty()) {
            filteredList.addAll(masterTableRestaurantList);
        }else {
            for (PlanificationRestaurant planificationRestaurant : masterTableRestaurantList) {
                if (matchesFilter(planificationRestaurant, filter)){
                    filteredList.add(planificationRestaurant);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

   /* private boolean matchesFilter(PlanificationRestaurant planificationRestaurant, String filter) {
        String name=TableDAO.EmployeeDAO.getEmployeeNameById(planificationRestaurant.getID_Employee());
        String table=TableDAO.getTableNumberById(planificationRestaurant.getID_Table());
        return name.contains(filter) ||
                table.contains(filter) ||
                planificationRestaurant.getShift().contains(filter);
    }*/

    private boolean matchesFilter(PlanificationRestaurant planificationRestaurant, String filter) {
        return planificationRestaurant.getEmployeeName().toLowerCase().contains(filter) ||
                String.valueOf(planificationRestaurant.getTableNumber()).toLowerCase().contains(filter) ||
                planificationRestaurant.getShift().toLowerCase().contains(filter);
    }

    private void updatePagination() {
        int itemCount = currentDisplayedList.size();
        int pageCount = (int) Math.ceil((double) itemCount / rowsPerPage);

        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        if(pagination.getCurrentPageIndex() >= pageCount && pageCount > 0) {
            pagination.setCurrentPageIndex(0);
        }

        updateTableForPage(pagination.getCurrentPageIndex());
    }

    private void updateTableForPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, currentDisplayedList.size());

        if(currentDisplayedList.isEmpty()){
            table.setItems(FXCollections.observableArrayList());
        }else {
            table.setItems(FXCollections.observableArrayList(currentDisplayedList.subList(fromIndex, toIndex)));
        }

        updatePaginationInfo(pageIndex);
        table.refresh();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Aplica el estilo de CSS si tienes un archivo para alertas genéricas
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm()
        );
        alert.showAndWait();
    }

    private void addTableForm() {
        AssignmentDialog dialog = new AssignmentDialog();
        dialog.showAndWait(); // Modal
        refreshTable(); // Refresca la tabla después de que el modal se cierra
    }

    // --- NUEVO MÉTODO PARA EDITAR ASIGNACIÓN ---
    private void editSelectedAssignment() {
        PlanificationRestaurant selectedAssignment = table.getSelectionModel().getSelectedItem();
        if (selectedAssignment != null) {
            // Crea una instancia del nuevo modal de edición
            EditAssignmentPlan dialog = new EditAssignmentPlan(selectedAssignment);
            dialog.showAndWait(); // Muestra el modal de forma modal
            refreshTable(); // Refresca la tabla principal después de que el modal se cierra
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione una asignación de la tabla para editar.", Alert.AlertType.WARNING);
        }
    }

    // --- NUEVO MÉTODO PARA ELIMINAR ASIGNACIÓN (similar a tu código comentado) ---
    private void deleteSelectedAssignment() {
        PlanificationRestaurant selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Eliminar asignación?");
            alert.setContentText("Está a punto de eliminar la asignación de "
                    + selected.getEmployeeName() + " en la Mesa " + selected.getTableNumber()
                    + " para el turno " + selected.getShift() + ". ¿Continuar?");

            alert.getDialogPane().getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/alerts.css").toExternalForm());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = LeaderAssigController.deleteAssignment(selected.getID_Assignment());
                if (success) {
                    showAlert("Eliminación Exitosa", "La asignación ha sido eliminada correctamente.", Alert.AlertType.INFORMATION);
                    refreshTable(); // Refrescar la tabla
                } else {
                    showAlert("Error de Eliminación", "No se pudo eliminar la asignación. Verifique los logs.", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione una asignación de la tabla para eliminar.", Alert.AlertType.WARNING);
        }
    }

    public BorderPane getView() {
        return view;
    }
}