package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;
import jemb.bistrogurmand.application.App;

import java.util.Arrays;

public class SidebarWaiter extends VBox {
    private Button btnDay;
    private Button btnAssignedTables;

    User currentUser = new UserSession().getCurrentUser();

    public SidebarWaiter() {
        getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/sidebar.css").toExternalForm());
        getStyleClass().add("sidebar");

        createLogo();
        createUserInfo();
        createMenu();

        this.setFillWidth(true);
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private void createLogo() {
        SVGPath logoIcon = new SVGPath();
        logoIcon.setContent("M2 5C0 5 0 2 2 2q2-2 4 0c2 0 2 3 0 3v3H2");
        logoIcon.getStyleClass().add("logo-icon");

        Bounds bounds = logoIcon.getBoundsInLocal();
        double scale = bounds.getWidth() > bounds.getHeight() ?
                20 / bounds.getWidth() : 20 / bounds.getHeight();

        logoIcon.setScaleX(scale);
        logoIcon.setScaleY(scale);

        Label logoText = new Label("Bistro");
        logoText.getStyleClass().add("logo-text");

        HBox logo = new HBox(10, logoIcon, logoText);
        logo.getStyleClass().add("logo");
        this.getChildren().add(logo);
    }

    private void createUserInfo() {
        ImageView userAvatar = new ImageView(new Image(currentUser.getUserImage()));
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);
        userAvatar.getStyleClass().add("user-avatar");

        Circle clip = new Circle(20, 20, 20);
        userAvatar.setClip(clip);

        Label userName = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        userName.getStyleClass().add("user-name");

        Label userRole = new Label(currentUser.getRolUser());
        userRole.getStyleClass().add("user-role");

        VBox userDetails = new VBox(5, userName, userRole);
        HBox userInfo = new HBox(10, userAvatar, userDetails);
        userInfo.getStyleClass().add("user-info");
        this.getChildren().add(userInfo);
    }

    private void createMenu() {
        // Crear botones del menú
        btnDay = createMenuButton("Jornada", "M3 12a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1zm0 8a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1v-4a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1zm10 0a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1v-8a1 1 0 0 0-1-1h-6a1 1 0 0 0-1 1zm1-17a1 1 0 0 0-1 1v4a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4a1 1 0 0 0-1-1z");
        btnAssignedTables = createMenuButton("Mesas Asignadas", "m6 20l1.5-3.75q.225-.575.725-.913T9.35 15H11v-4.025Q7.175 10.85 4.587 9.85T2 7.5q0-1.45 2.925-2.475T12 4q4.175 0 7.088 1.025T22 7.5q0 1.35-2.588 2.35T13 10.975V15h1.65q.6 0 1.113.338t.737.912L18 20h-2l-1.2-3H9.2L8 20z");

        // Configurar acciones de los botones
        btnDay.setOnAction(e -> {
            setActiveButton(btnDay);
            App.loadView("day"); // Carga la vista de Jornada
        });

        btnAssignedTables.setOnAction(e -> {
            setActiveButton(btnAssignedTables);
            App.loadView("assigned-tables"); // Carga la vista de Mesas Asignadas
        });

        // Marcar el primero como activo por defecto
        btnDay.getStyleClass().add("active");

        VBox menuButtons = new VBox(8, btnDay, btnAssignedTables);
        menuButtons.getStyleClass().add("sidebar-menu");
        VBox.setVgrow(menuButtons, Priority.ALWAYS);

        // Botón de cerrar sesión
        Button logoutButton = createLogoutButton();
        HBox logoutContainer = new HBox(logoutButton);
        logoutContainer.getStyleClass().add("logoutContainer");
        logoutContainer.setPadding(new Insets(0, 0, 20, 0));

        VBox menu = new VBox(menuButtons, logoutContainer);
        menu.setFillWidth(true);
        this.getChildren().add(menu);
    }

    private Button createMenuButton(String text, String svgPath) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.getStyleClass().add("icon");
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setPrefSize(24, 24);
        iconContainer.getStyleClass().add("icon-container");

        Label label = new Label(text);
        label.getStyleClass().add("menu-label");

        HBox content = new HBox(10, iconContainer, label);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPrefHeight(40);

        Button button = new Button();
        button.setGraphic(content);
        button.getStyleClass().add("menu-button");
        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    private Button createLogoutButton() {
        ImageView logoutIcon = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/logout.png").toString()));
        logoutIcon.setFitHeight(20);
        logoutIcon.setFitWidth(20);

        Button logoutButton = new Button("Cerrar Sesión");
        logoutButton.setGraphic(logoutIcon);
        logoutButton.getStyleClass().add("primary-button");
        logoutButton.setOnAction(e -> {
            currentUser = null;
            App.loadView("login");
        });

        return logoutButton;
    }

    private void setActiveButton(Button activeButton) {
        // Remover clase 'active' de todos los botones
        Arrays.asList(btnDay, btnAssignedTables).forEach(btn ->
                btn.getStyleClass().remove("active"));

        // Agregar clase 'active' al botón seleccionado
        activeButton.getStyleClass().add("active");
    }
}