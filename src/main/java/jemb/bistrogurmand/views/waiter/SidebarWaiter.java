package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;
import jemb.bistrogurmand.application.App;

import java.util.Arrays;
import java.util.function.Consumer;


public class SidebarWaiter extends VBox {
    private Button btnDashboard;
    private Button btnMeseros;
    private Button btnMenu;
    private Button btnMesas;
    private Button btnCorrection;

    User currentUser = new UserSession().getCurrentUser();

    private Consumer<String> viewChangeListener;

    public void setViewChangeListener(Consumer<String> listener) {
        this.viewChangeListener = listener;
    }

    public SidebarWaiter() {
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
        String imageURL = currentUser.getUserImage();
        ImageView userAvatar = new ImageView(new Image(imageURL));
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);
        userAvatar.getStyleClass().add("user-avatar");

        Circle clip = new Circle(userAvatar.getFitWidth() / 2, userAvatar.getFitHeight() / 2, userAvatar.getFitWidth() / 2);
        userAvatar.setClip(clip);

        String cUserName = currentUser.getFirstName() + " " + currentUser.getLastName() ;
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
        btnDashboard = crearMenuButton("Jornada", "M3.497 15.602a.7.7 0 1 1 0 1.398H.7a.7.7 0 1 1 0-1.398zm15.803 0a.7.7 0 1 1 0 1.398H5.529a.7.7 0 1 1 0-1.398zM3.497 9.334a.7.7 0 1 1 0 1.399H.7a.7.7 0 1 1 0-1.399zm15.803 0a.7.7 0 1 1 0 1.399H5.528a.7.7 0 1 1 0-1.399zM3.497 3a.7.7 0 1 1 0 1.398H.7A.7.7 0 1 1 .7 3zM19.3 3a.7.7 0 1 1 0 1.398H5.528a.7.7 0 1 1 0-1.398z");
        btnMeseros = crearMenuButton("Asignaciones", "m6 20l1.5-3.75q.225-.575.725-.913T9.35 15H11v-4.025Q7.175 10.85 4.587 9.85T2 7.5q0-1.45 2.925-2.475T12 4q4.175 0 7.088 1.025T22 7.5q0 1.35-2.588 2.35T13 10.975V15h1.65q.6 0 1.113.338t.737.912L18 20h-2l-1.2-3H9.2L8 20z");
        btnCorrection = crearMenuButton("Correcciones","M7.46 2a5.52 5.52 0 0 0-3.91 1.61a5.44 5.44 0 0 0-1.54 2.97a.503.503 0 0 1-.992-.166a6.514 6.514 0 0 1 6.44-5.41a6.55 6.55 0 0 1 4.65 1.93l1.89 2.21v-2.64a.502.502 0 0 1 1.006 0v4a.5.5 0 0 1-.503.5h-3.99a.5.5 0 0 1-.503-.5c0-.275.225-.5.503-.5h2.91l-2.06-2.4a5.53 5.53 0 0 0-3.9-1.6zm1.09 12a5.52 5.52 " +
                "0 0 0 3.91-1.61A5.44 5.44 0 0 0 14 9.42a.504.504 0 0 1 .992.166a6.514 6.514 0 0 1-6.44 5.41a6.55 6.55 0 0 1-4.65-1.93l-1.89-2.21v2.64a.501.501 0 0 1-.858.353a.5.5 0 0 1-.148-.354v-4c0-.276.225-.5.503-.5H5.5c.278 0 .503.224.503.5s-.225.5-.503.5H2.59l2.06 2.4a5.53 5.53 0 0 0 3.9 1.6z");


        // Set dashboard as active by default
        btnDashboard.getStyleClass().add("active");

        // Crear contenedor para los botones del menú
        VBox menuButtons = new VBox(8, btnDashboard, btnMeseros, btnCorrection);
        menuButtons.getStyleClass().add("sidebar-menu");

        VBox.setVgrow(menuButtons, Priority.ALWAYS);

        ImageView logoutIcon = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/logout.png").toString()));
        logoutIcon.setFitHeight(20);
        logoutIcon.setFitWidth(20);
        Button logoutButton = new Button("Cerrar Sesión");
        logoutButton.setGraphic(logoutIcon);
        logoutButton.getStyleClass().add("primary-button");
        logoutButton.setOnAction(e-> {
            currentUser = null;
            App.loadView("login");
        } );

        HBox logoutContainer = new HBox(logoutButton);
        logoutContainer.setAlignment(Pos.BOTTOM_CENTER);
        logoutContainer.getStyleClass().add("logoutContainer");
        logoutContainer.setPadding(new Insets(0, 0, 20, 0));

        VBox menu = new VBox(menuButtons, logoutContainer);
        menu.setFillWidth(true);
        VBox.setVgrow(menu, Priority.ALWAYS);

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
        Arrays.asList(btnDashboard, btnMeseros)
                .forEach(btn -> btn.getStyleClass().remove("active"));
        activeButton.getStyleClass().add("active");
    }
}