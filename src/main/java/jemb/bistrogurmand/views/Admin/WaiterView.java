package jemb.bistrogurmand.views.Admin;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.WaiterController;
import jemb.bistrogurmand.utils.Modals.AddWaiterDialog;
import jemb.bistrogurmand.utils.Modals.EditWaiterDialog;
import jemb.bistrogurmand.utils.User;
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

    private ObservableList<User> masterWaiterList;
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
        VBox globalSection = new VBox();

        HBox topBox = new HBox(20);
        topBox.getStyleClass().add("top-section");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        HBox titleContent = new HBox();
        titleContent.setAlignment(Pos.BOTTOM_LEFT);
        titleContent.setSpacing(10);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/users.png").toString()));
        iconTitle.setFitHeight(57);
        iconTitle.setFitWidth(57);
        Label title = new Label("Gestión de Meseros");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        titleContent.getChildren().addAll(iconTitle, title);

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

        topBox.getChildren().addAll(searchField, addbutton, refreshButton);
        globalSection.getChildren().addAll(titleContent, topBox);
        view.setTop(globalSection);
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

    private void editSelectedWaiter() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {

            EditWaiterDialog dialog = new EditWaiterDialog(selected);

            Optional<User> result = dialog.showAndWait();
            result.ifPresent(updatedUser -> {
                System.out.println("Mesero actualizado: " + updatedUser.getFirstName());
                if (waiterController.updateWaiter(updatedUser)){
                    showAlert("Empleado Actualizado","El empleado se ha actualizado correctamente",1);
                }else {
                    showAlert("Eror","Ocurrio un error al actualizar el empleado",3);
                }
                refreshTable();
            });
        } else {
            showAlert("Selección requerida", "Por favor seleccione un mesero para editar.", 2);
        }
    }

    private void deleteSelectedWaiter() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getGraphic();
            alert.setTitle("Confirmar desactivación");
            alert.setHeaderText("¿Desactivar mesero?");
            alert.setContentText("Está a punto de desactivar a " + selected.getFirstName() +
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
            showAlert("Selección requerida", "Por favor seleccione un mesero para eliminar.", 2);
        }
    }

    private void showAlert(String title, String message, int type) {
        /*
        1 -ALERTA DE INFORMACION
        2 -ALERTA DE WARNING
        3 -ALERTA DE ERROR
         */
        Alert alert;
        switch (type){
            case 1:
                 alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case 2:
                alert = new Alert(Alert.AlertType.WARNING);
                break;
            case 3:
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            default:
                alert = new Alert(Alert.AlertType.NONE);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addWaiterForm() {
        AddWaiterDialog dialog = new AddWaiterDialog();

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(newUser -> {
            String password = dialog.getPassword();
            if (waiterController.insertWaiter(newUser, password)) {
                showAlert("Mesero Agregado", "El mesero se ha agregado correctamente", 1);
                refreshTable();
            } else {
                showAlert("Error", "Ocurrió un error al agregar el mesero", 3);
            }
        });
    }

    public BorderPane getView() {
        return view;
    }
}