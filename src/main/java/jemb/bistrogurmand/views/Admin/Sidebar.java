package jemb.bistrogurmand.views.Admin;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

public class Sidebar extends VBox {
    private Button btnDashboard;
    private Button btnMeseros;
    private Button btnMenu;
    private Button btnMesas;

    public Sidebar() {
        getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/sidebar.css").toExternalForm());
        getStyleClass().add("sidebar");

        createLogo();
        createUserInfo();
        createMenu();


        this.setFillWidth(true);
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
    }

    private void createLogo() {
        SVGPath logoIcon = new SVGPath();
        logoIcon.setContent("M2 5C0 5 0 2 2 2q2-2 4 0c2 0 2 3 0 3v3H2");
        logoIcon.getStyleClass().add("logo-icon");

        Label logoText = new Label("Bistro Gurmand");
        logoText.getStyleClass().add("logo-text");

        HBox logo = new HBox(10, logoIcon, logoText);
        logo.getStyleClass().add("logo");

        this.getChildren().add(logo);
        //this.setMinHeight(Double.MAX_VALUE);
    }

    private void createUserInfo() {
        ImageView userAvatar = new ImageView(new Image("https://randomuser.me/api/portraits/men/32.jpg"));
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);
        userAvatar.getStyleClass().add("user-avatar");

        Circle clip = new Circle(userAvatar.getFitWidth() / 2, userAvatar.getFitHeight() / 2, userAvatar.getFitWidth() / 2);
        userAvatar.setClip(clip);

        Label userName = new Label("Carlos Mendoza");
        userName.getStyleClass().add("user-name");

        Label userRole = new Label("Líder de Meseros");
        userRole.getStyleClass().add("user-role");

        VBox userDetails = new VBox(10, userName, userRole);

        HBox userInfo = new HBox(10, userAvatar, userDetails);
        userInfo.setMaxWidth(Double.MAX_VALUE);
        userInfo.getStyleClass().add("user-info");

        this.getChildren().add(userInfo);
    }

    private void createMenu() {
        // Botones del menú
        btnDashboard = crearMenuButton("Dashboard", "M3 12a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1zm0 8a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1v-4a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1zm10 0a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1v-8a1 1 0 0 0-1-1h-6a1 1 0 0 0-1 1zm1-17a1 1 0 0 0-1 1v4a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4a1 1 0 0 0-1-1z");
        btnMeseros = crearMenuButton("Meseros", "M16 24a8 8 0 1 0 0-16a8 8 0 0 0 0 16m18 0a6 6 0 1 0 0-12a6 6 0 0 0 0 12M6.75 27A3.75 3.75 0 0 0 3 30.75V32s0 9 13 9s13-9 13-9v-1.25A3.75 3.75 0 0 0 25.25 27zm21.924 11.089c1.376.558 3.119.911 5.325.911c10.5 0 10.5-8 10.5-8v-.25A3.75 3.75 0 0 0 40.75 27H29.607a5.73 5.73 0 0 1 1.391 3.75v1.295l-.001.057l-.006.15q-.008.173-.035.43a10 10 0 0 1-.24 1.325a10.7 10.7 0 0 1-2.042 4.082");
        btnMenu = crearMenuButton("Productos", "M4.505 2h-.013a.5.5 0 0 0-.176.036a.5.5 0 0 0-.31.388C3.99 2.518 3.5 5.595 3.5 7c0 .95.442 1.797 1.13 2.345c.25.201.37.419.37.601v.5q0 .027-.003.054c-.027.26-.151 1.429-.268 2.631C4.614 14.316 4.5 15.581 4.5 16a2 2 0 1 0 4 0c0-.42-.114-1.684-.229-2.869a302 302 0 0 0-.268-2.63L8 10.446v-.5c0-.183.12-.4.37-.601A3 3 0 0 0 9.5 7c0-1.408-.493-4.499-.506-4.577a.5.5 0 0 0-.355-.403A.5.5 0 0 0 8.51 2h-.02h.001a.505.505 0 0 0-.501.505v4a.495.495 0 0 1-.99.021V2.5a.5.5 0 0 0-1 0v4l.001.032a.495.495 0 0 1-.99-.027V2.506A.506.506 0 0 0 4.506 2M11 6.5A4.5 4.5 0 0 1 15.5 2a.5.5 0 0 1 .5.5v6.978l.02.224a626 626 0 0 1 .228 2.696c.124 1.507.252 3.161.252 3.602a2 2 0 1 1-4 0c0-.44.128-2.095.252-3.602c.062-.761.125-1.497.172-2.042l.03-.356H12.5A1.5 1.5 0 0 1 11 8.5zM8.495 2h-.004z");
        btnMesas = crearMenuButton("Reportes", "m6 20l1.5-3.75q.225-.575.725-.913T9.35 15H11v-4.025Q7.175 10.85 4.587 9.85T2 7.5q0-1.45 2.925-2.475T12 4q4.175 0 7.088 1.025T22 7.5q0 1.35-2.588 2.35T13 10.975V15h1.65q.6 0 1.113.338t.737.912L18 20h-2l-1.2-3H9.2L8 20z");

        // Set dashboard as active by default
        btnDashboard.getStyleClass().add("active");

        VBox menu = new VBox(0, btnDashboard, btnMenu, btnMesas, btnMeseros);
        menu.getStyleClass().add("sidebar-menu");


        this.getChildren().add(menu);
    }

    private Button crearMenuButton(String text, String svgPath) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.getStyleClass().add("icon");

        // Define el tamaño deseado
        double desiredWidth = 20;
        double desiredHeight = 20;

        // Calcula los factores de escala
        double scaleX = desiredWidth / icon.getBoundsInLocal().getWidth();
        double scaleY = desiredHeight / icon.getBoundsInLocal().getHeight();

        // Aplica los factores de escala
        icon.setScaleX(scaleX);
        icon.setScaleY(scaleY);

        Label label = new Label(text);

        HBox content = new HBox(10, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(content);
        button.getStyleClass().add("menu-button");

        // Set action to toggle active state
        button.setOnAction(e -> {
            // Remove active class from all buttons
            btnDashboard.getStyleClass().remove("active");
            btnMenu.getStyleClass().remove("active");
            btnMesas.getStyleClass().remove("active");
            btnMeseros.getStyleClass().remove("active");

            // Add active class to clicked button
            button.getStyleClass().add("active");
        });

        return button;
    }
}