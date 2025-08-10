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
    private Button btnCorrection;
    private Button btnActiveSales;

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
        btnDashboard = crearMenuButton("Jornada", "M2 3v18c0 .6.4 1 1 1h5V2H3c-.6 0-1 .4-1 1m19-1H10v9h12V3c0-.6-.4-1-1-1M10 22h11c.6 0 1-.4 1-1v-8H10z");
        btnMeseros = crearMenuButton("Asignaciones", "m6 20l1.5-3.75q.225-.575.725-.913T9.35 15H11v-4.025Q7.175 10.85 4.587 9.85T2 7.5q0-1.45 2.925-2.475T12 " +
                                        "4q4.175 0 7.088 1.025T22 7.5q0 1.35-2.588 2.35T13 10.975V15h1.65q.6 0 1.113.338t.737.912L18 20h-2l-1.2-3H9.2L8 20z");
        btnCorrection = crearMenuButton("Correcciones","M12 21q-1.875 0-3.512-.712t-2.85-1.925t-1.925-2.85T3 12t.713-3.512t1.924-2.85t2.85-1.925T12 3q2.05 0 3.888.875T19 6.35V5q0-.425.288-.712T20 4t.713.288T21 5v4q0 .425-.288.713T20 10h-4q-.425 0-.712-.288T15 9t.288-.712T16 8h1.75q-1.025-1.4-2.525-2.2T12 5Q9.075 5 7.038 7.038T5 12t2.038 4.963T12 19q2.375 0 4.25-1.425t2.475-3.675q.125-.4.45-.6t.725-.15q.425.05.675.362t.15.688q-.725 2.975-3.15 4.888T12 21m1-9.4l2.5 2.5q.275.275.275.7t-.275.7t-.7.275t-.7-.275l-2.8-2.8q-.15-.15-.225-.337T11 11.975V8q0-.425.288-.712T12 7t.713.288T13 8z");
        btnActiveSales = crearMenuButton("Ventas pasadas", "M17 9H7V4h10zm3 4.09c-.33-.05-.66-.09-1-.09c-3.31 0-6 2.69-6 6H4v-7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2zM10 12H6v2h4zm6 10h2v-6h-2zm4-6v6h2v-6z");


        // Set dashboard as active by default
        btnDashboard.getStyleClass().add("active");

        // Crear contenedor para los botones del menú
        VBox menuButtons = new VBox(8, btnDashboard, btnMeseros, btnCorrection, btnActiveSales);
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
        Arrays.asList(btnDashboard, btnMeseros, btnCorrection, btnActiveSales)
                .forEach(btn -> btn.getStyleClass().remove("active"));
        activeButton.getStyleClass().add("active");
    }
}