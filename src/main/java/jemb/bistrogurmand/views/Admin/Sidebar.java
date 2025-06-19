package jemb.bistrogurmand.views.Admin;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import jemb.bistrogurmand.Controllers.UserSession;
import jemb.bistrogurmand.application.App;

import java.util.Arrays;
import java.util.function.Consumer;


public class Sidebar extends VBox {
    private Button btnDashboard;
    private Button btnMeseros;
    private Button btnMenu;
    private Button btnMesas;

    User currentUser = new UserSession().getCurrentUser();

    private Consumer<String> viewChangeListener;

    public void setViewChangeListener(Consumer<String> listener) {
        this.viewChangeListener = listener;
    }

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

        // Calcular la relación de aspecto original
        Bounds bounds = logoIcon.getBoundsInLocal();
        double originalAspectRatio = bounds.getWidth() / bounds.getHeight();

        // Tamaño deseado (usaremos el mismo para ancho y alto como área máxima)
        double desiredSize = 20;

        // Calcular el escalado manteniendo la relación de aspecto
        double scale;
        if (bounds.getWidth() > bounds.getHeight()) {
            scale = desiredSize / bounds.getWidth();
        } else {
            scale = desiredSize / bounds.getHeight();
        }

        // Aplicar el mismo factor de escala en ambos ejes
        logoIcon.setScaleX(scale);
        logoIcon.setScaleY(scale);

        Label logoText = new Label("Bistro");
        logoText.getStyleClass().add("logo-text");

        HBox logo = new HBox(10, logoIcon, logoText);
        logo.getStyleClass().add("logo");

        this.getChildren().add(logo);
        //this.setMinHeight(Double.MAX_VALUE);
    }

    private void createUserInfo() {
        String imageURL = currentUser.getImageUser();
        ImageView userAvatar = new ImageView(new Image(imageURL));
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);
        userAvatar.getStyleClass().add("user-avatar");

        Circle clip = new Circle(userAvatar.getFitWidth() / 2, userAvatar.getFitHeight() / 2, userAvatar.getFitWidth() / 2);
        userAvatar.setClip(clip);

        String cUserName = currentUser.getNameUser() + " " + currentUser.getLastNameUser() ;
        Label userName = new Label(cUserName);
        userName.getStyleClass().add("user-name");

        String cUserRol = currentUser.getRolUser();
        Label userRole = new Label(cUserRol);
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
        btnMeseros = crearMenuButton("Meseros", "M7 14s-1 0-1-1s1-4 5-4s5 3 5 4s-1 1-1 1zm4-6a3 3 0 1 0 0-6a3 3 0 0 0 0 6m-5.784 6A2.24 2.24 0 0 1 5 13c0-1.355.68-2.75 1.936-3.72A6.3 6.3 0 0 0 5 9c-4 0-5 3-5 4s1 1 1 1zM4.5 8a2.5 2.5 0 1 0 0-5a2.5 2.5 0 0 0 0 5");
        btnMenu = crearMenuButton("Productos", "M4.505 2h-.013a.5.5 0 0 0-.176.036a.5.5 0 0 0-.31.388C3.99 2.518 3.5 5.595 3.5 7c0 .95.442 1.797 1.13 2.345c.25.201.37.419.37.601v.5q0 .027-.003.054c-.027.26-.151 1.429-.268 2.631C4.614 14.316 4.5 15.581 4.5 16a2 2 0 1 0 4 0c0-.42-.114-1.684-.229-2.869a302 302 0 0 0-.268-2.63L8 10.446v-.5c0-.183.12-.4.37-.601A3 3 0 0 0 9.5 7c0-1.408-.493-4.499-.506-4.577a.5.5 0 0 0-.355-.403A.5.5 0 0 0 8.51 2h-.02h.001a.505.505 0 0 0-.501.505v4a.495.495 0 0 1-.99.021V2.5a.5.5 0 0 0-1 0v4l.001.032a.495.495 0 0 1-.99-.027V2.506A.506.506 0 0 0 4.506 2M11 6.5A4.5 4.5 0 0 1 15.5 2a.5.5 0 0 1 .5.5v6.978l.02.224a626 626 0 0 1 .228 2.696c.124 1.507.252 3.161.252 3.602a2 2 0 1 1-4 0c0-.44.128-2.095.252-3.602c.062-.761.125-1.497.172-2.042l.03-.356H12.5A1.5 1.5 0 0 1 11 8.5zM8.495 2h-.004z");
        btnMesas = crearMenuButton("Reportes", "m6 20l1.5-3.75q.225-.575.725-.913T9.35 15H11v-4.025Q7.175 10.85 4.587 9.85T2 7.5q0-1.45 2.925-2.475T12 4q4.175 0 7.088 1.025T22 7.5q0 1.35-2.588 2.35T13 10.975V15h1.65q.6 0 1.113.338t.737.912L18 20h-2l-1.2-3H9.2L8 20z");

        // Set dashboard as active by default
        btnDashboard.getStyleClass().add("active");

        VBox menu = new VBox(8, btnDashboard, btnMenu, btnMesas, btnMeseros);
        menu.getStyleClass().add("sidebar-menu");


        this.getChildren().add(menu);
    }

    private Button crearMenuButton(String text, String svgPath) {
        SVGPath icon = createScaledSvgIcon(svgPath, 24);

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setPrefSize(24, 24);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.getStyleClass().add("icon-container");

        Label label = new Label(text);
        label.getStyleClass().add("menu-label");

        HBox content = new HBox(10, iconContainer, label);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPrefHeight(40); // altura uniforme
        content.getStyleClass().add("button-content");

        Button button = new Button();
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("menu-button");
        button.setOnAction(e -> {
            setActiveButton(button);
            if (viewChangeListener != null) {
                viewChangeListener.accept(text);
            }
        });

        return button;
    }

    private SVGPath createScaledSvgIcon(String path, double size) {
        SVGPath icon = new SVGPath();
        icon.setContent(path);
        icon.getStyleClass().add("icon");

        // Establecer tamaño fijo usando prefSize y mantener aspecto con escala
        icon.setScaleX(1);
        icon.setScaleY(1);

        // Forzar tamaño de contenedor usando StackPane
        StackPane wrapper = new StackPane(icon);
        wrapper.setPrefSize(size, size);
        wrapper.setMinSize(size, size);
        wrapper.setMaxSize(size, size);
        wrapper.setAlignment(Pos.CENTER);

        icon.setScaleX(size / 24);
        icon.setScaleY(size / 24);

        return icon;
    }

    private void setActiveButton(Button activeButton) {
        Arrays.asList(btnDashboard, btnMenu, btnMesas, btnMeseros)
                .forEach(btn -> btn.getStyleClass().remove("active"));
        activeButton.getStyleClass().add("active");
    }
}