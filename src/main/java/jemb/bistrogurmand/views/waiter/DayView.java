package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.Controllers.DayViewController;
import jemb.bistrogurmand.DbConection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DayView {
    private BorderPane root;
    private int waiterId = 1; // Deberías obtener este ID de la sesión
    private String waiterName = "Nombre Completo";
    private String shift = "Turno no especificado";

    public DayView() {
        root = new BorderPane();
        loadWaiterData(); // Cargar datos del mesero

        SidebarWaiter sm = new SidebarWaiter();

        // Estilo del fondo general
        root.setStyle("-fx-background-color: #f5f5f5;");

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(60, 60, 60, 60));

        // Tarjeta principal
        VBox infoCard = new VBox(10);
        infoCard.setPadding(new Insets(30));
        infoCard.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label nombre = new Label(waiterName);
        nombre.setFont(Font.font("System", FontWeight.BOLD, 30));
        nombre.setTextFill(Color.web("#2E7D32"));

        Label turno = new Label(shift);
        turno.setFont(Font.font("System", FontWeight.BOLD, 24));

        infoCard.getChildren().addAll(nombre, turno);

        // Controller para obtener datos
        DayViewController controller = new DayViewController();
        double averageRating = controller.getAverageRating(waiterId);
        int activeOrders = controller.getActiveOrdersCount(waiterId);
        int assignedTables = controller.getAssignedTablesCount(waiterId);

        // Tarjetas inferiores
        HBox statsCards = new HBox(30);
        statsCards.setPadding(new Insets(20, 0, 0, 0));
        statsCards.setAlignment(Pos.CENTER_LEFT);

        statsCards.getChildren().addAll(
                crearMiniCard("Calificación promedio:", String.format("%.1f", averageRating), "★"),
                crearMiniCard("Pedidos activos:", String.valueOf(activeOrders), "🔔"),
                crearMiniCard("Mesas asignadas:", String.valueOf(assignedTables), "🍽")
        );

        mainContent.getChildren().addAll(infoCard, statsCards);
        root.setCenter(mainContent);
        root.setLeft(sm);
    }

    private void loadWaiterData() {
        // Obtener nombre del empleado
        String nameSql = "SELECT Name FROM Employee WHERE ID_Employee = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(nameSql)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                waiterName = rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Obtener turno actual desde Assignment
        String shiftSql = "SELECT Shift FROM Assignment WHERE ID_Employee = ? AND TRUNC(dateassig) = TRUNC(SYSDATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(shiftSql)) {

            stmt.setInt(1, waiterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                shift = "Turno: " + rs.getString("Shift");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox crearMiniCard(String titulo, String valor, String icono) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefSize(180, 100);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);");

        Label title = new Label(titulo);
        title.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label data = new Label(valor + " " + icono);
        data.setFont(Font.font("System", FontWeight.BOLD, 26));
        data.setTextFill(Color.web("#AD1457"));

        card.getChildren().addAll(title, data);
        return card;
    }

    public BorderPane getView() {
        return root;
    }
}