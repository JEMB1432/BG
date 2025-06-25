package jemb.bistrogurmand.views.Meseros;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SidebarMesero {

    private VBox sidebar;

    public SidebarMesero() {
        sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: white;");

        sidebar.getChildren().addAll(
                crearHeader(),
                crearUserInfo(),
                crearMenuItem("Jornada", true, "≡"),
                crearMenuItem("Mesas asignadas", false, "🪑"),
                crearMenuItem("Tomar pedido", false, "👥"),
                crearMenuItem("Modificación de pedido", false, "✏️")
        );
    }

    private VBox crearHeader() {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 20, 10, 20));

        Label icon = new Label("👨‍🍳");
        icon.setStyle("-fx-font-size: 24px;");

        Label title = new Label("Bistro");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#AD1457")); // Vino

        header.getChildren().addAll(icon, title);
        return header;
    }

    private VBox crearUserInfo() {
        VBox userBox = new VBox(5);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(10, 20, 10, 20));

        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 20px;");

        Label nombre = new Label("Nombre Mesero");
        nombre.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label rol = new Label("Mesero");
        rol.setTextFill(Color.GREY);

        userBox.getChildren().addAll(avatar, nombre, rol);
        return userBox;
    }

    private HBox crearMenuItem(String texto, boolean activo, String icono) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 20, 10, 20));

        Label iconLabel = new Label(icono);
        iconLabel.setStyle("-fx-font-size: 14px;");

        Label textLabel = new Label(texto);
        textLabel.setFont(Font.font("System", 14));

        item.getChildren().addAll(iconLabel, textLabel);

        if (activo) {
            item.setStyle("-fx-background-color: #f3dfe5; -fx-border-width: 0 0 1 0; -fx-border-color: #e0e0e0;");
            textLabel.setTextFill(Color.web("#AD1457")); // Activo
        } else {
            item.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #e0e0e0;");
        }

        return item;
    }

    public VBox getView() {
        return sidebar;
    }
}
