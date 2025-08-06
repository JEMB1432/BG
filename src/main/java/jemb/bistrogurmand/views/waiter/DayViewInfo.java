
package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jemb.bistrogurmand.Controllers.DayViewController;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;

public class DayViewInfo {
    User currentUser = new UserSession().getCurrentUser();

    // Controller para obtener datos
    DayViewController controller = new DayViewController();

    private BorderPane root;
    private int waiterId = Integer.parseInt(currentUser.getUserID());
    private String waiterName = currentUser.getFirstName() + " " + currentUser.getLastName();
    private String shift = controller.getShiftTime(waiterId);


    public DayViewInfo() {
        root = new BorderPane();
        // Estilo del fondo general
        root.setStyle("-fx-background-color: #f5f5f5;");

        VBox mainContent = new VBox(30);
        mainContent.setPadding(new Insets(60, 60, 60, 60));

        // Tarjeta principal
        VBox infoCard = new VBox(10);
        infoCard.setPadding(new Insets(30));
        infoCard.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label nombre = new Label(waiterName);
        System.out.println();
        nombre.setFont(Font.font("System", FontWeight.BOLD, 30));
        nombre.setTextFill(Color.web("#2E7D32"));

        Label turno = new Label(shift);
        turno.setFont(Font.font("System", FontWeight.BOLD, 24));

        infoCard.getChildren().addAll(nombre, turno);

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
    }

    private VBox crearMiniCard(String titulo, String valor, String icono) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefSize(180, 100);
        card.setStyle("-fx-background-color: white; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);");

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
