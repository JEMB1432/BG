package jemb.bistrogurmand.views.waiter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.application.App;
import jemb.bistrogurmand.utils.TableRestaurant;
import jemb.bistrogurmand.utils.UserSession;

public class AssignedTablesView {
    private BorderPane view;

    public AssignedTablesView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Sidebar
        SidebarWaiter sidebar = new SidebarWaiter();
        view.setLeft(sidebar);

        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar mesa...");
        searchField.setStyle("-fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 5 10;");
        searchField.setMaxWidth(300);

        // Title
        Label title = new Label("Mesas Asignadas");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2E7D32;");

        // Table container
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(20));
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Table
        TableView<TableRestaurant> table = createTable();

        // Sample data (in production would come from database)
        ObservableList<TableRestaurant> tables = FXCollections.observableArrayList(
                new TableRestaurant("T1", 2, 4, "Disponible", "Terraza"),
                new TableRestaurant("T2", 3, 2, "En servicio", "Interior"),
                new TableRestaurant("T3", 4, 6, "Esperando orden", "Ventana"),
                new TableRestaurant("T4", 5, 4, "Disponible", "Interior")
        );
        table.setItems(tables);

        tableContainer.getChildren().addAll(searchField, table);
        mainContent.getChildren().addAll(title, tableContainer);
        view.setCenter(mainContent);
    }

    private TableView<TableRestaurant> createTable() {
        TableView<TableRestaurant> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Table column
        TableColumn<TableRestaurant, Integer> colMesa = new TableColumn<>("Mesa");
        colMesa.setCellValueFactory(new PropertyValueFactory<>("NumberTable"));
        colMesa.setStyle("-fx-alignment: CENTER;");

        // Status column
        TableColumn<TableRestaurant, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("State"));
        colEstado.setStyle("-fx-alignment: CENTER;");

        // Actions column
        TableColumn<TableRestaurant, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button checkBtn = new Button("✔");
            private final Button xBtn = new Button("✘");

            {
                actionBox.setAlignment(Pos.CENTER);
                checkBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                xBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
                actionBox.getChildren().addAll(checkBtn, xBtn);

                checkBtn.setOnAction(event -> {
                    TableRestaurant mesa = getTableView().getItems().get(getIndex());
                    System.out.println("Check action for table: " + mesa.getNumberTable());
                });

                xBtn.setOnAction(event -> {
                    TableRestaurant mesa = getTableView().getItems().get(getIndex());
                    System.out.println("X action for table: " + mesa.getNumberTable());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().addAll(colMesa, colEstado, colAcciones);
        return table;
    }

    public BorderPane getView() {
        return view;
    }
}