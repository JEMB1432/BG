package jemb.bistrogurmand.views.Admin;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.AssignmentController;
import jemb.bistrogurmand.Controllers.DashboardController;
import jemb.bistrogurmand.utils.Assignment;
import jemb.bistrogurmand.utils.AssignmentColumnFactory;
import jemb.bistrogurmand.utils.RadarChartCustom;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jemb.bistrogurmand.utils.AssignmentColumnFactory.*;

public class GenerateDashboardInfo {
    private BorderPane view;
    private TableView<Assignment> table;
    private AssignmentController assignmentController;
    private TextField searchField;
    private Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private LocalDate dateSelected = LocalDate.now();

    private ObservableList<Assignment> masterAssignmentList;
    private ObservableList<Assignment> currentDisplayedList;

    private DashboardController dashboardController;

    DatePicker datePicker = new DatePicker();

    public GenerateDashboardInfo() {
        masterAssignmentList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/styles.css").toExternalForm());
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/graphic.css").toExternalForm());
        view.setPadding(new Insets(20));
        assignmentController = new AssignmentController();
        dashboardController = new DashboardController();

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

        datePicker.setEditable(false);
        datePicker.setPrefWidth(270);
        datePicker.setValue(LocalDate.now());
        datePicker.getStyleClass().add("date-picker");
        datePicker.setOnAction(e -> {
            dateSelected = datePicker.getValue();
            createTopSection();
            refreshTable();
        });

        createTopSection();
        configureTable();
        configurePagination();

        loadInitialData();
    }

    private void createTopSection() {
        VBox topContent = new VBox();
        topContent.setSpacing(20);
        topContent.getStyleClass().add("top-content");

        HBox titleCardView = new HBox();
        titleCardView.getStyleClass().add("title-card-view");
        titleCardView.setSpacing(15);
        titleCardView.setAlignment(Pos.BOTTOM_LEFT);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/stat.png").toString()));
        iconTitle.setFitHeight(48);
        iconTitle.setFitWidth(48);
        Label titleView = new Label("Dashboard");
        titleView.getStyleClass().add("title");
        titleView.setAlignment(Pos.BOTTOM_LEFT);

        // Sección de gráficos
        HBox chartsContainer = new HBox(20);
        chartsContainer.setPadding(new Insets(20, 0, 0, 0));
        chartsContainer.setAlignment(Pos.CENTER);
        chartsContainer.getStyleClass().add("card-charts");
        chartsContainer.setFillHeight(true);

        // Gráfico de ventas por turno
        VBox salesChartBox = new VBox(10);
        salesChartBox.getStyleClass().add("chart-container");
        BarChart<String, Number> salesChart = dashboardController.createShiftSalesChart(dateSelected);
        salesChartBox.getChildren().addAll(
                new Label(" "),
                salesChart
        );

        Label salesChartTitle = new Label("Gráficas de información: ");
        salesChartTitle.getStyleClass().add("title-chart");
        salesChartTitle.setPrefWidth(Double.MAX_VALUE);

        // Gráfico de calificaciones
        VBox ratingsChartBox = new VBox(10);
        ratingsChartBox.getStyleClass().add("chart-container");
        RadarChartCustom ratingsChart = dashboardController.createEmployeeRatingsChart(dateSelected);
        ratingsChartBox.getChildren().addAll(
                new Label("Evaluación de Empleados"),
                ratingsChart
        );

        HBox.setHgrow(salesChartBox, Priority.ALWAYS);
        HBox.setHgrow(ratingsChartBox, Priority.ALWAYS);
        salesChartBox.setMaxWidth(Double.MAX_VALUE);
        ratingsChartBox.setMaxWidth(Double.MAX_VALUE);

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

        Button todayButton = new Button("Hoy");
        todayButton.getStyleClass().add("primary-button");
        todayButton.setOnAction(e -> {
                    dateSelected = LocalDate.now();
                    createTopSection();
                    refreshTable();
        });

        Label titleTable = new Label("Asignaciones del día: " + dateSelected.toString());
        titleTable.getStyleClass().add("title-table");
        titleTable.setPrefWidth(Double.MAX_VALUE);
        titleTable.setAlignment(Pos.CENTER);

        topBox.getChildren().addAll(todayButton, datePicker, searchField, refreshButton);
        titleCardView.getChildren().addAll(iconTitle, titleView);
        chartsContainer.getChildren().addAll( salesChartBox, ratingsChartBox);
        topContent.getChildren().addAll(titleCardView, createCards(), salesChartTitle, chartsContainer, titleTable, topBox);
        view.setTop(topContent);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");
        table.setMaxHeight(Double.MAX_VALUE); // Permite crecer
        table.setPrefHeight(400);

        table.getColumns().addAll(
                AssignmentColumnFactory.createIndexColumn(pagination,rowsPerPage),
                createTableColumn(),
                createEmployeeColumn(),
                createShiftColumn(),
                createTimeColumn()
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
        masterAssignmentList.setAll(assignmentController.getAssignments(dateSelected));
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

    public HBox createCards(){
        HBox cards = new HBox(50);

        BigDecimal saleInfo = dashboardController.getDailySalesTotal(dateSelected);
        VBox cardSale = new VBox(20);
        cardSale.getStyleClass().add("card-sale");
        Label titleSale = new Label("Ventas totales");
        titleSale.getStyleClass().add("title-card");
        Label totalSale = new Label("$" + saleInfo);
        totalSale.getStyleClass().add("total-card");
        cardSale.getChildren().addAll(titleSale, totalSale);

        int orderInfo = dashboardController.getDailyOrdersTotal(dateSelected);
        VBox cardOrder = new VBox(20);
        cardOrder.getStyleClass().add("card-sale");
        Label titleOrder = new Label("Órdenes totales");
        titleOrder.getStyleClass().add("title-card");
        Label totalOrder = new Label(""+ orderInfo);
        totalOrder.getStyleClass().add("total-card");
        cardOrder.getChildren().addAll(titleOrder, totalOrder);

        cards.getChildren().addAll(cardSale,cardOrder);
        return cards;
    }

    public BorderPane getView() {
        return view;
    }
}