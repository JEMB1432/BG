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
import jemb.bistrogurmand.Controllers.AssignedTableController;
import jemb.bistrogurmand.application.App;
import jemb.bistrogurmand.utils.Modals.AssignedTable;
import jemb.bistrogurmand.utils.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssignedTablesView {
    private final BorderPane view;
    private TableView<AssignedTable> table;

    public AssignedTablesView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #f5f5f5;");

        SidebarWaiter sidebar = new SidebarWaiter();
        view.setLeft(sidebar);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar mesa...");
        searchField.setStyle("-fx-background-radius: 15; -fx-border-radius: 15; -fx-padding: 5 10;");
        searchField.setMaxWidth(300);

        Label title = new Label("Mesas Asignadas");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2E7D32;");

        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(20));
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        table = createTable();

        loadAssignedTables();

        tableContainer.getChildren().addAll(searchField, table);
        mainContent.getChildren().addAll(title, tableContainer);
        view.setCenter(mainContent);
    }

    private TableView<AssignedTable> createTable() {
        TableView<AssignedTable> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AssignedTable, Integer> colMesa = new TableColumn<>("Mesa");
        colMesa.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        colMesa.setStyle("-fx-alignment: CENTER;");

        TableColumn<AssignedTable, String> colUbicacion = new TableColumn<>("Ubicación");
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("location"));
        colUbicacion.setStyle("-fx-alignment: CENTER;");

        TableColumn<AssignedTable, String> colTurno = new TableColumn<>("Turno");
        colTurno.setCellValueFactory(new PropertyValueFactory<>("shift"));
        colTurno.setStyle("-fx-alignment: CENTER;");

        TableColumn<AssignedTable, String> colHorario = new TableColumn<>("Horario");
        colHorario.setCellValueFactory(cellData -> {
            AssignedTable at = cellData.getValue();
            String formatted = at.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " + at.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        colHorario.setStyle("-fx-alignment: CENTER;");

        TableColumn<AssignedTable, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });
        colFecha.setStyle("-fx-alignment: CENTER;");

        TableColumn<AssignedTable, String> colFavorita = new TableColumn<>("Favorita");
        colFavorita.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().isFavorite() ? "Sí" : "No"
            );
        });
        colFavorita.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(colMesa, colUbicacion, colTurno, colHorario, colFecha, colFavorita);
        return table;
    }

    private void loadAssignedTables() {
        //int waiterId = UserSession.getUser().getId(); // Requiere que UserSession tenga ese método
        AssignedTableController controller = new AssignedTableController();
       // List<AssignedTable> tableList = controller.getAssignedTablesByWaiter(waiterId);
        //ObservableList<AssignedTable> observableList = FXCollections.observableArrayList(tableList);
        //table.setItems(observableList);
    }

    public BorderPane getView() {
        return view;
    }
}
