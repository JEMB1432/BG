package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jemb.bistrogurmand.views.waiter.SidebarMesero;
import jemb.bistrogurmand.views.waiter.TakeorderView;
import jemb.bistrogurmand.views.waiter.ModifyOrderView;

public class JornadaView {
    private BorderPane root;

    public JornadaView() {
        root = new BorderPane();

        // Estilo del fondo general
        root.setStyle("-fx-background-color: #f5f5f5;");

        VBox sidebar = new SidebarMesero().getView();
        root.setLeft(sidebar);

        HBox itemTomar     = (HBox) sidebar.getChildren().get(4);
        HBox itemModificar = (HBox) sidebar.getChildren().get(5);

        itemTomar.setOnMouseClicked((MouseEvent e) -> {
            // reemplaza solo el centro con TakeorderView
            root.setCenter(new TakeorderView("TBL-01").getView());
        });

        itemModificar.setOnMouseClicked((MouseEvent e) -> {
            // reemplaza solo el centro con ModifyOrderView
            root.setCenter(new ModifyOrderView("TBL-01").getView());
        });

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40, 40, 40, 40));

        // Tarjeta principal
        VBox infoCard = new VBox(10);
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label nombre = new Label("Nombre Completo");
        nombre.setFont(Font.font("System", FontWeight.BOLD, 24));
        nombre.setTextFill(Color.web("#2E7D32")); // verde

        Label turno = new Label("Turno: Mañana (8:00 - 14:00)");
        turno.setFont(Font.font("System", FontWeight.BOLD, 20));

        infoCard.getChildren().addAll(nombre, turno);

        // Tarjetas inferiores
        HBox statsCards = new HBox(30);
        statsCards.setPadding(new Insets(20, 0, 0, 0));
        statsCards.setAlignment(Pos.CENTER_LEFT);

        statsCards.getChildren().addAll(
                crearMiniCard("Calificación promedio:", "4.5", "★"),
                crearMiniCard("Pedidos activos:", "2", "🔔"),
                crearMiniCard("Mesas Asignadas:", "4", "🍽")
        );

       mainContent.getChildren().addAll(infoCard, statsCards);
        root.setCenter(mainContent);
    }

    private VBox crearMiniCard(String titulo, String valor, String icono) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefSize(140, 80);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);");

        Label title = new Label(titulo);
        title.setFont(Font.font("System", FontWeight.BOLD, 12));

        Label data = new Label(valor + " " + icono);
        data.setFont(Font.font("System", FontWeight.BOLD, 20));
        data.setTextFill(Color.web("#AD1457")); // magenta vino

        card.getChildren().addAll(title, data);
        return card;
    }


    public BorderPane getView() {
        return root;
    }
}

