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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jemb.bistrogurmand.Controllers.OrderController;
import jemb.bistrogurmand.Controllers.ProductController;
import jemb.bistrogurmand.Controllers.SaleController;
import jemb.bistrogurmand.utils.*;

import java.util.Optional;

import static jemb.bistrogurmand.utils.HistorySalesColumnFactory.*;

public class HistorySalesView {
    private final BorderPane view;
    private final TableView<Sale> table;
    private final SaleController saleController;
    private final TextField searchField;
    private final Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private final ObservableList<Sale> masterSalesList;
    private final ObservableList<Sale> currentDisplayedList;

    private int idWaiter;

    public HistorySalesView() {
        User currentUser = UserSession.getCurrentUser();
        idWaiter = Integer.parseInt(currentUser.getUserID());

        masterSalesList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();

        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/styles.css").toExternalForm());
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        //view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm());
        view.setPadding(new Insets(20));
        saleController = new SaleController();

        // Inicializar controladores
        ProductController productController = new ProductController();
        OrderController orderController = new OrderController();
        AssignmentColumnFactory.initialize(productController, orderController, idWaiter);

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

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/sale.png").toString()));
        iconTitle.setFitHeight(48);
        iconTitle.setFitWidth(48);
        Label titleView = new Label("Historial de ventas no cerradas");
        titleView.getStyleClass().add("title");
        titleView.setAlignment(Pos.BOTTOM_LEFT);

        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section-dash");
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPrefWidth(Double.MAX_VALUE);
        topBox.setPadding(new Insets(0, 0, 0, 0));

        searchField.setPromptText("Buscar Venta...");
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
                HistorySalesColumnFactory.createIndexColumn(pagination, rowsPerPage),
                createDateColumn(),
                createTotalColumn(),
                createRatingColumn(),
                createStatusColumn(),
                createCloseButtonColumn()
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
        masterSalesList.setAll(saleController.getActiveSalesByEmployeeHistory(idWaiter));
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<Sale> filteredList = FXCollections.observableArrayList();

        if (filter.isEmpty()) {
            filteredList.addAll(masterSalesList);
        } else {
            for (Sale sale : masterSalesList) {
                if (matchesFilter(sale, filter)) {
                    filteredList.add(sale);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

    private boolean matchesFilter(Sale sale, String filter) {
        return sale.getSaleDate().toString().toLowerCase().contains(filter) ||
                (sale.getTotal() + "").contains(filter) ||
                (sale.getStatus() == 0 ? "Pendiente" : "Aprobado" ).toLowerCase().contains(filter);
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

    private TableColumn<Sale, Void> createCloseButtonColumn() {
        TableColumn<Sale, Void> col = new TableColumn<>("Acciones");
        col.setStyle("-fx-alignment: center;");

        col.setCellFactory(param -> new TableCell<>() {
            private final Button closeButton = new Button("Cerrar Venta");

            {
                closeButton.getStyleClass().add("close-button-d");

                closeButton.setOnAction(event -> {
                    Sale sale = getTableView().getItems().get(getIndex());
                    Optional<Double> rating = showRatingDialog();
                    rating.ifPresent(r -> {
                        if (new OrderController().finalizeSale(sale.getIdSale(), r)) {
                            showAlert("Venta Cerrada", "La venta se ha cerrado correctamente", Alert.AlertType.INFORMATION);
                            // Quitar de la lista actual
                            currentDisplayedList.remove(sale);
                            updatePagination();
                        } else {
                            showAlert("Error", "No se pudo cerrar la venta", Alert.AlertType.ERROR);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(closeButton);
                }
            }
        });

        return col;
    }

    private Optional<Double> showRatingDialog() {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Calificar Venta");
        dialog.setHeaderText("Por favor califique la experiencia del cliente");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        dialog.getDialogPane().applyCss();
        dialog.getDialogPane().layout();

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/star_full.png")));

        // Contenedor de estrellas
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER);

        Slider slider = new Slider(0, 5, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(1);
        slider.setBlockIncrement(0.5);
        slider.setSnapToTicks(true);

        slider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                if (value % 1 == 0) return String.valueOf(value.intValue());
                return "";
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        });

        Label valueLabel = new Label(String.format("Calificación: %.1f", slider.getValue()));
        valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Runnable updateStars = () -> {
            starsBox.getChildren().clear();
            double val = Math.round(slider.getValue() * 2) / 2.0;
            int fullStars = (int) val;
            boolean halfStar = (val - fullStars) >= 0.5;
            int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

            for (int i = 0; i < fullStars; i++) {
                starsBox.getChildren().add(new ImageView(
                        new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/star_full.png"))
                ));
            }
            if (halfStar) {
                starsBox.getChildren().add(new ImageView(
                        new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/star_half.png"))
                ));
            }
            for (int i = 0; i < emptyStars; i++) {
                starsBox.getChildren().add(new ImageView(
                        new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/star_empty.png"))
                ));
            }

            valueLabel.setText(String.format("Calificación: %.1f", val));
        };

        slider.valueProperty().addListener((obs, oldVal, newVal) -> updateStars.run());
        updateStars.run();

        VBox content = new VBox(10, starsBox, slider, valueLabel);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return Math.round(slider.getValue() * 2) / 2.0;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
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
