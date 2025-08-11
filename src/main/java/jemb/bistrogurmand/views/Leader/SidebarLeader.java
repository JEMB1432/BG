package jemb.bistrogurmand.views.Leader;

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
import jemb.bistrogurmand.application.App;
import jemb.bistrogurmand.utils.UserSession;
import jemb.bistrogurmand.utils.User;

import java.util.Arrays;
import java.util.function.Consumer;

public class SidebarLeader extends VBox {
    private Button btnSummary;
    private Button btnOrderChange;
    private Button btnPlanification;
    private Button btnProfile;

    User currentUser = new UserSession().getCurrentUser();

    private Consumer<String> viewChangeListener;

    public void setViewChangeListener(Consumer<String> listener) {
        this.viewChangeListener = listener;
    }

    public SidebarLeader() {
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
        String placeholderPath = "/jemb/bistrogurmand/Icons/user.png";

        Image placeholderImage = new Image(getClass().getResourceAsStream(placeholderPath));
        ImageView userAvatar = new ImageView(placeholderImage);
        userAvatar.setFitWidth(40);
        userAvatar.setFitHeight(40);

        String imageURL = currentUser.getUserImage();
        Image userImage = new Image(imageURL, 40, 40, true, true, true);

        userImage.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !userImage.isError()) {
                // Cambiar a la imagen remota cuando ya esté cargada
                userAvatar.setImage(userImage);
                System.out.println("Imagen remota cargada y aplicada.");
            }
        });

        userImage.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) {
                System.out.println("Error cargando la imagen remota, usando placeholder.");
            }
        });

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
        btnSummary = crearMenuButton("Resumen de turno", "M12,2C6.477,2,2,6.477,2,12c0,5.523,4.477,10,10,10s10-4.477,10-10C22,6.477,17.523,2,12,2z M15.293,16.707L11,12.414V6h2 v5.586l3.707,3.707L15.293,16.707z");
        btnOrderChange = crearMenuButton("Cambio de pedido", "M 7.425781 2.761719 C 6.179688 3.238281 5.265625 4.816406 5.488281 6.078125 C 5.625 6.882812 6.375 7.878906 7.09375 8.226562 C 8.011719 8.652344 8.789062 8.605469 9.691406 8.070312 C 11.460938 6.996094 11.519531 4.34375 9.796875 3.125 C 9.046875 2.589844 8.203125 2.464844 7.425781 2.761719 Z M 7.425781 2.761719 M 17.714844 5.195312 C 17.488281 5.605469 17.503906 5.777344 17.773438 6.0625 C 17.96875 6.269531 17.941406 6.300781 17.535156 6.394531 C 16.800781 6.570312 15.808594 7.691406 15.601562 8.574219 C 15.449219 9.207031 15.359375 9.316406 15.058594 9.316406 C 14.640625 9.316406 14.550781 9.851562 14.941406 10.011719 C 15.074219 10.058594 16.648438 10.105469 18.464844 10.105469 L 21.75 10.105469 L 21.75 9.710938 C 21.75 9.425781 21.675781 9.316406 21.464844 9.316406 C 21.253906 9.316406 21.136719 9.125 21.015625 8.605469 C 20.789062 7.671875 19.996094 6.742188 19.199219 6.488281 C 18.691406 6.332031 18.613281 6.253906 18.75 6.078125 C 18.839844 5.953125 18.898438 5.667969 18.871094 5.414062 C 18.839844 5.050781 18.75 4.957031 18.34375 4.925781 C 18 4.894531 17.835938 4.957031 17.714844 5.195312 Z M 17.714844 5.195312 M 4.800781 14.429688 L 4.800781 19.578125 L 12.148438 19.578125 L 12.148438 16.578125 C 12.148438 14.921875 12.195312 13.578125 12.238281 13.578125 C 12.285156 13.578125 12.703125 14.019531 13.171875 14.558594 C 14.550781 16.136719 14.671875 16.261719 14.953125 16.246094 C 15.570312 16.230469 15.808594 15.929688 16.726562 14.132812 L 17.671875 12.238281 L 18.628906 11.890625 C 19.828125 11.429688 20.21875 11.226562 20.308594 10.957031 C 20.371094 10.785156 19.996094 10.738281 18.300781 10.738281 L 16.214844 10.738281 L 15.644531 12.03125 L 15.058594 13.328125 L 14.191406 11.984375 C 13.695312 11.257812 13.140625 10.484375 12.945312 10.277344 C 12.765625 10.074219 12.601562 9.851562 12.601562 9.789062 C 12.601562 9.570312 11.683594 9.316406 10.949219 9.316406 C 10.246094 9.316406 10.246094 9.316406 10.09375 9.980469 C 9.269531 13.484375 8.398438 16.957031 8.324219 17.019531 C 8.28125 17.070312 8.21875 17.070312 8.203125 17.035156 C 8.175781 17.003906 7.738281 15.269531 7.246094 13.183594 L 6.328125 9.394531 L 5.566406 9.347656 L 4.800781 9.300781 Z M 4.800781 14.429688 ");
        btnPlanification = crearMenuButton("Planificacion", "M 7.28125 2.101562 C 7.171875 2.183594 7.089844 5.210938 7.089844 8.78125 L 7.089844 15.273438 L 12.574219 15.273438 C 15.816406 15.273438 18.246094 15.382812 18.574219 15.546875 C 19.308594 15.953125 19.226562 17.074219 18.410156 17.617188 C 17.917969 17.945312 17.726562 18.300781 17.726562 18.953125 C 17.726562 20.046875 18.4375 20.726562 19.582031 20.726562 C 20.289062 20.726562 20.5625 20.535156 21 19.855469 C 21.519531 19.007812 21.546875 18.4375 21.546875 10.417969 L 21.546875 1.910156 L 14.507812 1.910156 C 10.636719 1.910156 7.363281 1.992188 7.28125 2.101562 M 5.972656 16.445312 C 4.828125 17.316406 4.582031 19.226562 5.453125 20.671875 L 6 21.546875 L 12.191406 21.519531 C 15.628906 21.519531 18.246094 21.4375 18.054688 21.355469 C 17.507812 21.136719 16.910156 19.746094 16.910156 18.710938 C 16.910156 18.027344 17.101562 17.617188 17.589844 17.238281 C 17.972656 16.9375 18.273438 16.554688 18.273438 16.390625 C 18.273438 15.953125 6.574219 15.980469 5.972656 16.445312");
        btnProfile = crearMenuButton("Mi perfil","M8 9c3.85 0 7 2.5 7 4.5a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 1 13.5C1 11.5 4.15 9 8 9m0-8a3.5 3.5 0 1 1 0 7a3.5 3.5 0 0 1 0-7");

        // Set dashboard as active by default
        btnSummary.getStyleClass().add("active");

        VBox menuButtons = new VBox(8, btnSummary, btnOrderChange, btnPlanification, btnProfile);
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
        Arrays.asList(btnSummary,btnOrderChange,btnPlanification, btnProfile)
                .forEach(btn -> btn.getStyleClass().remove("active"));
        activeButton.getStyleClass().add("active");
    }
}