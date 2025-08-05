package jemb.bistrogurmand.views.waiter;

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
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.AssignedTableController;
import jemb.bistrogurmand.Controllers.AssignmentController;
import jemb.bistrogurmand.utils.Assignment;
import jemb.bistrogurmand.utils.AssignmentColumnFactory;

import java.time.LocalDate;

import static jemb.bistrogurmand.utils.AssignmentColumnFactory.*;

public class AssignedTablesView extends BorderPane {
    private BorderPane view;
    private TableView<Assignment> table;
    private AssignedTableController assignedTableController;
    private TextField searchField;
    private Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private LocalDate dateSelected = LocalDate.now();

    private ObservableList<Assignment> masterAssignmentList;
    private ObservableList<Assignment> currentDisplayedList;

    public AssignedTablesView() {
        masterAssignmentList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();

        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/styles.css").toExternalForm());
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.setPadding(new Insets(20));
        assignedTableController = new AssignedTableController();

        searchField = new TextField();
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        pagination = new Pagination();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Agregar un pequeño retardo para evitar procesamiento excesivo
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> filterAndPaginateTable());
            pause.playFromStart();
        });



        createTopSection();
        configureTable();
        configurePagination();
        loadInitialData();
    }

    private void createTopSection() {
        VBox topContent = new VBox();
        topContent.setSpacing(20);

        HBox titleCardView = new HBox();
        titleCardView.getStyleClass().add("title-card-view");
        titleCardView.setSpacing(15);
        titleCardView.setAlignment(Pos.BOTTOM_LEFT);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/table.png").toString()));
        iconTitle.setFitHeight(48);
        iconTitle.setFitWidth(48);
        Label titleView = new Label("Mesas Asignadas");
        titleView.getStyleClass().add("title");
        titleView.setAlignment(Pos.BOTTOM_LEFT);

        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section-dash");
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPrefWidth(Double.MAX_VALUE);
        topBox.setPadding(new Insets(0, 0, 0, 0));

        searchField.setPromptText("Buscar asignaciones...");
        searchField.getStyleClass().add("search-field");

        ImageView imageViewUpdate = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/update.png").toString()));
        imageViewUpdate.setFitHeight(16);
        imageViewUpdate.setFitWidth(16);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setGraphic(imageViewUpdate);
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(searchField, refreshButton);
        titleCardView.getChildren().addAll(iconTitle, titleView);
        topContent.getChildren().addAll(titleCardView, topBox);
        view.setTop(topContent);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                AssignmentColumnFactory.createIndexColumn(pagination,rowsPerPage),
                createTableColumn(),
                createShiftColumn(),
                createTimeColumn(),
                createButtonsColumn()
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

    private void refreshTable() {
        masterAssignmentList.setAll(assignedTableController.getAssignments());
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<Assignment> filteredList = FXCollections.observableArrayList();

        if(filter.isEmpty()) {
            filteredList.addAll(masterAssignmentList);
        }else {
            for (Assignment assignment : masterAssignmentList) {
                if (matchesFilter(assignment, filter)){
                    filteredList.add(assignment);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

    private boolean matchesFilter(Assignment assignment, String filter) {
        return assignment.getEmployeeAssign().toLowerCase().contains(filter) ||
                assignment.getTableAssign().toLowerCase().contains(filter) ||
                assignment.getShiftAssign().toLowerCase().contains(filter);
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

    public BorderPane getView() {
        return view;
    }
}
