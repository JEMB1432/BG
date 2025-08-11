package jemb.bistrogurmand.views.waiter;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
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
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.utils.*;
import jemb.bistrogurmand.utils.Modals.CorrectionDetailsDialog;


import java.util.List;

import static jemb.bistrogurmand.utils.HistoryCorrectionColumnFactory.*;

public class HistoryCorrectionsView {
    private final BorderPane view;
    private final TableView<SaleCorrectionSummary> table;
    private static OrderController orderController;
    private final TextField searchField;
    private final Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;
    private final int currentWaiterId;


    private ObservableList<SaleCorrectionSummary> masterSummaryList;
    private ObservableList<SaleCorrectionSummary> currentDisplayedList;

    public HistoryCorrectionsView() {
        User currentUser = new UserSession().getCurrentUser();
        int id = Integer.parseInt(currentUser.getUserID());
        this.currentWaiterId = id;

        masterSummaryList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();

        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/styles.css").toExternalForm());
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.setPadding(new Insets(20));
        orderController = new OrderController();

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
        loadInitialData();

    }

    private void createTopSection() {
        VBox topContent = new VBox();
        topContent.setSpacing(20);

        HBox titleCardView = new HBox();
        titleCardView.getStyleClass().add("title-card-view");
        titleCardView.setSpacing(15);
        titleCardView.setAlignment(Pos.BOTTOM_LEFT);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/correction.png").toString()));
        iconTitle.setFitHeight(48);
        iconTitle.setFitWidth(48);
        Label titleView = new Label("Historial de correcciones");
        titleView.getStyleClass().add("title");
        titleView.setAlignment(Pos.BOTTOM_LEFT);

        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section-dash");
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPrefWidth(Double.MAX_VALUE);
        topBox.setPadding(new Insets(0, 0, 0, 0));

        searchField.setPromptText("Buscar correcciones...");
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
                HistoryCorrectionColumnFactory.createIndexColumn(pagination, rowsPerPage),
                createNumberTableColumn(),
                HistoryCorrectionColumnFactory.createDateColumn(),
                createCountColumn(),
                createNewTotalColumn(),
                HistoryCorrectionColumnFactory.createStatusColumn(),
                createDetailsColumn()
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
        int from = pageIndex * rowsPerPage + 1;
        int to = Math.min((pageIndex + 1) * rowsPerPage, currentDisplayedList.size());
        int total = currentDisplayedList.size();
        paginationInfo.setText(String.format("Mostrando %d-%d de %d resultados", from, to, total));
    }

    private void loadInitialData() {
        refreshTable();
    }

    private void refreshTable() {
        masterSummaryList.setAll(orderController.getCorrectionSummaryByEmployeeId(currentWaiterId));
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<SaleCorrectionSummary> filteredList = FXCollections.observableArrayList();

        if (filter.isEmpty()) {
            filteredList.addAll(masterSummaryList);
        } else {
            for (SaleCorrectionSummary correction : masterSummaryList) {
                if (matchesFilter(correction, filter)) {
                    filteredList.add(correction);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

    private boolean matchesFilter(SaleCorrectionSummary correction, String filter) {
        return correction.getSaleDate().toString().toLowerCase().contains(filter) ||
                (correction.getTableNumber() + "").contains(filter) ||
                (correction.getNewTotal() + "").contains(filter) ||
                (correction.getStatus()).contains(filter) ||
                (correction.getCorrectionCount() + "").contains(filter);
    }

    private void updatePagination() {
        int itemCount = currentDisplayedList.size();
        int pageCount = (int) Math.ceil((double) itemCount / rowsPerPage);

        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        if (pagination.getCurrentPageIndex() >= pageCount && pageCount > 0) {
            pagination.setCurrentPageIndex(0);
        }

        updateTableForPage(pagination.getCurrentPageIndex());
    }

    private void updateTableForPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, currentDisplayedList.size());

        if (currentDisplayedList.isEmpty()) {
            table.setItems(FXCollections.observableArrayList());
        } else {
            table.setItems(FXCollections.observableArrayList(currentDisplayedList.subList(fromIndex, toIndex)));
        }

        updatePaginationInfo(pageIndex);
        table.refresh();
    }

    private  TableColumn<SaleCorrectionSummary, String> createNumberTableColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Mesa");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf("Mesa: " + cd.getValue().getTableNumber())));
        return col;
    }

    private  TableColumn<SaleCorrectionSummary, String> createCountColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Correcciones");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getCorrectionCount())));
        return col;
    }

    private  TableColumn<SaleCorrectionSummary, String> createOriginalTotalColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Total Original");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getOriginalTotal())));
        return col;
    }

    private  TableColumn<SaleCorrectionSummary, String> createNewTotalColumn() {
        TableColumn<SaleCorrectionSummary, String> col = new TableColumn<>("Nuevo Total");
        col.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.2f", cd.getValue().getNewTotal())));
        return col;
    }

    private  TableColumn<SaleCorrectionSummary, Void> createDetailsColumn() {
        TableColumn<SaleCorrectionSummary, Void> col = new TableColumn<>("Detalles");
        col.setStyle("-fx-alignment: center");
        col.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Ver Detalles");

            {
                btn.getStyleClass().add("btn-detils");
                btn.setOnAction(event -> {
                    SaleCorrectionSummary summary = getTableView().getItems().get(getIndex());
                    showCorrectionDetails(summary.getSaleId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
        return col;
    }

    private static void showCorrectionDetails(int saleId) {
        try {
            List<OrderRestaurant> details = orderController.getCorrectionDetailsBySaleId(saleId);
            CorrectionDetailsDialog dialog = new CorrectionDetailsDialog(details);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudieron cargar los detalles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }

}
