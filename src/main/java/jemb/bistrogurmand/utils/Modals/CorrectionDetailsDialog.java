package jemb.bistrogurmand.utils.Modals;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.OrderRestaurant;

import java.util.List;

public class CorrectionDetailsDialog extends Dialog<Void> {

    public CorrectionDetailsDialog(List<OrderRestaurant> details) {
        setTitle("Detalles de Corrección");
        setHeaderText("Detalles completos de la corrección");


        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm()
        );


        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        TableView<OrderRestaurant> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrderRestaurant, String> productCol = new TableColumn<>("Producto");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(200);

        TableColumn<OrderRestaurant, Void> statusCol = new TableColumn<>("Estado");
        statusCol.setCellFactory(param -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.getStyleClass().add("badge");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    OrderRestaurant detail = getTableView().getItems().get(getIndex());
                    String status = switch (detail.getApproved()) {
                        case 0 -> "Pendiente";
                        case 1 -> "Aprobado";
                        case 2 -> "Rechazado";
                        default -> "Desconocido";
                    };
                    label.setText(status);
                    label.getStyleClass().removeAll("badge-yellow", "badge", "badge-red");
                    switch (status) {
                        case "Pendiente" -> label.getStyleClass().add("badge-yellow");
                        case "Aprobado" -> label.getStyleClass().add("badge");
                        case "Rechazado" -> label.getStyleClass().add("badge-red");
                        default -> label.getStyleClass().add("");
                    }
                    setGraphic(label);
                }
            }
        });
        statusCol.setPrefWidth(150);

        table.getColumns().addAll(productCol, statusCol);

        ObservableList<OrderRestaurant> items = FXCollections.observableArrayList(details);
        table.setItems(items);

        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        table.setPrefHeight(400);

        GridPane content = new GridPane();
        content.setPadding(new Insets(15));
        content.setHgap(10);
        content.setVgap(10);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        content.getColumnConstraints().add(cc);

        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);
        content.getRowConstraints().add(rc);

        content.add(table, 0, 0);
        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        getDialogPane().setContent(content);

        getDialogPane().setMinWidth(500);
        getDialogPane().setMinHeight(400);

        Platform.runLater(() -> {
            if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) getDialogPane().getScene().getWindow();
                stage.setMinWidth(500);
                stage.setMinHeight(400);
            }
        });
    }
}
