package jemb.bistrogurmand.views.Admin;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.WaiterController;
import jemb.bistrogurmand.utils.UserTableColumnFactory;

import java.util.Optional;

import static jemb.bistrogurmand.utils.UserTableColumnFactory.*;

public class WaiterView {
    private BorderPane view;
    private TableView<User> table;
    private WaiterController waiterController;
    private TextField searchField;
    private Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private ObservableList<User> masterWaiterList; // Esta contendrá a todos los meseros
    private ObservableList<User> currentDisplayedList;

    public WaiterView() {
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

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Agregar un pequeño retardo para evitar procesamiento excesivo
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> filterAndPaginateTable());
            pause.playFromStart();
        });

        // Configurar la interfaz
        createTopSection();
        configureTable();
        configurePagination();
        createBottomSection();

        loadInitialData();
    }

    private void createTopSection() {
        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/users.png").toString()));
        iconTitle.setFitHeight(57);
        iconTitle.setFitWidth(57);
        Label title = new Label("Gestión de Meseros");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        searchField.setPromptText("Buscar meseros...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(Double.MAX_VALUE);
        //searchField.textProperty().addListener((obs, oldVal, newVal) -> filterAndPaginateTable());

        ImageView imageViewAdd = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/add.png").toString()));
        imageViewAdd.setFitHeight(16);
        imageViewAdd.setFitWidth(16);
        Button addbutton = new Button("Agregar Mesero");
        addbutton.setGraphic(imageViewAdd);
        addbutton.getStyleClass().add("primary-button");
        addbutton.setOnAction(event -> addWaiterForm());

        ImageView imageViewUpdate = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/update.png").toString()));
        imageViewUpdate.setFitHeight(16);
        imageViewUpdate.setFitWidth(16);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setGraphic(imageViewUpdate);
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(iconTitle, title, searchField, addbutton, refreshButton);
        view.setTop(topBox);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                UserTableColumnFactory.createIndexColumn(pagination, rowsPerPage),
                createFirstNameColumn(),
                createLastNameColumn(),
                createPhoneColumn(),
                createEmailColumn(),
                createRolColumn(),
                createStateColumn()
        );

    }

    private void configurePagination() {
        // Crear la etiqueta de información
        paginationInfo = new Label();

        // Configurar el cambio de página
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateTableForPage(newVal.intValue());
            updatePaginationInfo(newVal.intValue());
        });

        // Configurar el diseño
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

    private void createBottomSection() {
        HBox buttonBox = new HBox(20);
        buttonBox.getStyleClass().add("bottom-section");
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(e -> editSelectedWaiter());

        Button deleteButton = new Button("Eliminar");
        //deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedWaiter());

        buttonBox.getChildren().addAll(editButton, deleteButton);
        view.setBottom(buttonBox);
    }

    private void refreshTable() {
        masterWaiterList.setAll(waiterController.getWaitersList());
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        if (filter.isEmpty()) {
            filteredList.setAll(masterWaiterList);
        } else {
            for (User waiter : masterWaiterList) {
                if (matchesFilter(waiter, filter)) {
                    filteredList.add(waiter);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

    private boolean matchesFilter(User waiter, String filter) {
        return waiter.getFirstName().toLowerCase().contains(filter) ||
                waiter.getLastName().toLowerCase().contains(filter) ||
                waiter.getEmail().toLowerCase().contains(filter) ||
                waiter.getPhone().toLowerCase().contains(filter);
    }

    private void updatePagination() {
        int itemCount = currentDisplayedList.size();
        int pageCount = (int) Math.ceil((double) itemCount / rowsPerPage);

        // Asegurar que pageCount sea al menos 1
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        // Si estamos en una página que ya no existe, volver a la página 0
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
            table.setItems(FXCollections.observableArrayList(
                    currentDisplayedList.subList(fromIndex, toIndex)
            ));
        }

        updatePaginationInfo(pageIndex);
        table.refresh();
    }

    // Ajustar calculatePageCount para que tome el tamaño de la lista
    private int calculatePageCount(int itemCount) {
        return Math.max(1, (int) Math.ceil((double) itemCount / rowsPerPage));
    }

    private void editSelectedWaiter() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Implementar lógica de edición
            System.out.println("Editar mesero: " + selected.getFirstName());
        } else {
            showAlert("Selección requerida", "Por favor seleccione un mesero para editar.");
        }
    }

    private void deleteSelectedWaiter() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Eliminar mesero?");
            alert.setContentText("Está a punto de eliminar a " + selected.getFirstName() +
                    " " + selected.getLastName() + ". ¿Continuar?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Eliminar de todas las listas
                masterWaiterList.remove(selected);
                currentDisplayedList.remove(selected);

                // Actualizar la paginación y la tabla
                updatePagination();

                // Si la lista está vacía, forzar actualización
                if (currentDisplayedList.isEmpty()) {
                    table.getItems().clear();
                    updatePaginationInfo(0);
                }
            }
        } else {
            showAlert("Selección requerida", "Por favor seleccione un mesero para eliminar.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addWaiterForm(){
        System.out.println("addWaiterForm");
    }

    public BorderPane getView() {
        return view;
    }
}