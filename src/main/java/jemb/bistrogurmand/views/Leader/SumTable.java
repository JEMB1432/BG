package jemb.bistrogurmand.views.Leader;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import jemb.bistrogurmand.Controllers.WaiterController;
import jemb.bistrogurmand.utils.User;

public class SumTable {
    private BorderPane view;
    private TableView<User> table;
    private WaiterController waiterController;
    private TextField searchField;
    private Pagination pagination;
    private final int rowsPerPage = 10;

    private ObservableList<User> masterWaiterList; // Esta contendrá a todos los meseros
    private ObservableList<User> currentDisplayedList;

    public SumTable() {
        masterWaiterList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.setPadding(new Insets(20));
        waiterController = new WaiterController();

        // Inicializar primero los componentes
        searchField = new TextField();
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        pagination = new Pagination();

        // Configurar la interfaz
        createTopSection();
        configureTable();
        configurePagination();
        createBottomSection();

        // Cargar datos después de que todo está configurado
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndPaginateTable());
        loadInitialData();
    }

    private void createTopSection() {
        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        Label title = new Label("Resumen de turnos");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        searchField.setPromptText("Buscar...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        Button refreshButton = new Button("Actualizar");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(title, searchField, refreshButton);
        view.setTop(topBox);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        // Columna de turno
        TableColumn<User, Void> indexColumn = new TableColumn<>("Turno");
        indexColumn.getStyleClass().add("index-column");
        indexColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.valueOf(getIndex() + 1 + (pagination.getCurrentPageIndex() * rowsPerPage)));
                }
            }
        });
        indexColumn.setPrefWidth(25);

// Columna de imagen
        /*
        TableColumn<User, String> imageColumn = new TableColumn<>("Foto");
        imageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserImage()));
        imageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null || imageUrl.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image(imageUrl, true); // true permite carga asíncrona
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });*/

        TableColumn<User, String> activeWaitersColumn= new TableColumn<>("Meseros activos");
        activeWaitersColumn.setStyle("-fx-alignment: center-left");
        activeWaitersColumn.getStyleClass().add("text-column");
        activeWaitersColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            //return new SimpleStringProperty(user.getFirstName());
            return new SimpleStringProperty("4");
        });

        TableColumn<User, String> serviceWaiters = new TableColumn<>("Meseros en servicio");
        serviceWaiters.setStyle("-fx-alignment: center-left");
        serviceWaiters.getStyleClass().add("text-column");
        serviceWaiters.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            //return new SimpleStringProperty(user.getLastName());
            return new SimpleStringProperty("4");
        });

        TableColumn<User, String> pendingOrders = new TableColumn<>("Ordenes Pendientes");
        pendingOrders.setStyle("-fx-alignment: center-left");
        pendingOrders.getStyleClass().add("text-column");
        pendingOrders.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            //return new SimpleStringProperty(user.getPhone());
            return new SimpleStringProperty("4");
        });



        table.getColumns().addAll(indexColumn,activeWaitersColumn,serviceWaiters,pendingOrders);
    }

    private void configurePagination() {
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateTableForPage(newVal.intValue());
            table.refresh();
        });
        view.setCenter(new VBox(10, table, pagination));
    }

    private void loadInitialData() {
        refreshTable();
    }

    private void createBottomSection() {
        HBox buttonBox = new HBox(20);
        buttonBox.getStyleClass().add("bottom-section");
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        //Button editButton = new Button("Editar");
        //editButton.getStyleClass().add("primary-button");
        //editButton.setOnAction(e -> editSelectedWaiter());

        //Button deleteButton = new Button("Eliminar");
        //deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        //deleteButton.getStyleClass().add("danger-button");
        //deleteButton.setOnAction(e -> deleteSelectedWaiter());

        //buttonBox.getChildren().addAll(editButton, deleteButton);
        view.setBottom(buttonBox);
    }

    //-----------------------------------------------------------------------------------------------------------------------//

    private void refreshTable() {
        //waiterController.getWaitersList();
        //pagination.setPageCount(calculatePageCount());
        //updateTableForPage(pagination.getCurrentPageIndex());
        masterWaiterList.setAll(waiterController.getWaitersList());
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        // Filtrar desde la lista maestra
        if (filter.isEmpty()) {
            filteredList.addAll(masterWaiterList);
        } else {
            for (User waiter : masterWaiterList) {
                if (waiter.getFirstName().toLowerCase().contains(filter) ||
                        waiter.getLastName().toLowerCase().contains(filter) ||
                        waiter.getEmail().toLowerCase().contains(filter) ||
                        waiter.getPhone().toLowerCase().contains(filter)) {
                    filteredList.add(waiter);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);

        pagination.setPageCount(calculatePageCount(currentDisplayedList.size()));

        if (pagination.getCurrentPageIndex() >= pagination.getPageCount()) {
            pagination.setCurrentPageIndex(0);
        }

        updateTableForPage(pagination.getCurrentPageIndex());
    }

    // Ajustar calculatePageCount para que tome el tamaño de la lista
    private int calculatePageCount(int itemCount) {
        return Math.max(1, (int) Math.ceil((double) itemCount / rowsPerPage));
    }

    //------------------------------------------------------------------------------------------------------------//

    private void filterTable() {
        String filter = searchField.getText().toLowerCase();
        if (filter.isEmpty()) {
            refreshTable();
            return;
        }

        ObservableList<User> filteredList = FXCollections.observableArrayList();
        for (User waiter : waiterController.getWaitersList()) {
            if (waiter.getFirstName().toLowerCase().contains(filter) ||
                    waiter.getLastName().toLowerCase().contains(filter) ||
                    waiter.getEmail().toLowerCase().contains(filter) ||
                    waiter.getPhone().toLowerCase().contains(filter)) {
                filteredList.add(waiter);
            }
        }

        table.setItems(filteredList);
        pagination.setPageCount((int) Math.ceil((double) filteredList.size() / rowsPerPage));
    }

    private int calculatePageCount() {
        int itemCount = waiterController.getWaitersList().size();
        return Math.max(1, (int) Math.ceil((double) itemCount / rowsPerPage));
    }

    private void updateTableForPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, currentDisplayedList.size());

        if (currentDisplayedList.isEmpty()) {
            table.setItems(FXCollections.observableArrayList());
        } else {
            table.setItems(FXCollections.observableArrayList(
                    currentDisplayedList.subList(fromIndex, toIndex)
            ));
        }
    }

    //private void editSelectedWaiter() {
        //User selected = table.getSelectionModel().getSelectedItem();
        //if (selected != null) {
            // Implementar lógica de edición
            //System.out.println("Editar mesero: " + selected.getFirstName());
        //} else {
            //showAlert("Selección requerida", "Por favor seleccione un mesero para editar.");
        //}
    //}

    //private void deleteSelectedWaiter() {
      //  User selected = table.getSelectionModel().getSelectedItem();
    //    if (selected != null) {
      //      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //    alert.setTitle("Confirmar eliminación");
          //  alert.setHeaderText("¿Eliminar mesero?");
            //alert.setContentText("Está a punto de eliminar a " + selected.getFirstName() +
              //      " " + selected.getLastName() + ". ¿Continuar?");
//
   //          Optional<ButtonType> result = alert.showAndWait();
     //       if (result.isPresent() && result.get() == ButtonType.OK) {
       //         // Implementar lógica de eliminación
         //       refreshTable();
           // }
        //} else {
          //  showAlert("Selección requerida", "Por favor seleccione un mesero para eliminar.");
        //}
    //}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}
