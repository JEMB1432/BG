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
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.OrderChangeController;
import jemb.bistrogurmand.Controllers.TableController;
import jemb.bistrogurmand.utils.OrderColumnFactory;
import jemb.bistrogurmand.utils.OrderRestaurant;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.TableRestaurantColumnFactory;

import java.time.LocalDate;

import static jemb.bistrogurmand.utils.OrderColumnFactory.*;
import static jemb.bistrogurmand.utils.TableRestaurantColumnFactory.*;

public class OrderChangeView {
    private BorderPane view;
    private TableView<OrderRestaurant> table;
    private OrderChangeController tableController;
    private TextField searchField;
    private Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private ObservableList<OrderRestaurant> masterTableRestaurantList;
    private ObservableList<OrderRestaurant> currentDisplayedList;

    public OrderChangeView() {
        masterTableRestaurantList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.setPadding(new Insets(20));
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        tableController = new OrderChangeController();

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

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/waiter.png").toString()));
        iconTitle.setFitWidth(57);
        iconTitle.setFitHeight(57);
        Label title = new Label("Cambio de pedido");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        titleContent.getChildren().addAll(iconTitle, title);

        searchField.setPromptText("Buscar pedido de cambio...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(Double.MAX_VALUE);

        ImageView imageViewUpdate = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/update.png").toString()));
        imageViewUpdate.setFitHeight(16);
        imageViewUpdate.setFitWidth(16);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setGraphic(imageViewUpdate);
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(searchField, refreshButton);
        globalSection.getChildren().addAll(titleContent, topBox);
        view.setTop(globalSection);
    }

    /* SIN MOSTRAR DESHABILITADOS
    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                OrderColumnFactory.createIndexColumn(pagination,rowsPerPage),
                createID_EmployeeColumn(),
                createID_ProductColumn(),
                OrderColumnFactory.createStateColumn()
        );
    }*/

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                OrderColumnFactory.createIndexColumn(pagination, rowsPerPage),
                createID_EmployeeColumn(),
                createID_ProductColumn(),
                OrderColumnFactory.createStateColumn()
        );

        table.setRowFactory(tv -> new TableRow<OrderRestaurant>() {
            @Override
            protected void updateItem(OrderRestaurant item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("disabled-row", "active-row");
                if (item != null && !empty) {
                    if (item.getApproved() == 2) {
                        getStyleClass().add("disabled-row");
                    } else if (item.getApproved() == 1) {
                        // Si quieres, puedes agregar un estilo para las aprobadas
                        getStyleClass().add("active-row");
                    }
                }
            }
        });
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

        Button acceptButton = new Button("Aprobar");
        acceptButton.getStyleClass().add("secondary-button");
        acceptButton.setOnAction(event -> acceptSelectedTable());

        Button cancelButton = new Button("No aprobar");
        cancelButton.getStyleClass().add("danger-button");
        cancelButton.setOnAction(event -> cancelSelectedTable());

        buttomBox.getChildren().addAll(acceptButton, cancelButton);
        view.setBottom(buttomBox);
    }

    private void refreshTable() {
        masterTableRestaurantList.clear();
        masterTableRestaurantList.setAll(tableController.getDailyOrderCorrections());
        searchField.clear();
        filterAndPaginateTable();
        table.refresh();
    }
    private void refreshCancel() {
        searchField.clear();
        filterAndPaginateTable();
        table.refresh();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<OrderRestaurant> filteredList = FXCollections.observableArrayList();

        if (filter.isEmpty()) {
            filteredList.addAll(masterTableRestaurantList);
        } else {
            for (OrderRestaurant order : masterTableRestaurantList) {
                if (order.getEmployeeName().toLowerCase().contains(filter) ||
                        order.getProductName().toLowerCase().contains(filter) ||
                        String.valueOf(order.getID_Correction()).contains(filter)) {
                    filteredList.add(order);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
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

    private void acceptSelectedTable() {
        OrderRestaurant selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Llama al controlador para aprobar la corrección en la base de datos
            if (tableController.approveOrderCorrection(selected.getID_Correction())) {
                showAlert("Éxito", "La corrección ha sido aprobada.", Alert.AlertType.INFORMATION);
                refreshTable();
            } else {
                showAlert("Error", "No se pudo aprobar la corrección. Inténtelo de nuevo.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione una orden para cambiar su estado", Alert.AlertType.WARNING);
        }
    }

   /* private void cancelSelectedTable() {
        OrderRestaurant selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setApproved(2);
            refreshCancel();
            showAlert("Éxito", "La corrección ha sido marcada como no aprobada.", Alert.AlertType.INFORMATION);

        } else {
            showAlert("Selección Requerida", "Por favor, seleccione una orden para cambiar su estado", Alert.AlertType.WARNING);
        }
    }*/

    //  ALTERANDO BD APPROVED =2
    private void cancelSelectedTable() {
        OrderRestaurant selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (tableController.rejectOrderCorrection(selected.getID_Correction())) {
                selected.setApproved(2); // Usamos 2 para "No Aprobado"
                refreshTable();
                showAlert("Éxito", "La corrección ha sido marcada como no aprobada.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "No se pudo marcar la corrección. Inténtelo de nuevo.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione una orden para cambiar su estado", Alert.AlertType.WARNING);
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

    private void addTableForm() {
        System.out.println("Adding table");
    }

    public BorderPane getView() {
        return view;
    }

    /*
    // En tu método que maneja el botón de aprobar
private void handleApproveButton(OrderCorrection correction) {
    try {
        // 1. Primero sincronizar los cambios
        syncApprovedCorrectionWithSale(correction.getIdCorrection());

        // 2. Luego marcar como aprobado
        String updateSql = "UPDATE Order_Correction SET Approved = 1 WHERE ID_Correction = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setInt(1, correction.getIdCorrection());
            stmt.executeUpdate();
        }

        // 3. Mostrar mensaje de éxito
        showAlert("Éxito", "Corrección aprobada y sincronizada con la venta", Alert.AlertType.INFORMATION);

        // 4. Refrescar la vista
        refreshTable();

    } catch (SQLException e) {
        showAlert("Error", "No se pudo completar la aprobación: " + e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
     */
}