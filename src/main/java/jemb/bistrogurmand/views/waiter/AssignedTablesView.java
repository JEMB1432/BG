package jemb.bistrogurmand.views.waiter;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.utils.TableRestaurant;

import java.util.List;

public class AssignedTablesView {
    private VBox view;

    public AssignedTablesView() {
        view = new VBox(20);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Título
        Label title = new Label("Mesas Asignadas");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Contenedor blanco
        VBox tableCard = new VBox(10);
        tableCard.setPadding(new Insets(20));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        // Campo búsqueda
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar mesa...");
        searchField.setMaxWidth(Double.MAX_VALUE);

        // Tabla
        TableView<TableRestaurant> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Columnas
        TableColumn<TableRestaurant, String> colMesa = new TableColumn<>("Mesa");
        colMesa.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNumberTable().toString()));

        TableColumn<TableRestaurant, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getState()));

        TableColumn<TableRestaurant, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);

            {
                actionBox.setAlignment(Pos.CENTER_LEFT);

                Button btnVer = createIconButton("📋", "#2e7d32");
                Button btnFoto = createIconButton("🖼", "gray");
                Button btnEdit = createIconButton("✏️", "#4caf50");
                Button btnDelete = createIconButton("❌", "#d32f2f");

                ImageView avatar = new ImageView(new Image("/avatar.png")); // Asegúrate de tener este recurso
                avatar.setFitWidth(26);
                avatar.setFitHeight(26);
                avatar.setClip(new javafx.scene.shape.Circle(13, 13, 13));

                btnVer.setOnAction(e -> {
                    TableRestaurant mesa = getTableView().getItems().get(getIndex());
                    showAlert("Detalles de Mesa", "Mesa " + mesa.getNumberTable() + "\nEstado: " + mesa.getState());
                });

                btnFoto.setOnAction(e -> showAlert("Foto Mesa", "Aquí se mostraría una foto 📷"));

                btnEdit.setOnAction(e -> {
                    TableRestaurant mesa = getTableView().getItems().get(getIndex());
                    mesa.setState("Disponible");
                    getTableView().refresh();
                    showAlert("Editado", "Estado cambiado a Disponible");
                });

                btnDelete.setOnAction(e -> {
                    TableRestaurant mesa = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(mesa);
                    showAlert("Eliminado", "Mesa eliminada");
                });

                actionBox.getChildren().addAll(btnFoto, btnEdit, btnDelete, avatar);
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

        // Agregar columnas
        table.getColumns().addAll(colMesa, colEstado, colAcciones);

        // Datos de prueba
        List<TableRestaurant> mesas = List.of(
                new TableRestaurant("1", 2, 4, "Disponible", "Terraza"),
                new TableRestaurant("2", 3, 2, "En servicio", "Interior"),
                new TableRestaurant("3", 4, 6, "Esperando orden", "Ventana"),
                new TableRestaurant("4", 5, 4, "Disponible", "Interior")
        );
        table.getItems().addAll(mesas);

        // Armar vista
        tableCard.getChildren().addAll(searchField, table);
        view.getChildren().addAll(title, tableCard);
    }

    private Button createIconButton(String icon, String color) {
        Button btn = new Button(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + ";");
        btn.setFont(Font.font(14));
        return btn;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}
