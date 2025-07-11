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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.PlanificationController;
import jemb.bistrogurmand.Controllers.TableController;
import jemb.bistrogurmand.utils.PlanificationColumnFactory;
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.TableRestaurantColumnFactory;

import static jemb.bistrogurmand.utils.PlanificationColumnFactory.*;
import static jemb.bistrogurmand.utils.TableRestaurantColumnFactory.*;

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
        //createBottomSection();

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

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/table.png").toString()));
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
                createNumberPColumn(),
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

    /*private void createBottomSection() {
        HBox buttomBox = new HBox(20);
        buttomBox.getStyleClass().add("bottom-section");
        buttomBox.setAlignment(Pos.CENTER_RIGHT);
        buttomBox.setPadding(new Insets(20, 0, 0, 0));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(event -> editSelectedTable());

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteSelectedTable());

        buttomBox.getChildren().addAll(editButton, deleteButton);
        view.setBottom(buttomBox);
    }*/

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

    private boolean matchesFilter(PlanificationRestaurant planificationRestaurant, String filter) {
        return planificationRestaurant.getID_Assignment().toString().contains(filter) ||
                planificationRestaurant.getID_Employee().toString().contains(filter) ||
                planificationRestaurant.getID_Table().toString().contains(filter);
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

    /*private void editSelectedTable() {
        TableRestaurant selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Implementar lógica de edición
            System.out.println("Editar mesa: " + selected.getNumberTable().toString());
        } else {
            showAlert("Selección requerida", "Por favor seleccione una mesa para editar.");
        }
    }

    private void deleteSelectedTable() {

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
*/
    private void addTableForm() {
            AssignmentDialog dialog = new AssignmentDialog();
            dialog.showAndWait(); // Modal
        updateTableForPage(1);
    }

   /* Stage modal = new Stage();
    modal.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
*/

    public BorderPane getView() {
        return view;
    }
}