package jemb.bistrogurmand.views.Leader;

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
import jemb.bistrogurmand.Controllers.PlanificationController;
import jemb.bistrogurmand.utils.PlanificationRestaurant;
import jemb.bistrogurmand.utils.ShiftSummary;

public class SumTable {
    private BorderPane view;
    private TableView<ShiftSummary> table;

    private ObservableList<ShiftSummary> masterSummaryList;

    public SumTable() {
        masterSummaryList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.setPadding(new Insets(20));


        // Inicializar primero los componentes
        table = new TableView<>();
        table.getStyleClass().add("table-view");


        createTopSection();
        configureTable();


        VBox centerLayout = new VBox(10, table);
        view.setCenter(centerLayout);


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

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/clock-ico.png").toString()));
        iconTitle.setFitHeight(57);
        iconTitle.setFitWidth(57);
        Label title = new Label("Resumen");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        titleContent.getChildren().addAll(iconTitle, title);
        titleContent.setAlignment(Pos.BOTTOM_LEFT);

        Button refreshButton = new Button("Actualizar");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        // Ajusta los elementos que añades al topBo
        topBox.getChildren().addAll(titleContent, refreshButton);
        topBox.setAlignment(Pos.CENTER_LEFT);
        view.setTop(topBox);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");


        TableColumn<ShiftSummary, String> shiftColumn = new TableColumn<>("Turno");
        shiftColumn.getStyleClass().add("text-column");
        shiftColumn.setPrefWidth(100); // Ajusta el ancho
        shiftColumn.setCellValueFactory(cellData -> cellData.getValue().shiftNameProperty()); // Usa la propiedad del nombre del turno
        shiftColumn.setStyle("-fx-alignment: center");

/*
        // Columna de Meseros Activos
        TableColumn<ShiftSummary, Number> activeWaitersColumn = new TableColumn<>("Meseros activos");
        activeWaitersColumn.setStyle("-fx-alignment: center"); // Centrar el número
        activeWaitersColumn.getStyleClass().add("number-column"); // Un estilo para números
        activeWaitersColumn.setCellValueFactory(cellData -> cellData.getValue().activeWaitersProperty()); // Usa la propiedad de conteo
*/

        // Columna de Meseros en Servicio
        TableColumn<ShiftSummary, Number> serviceWaitersColumn = new TableColumn<>("Meseros en servicio");
        serviceWaitersColumn.setStyle("-fx-alignment: center");
        serviceWaitersColumn.getStyleClass().add("number-column");
        serviceWaitersColumn.setCellValueFactory(cellData -> cellData.getValue().serviceWaitersProperty());


        // Columna de Mesas Libres
        TableColumn<ShiftSummary, Number> freeTablesColumn = new TableColumn<>("Mesas Libres");
        freeTablesColumn.setStyle("-fx-alignment: center");
        freeTablesColumn.getStyleClass().add("number-column");
        freeTablesColumn.setCellValueFactory(cellData -> cellData.getValue().activeWaitersProperty());


        // Columna de Órdenes Pendientes
        TableColumn<ShiftSummary, Number> pendingOrdersColumn = new TableColumn<>("Órdenes Pendientes");
        pendingOrdersColumn.setStyle("-fx-alignment: center");
        pendingOrdersColumn.getStyleClass().add("number-column");
        pendingOrdersColumn.setCellValueFactory(cellData -> cellData.getValue().pendingOrdersProperty());

        //table.getColumns().addAll(shiftColumn, activeWaitersColumn, serviceWaitersColumn, pendingOrdersColumn);
        table.getColumns().addAll(shiftColumn, serviceWaitersColumn, freeTablesColumn, pendingOrdersColumn);
    }


    private void loadInitialData() {
        refreshTable();
    }
    private void refreshTable() {
        masterSummaryList.setAll(PlanificationController.getShiftSummariesForToday());
        table.setItems(masterSummaryList);
    }


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