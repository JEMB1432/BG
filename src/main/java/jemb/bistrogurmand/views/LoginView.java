package jemb.bistrogurmand.views;

import jemb.bistrogurmand.Controllers.LoginController;
import jemb.bistrogurmand.application.App;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginView {
    private VBox view;

    public LoginView() {
        createLogin();
    }

    private void createLogin() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);

        // Contenedor del login
        VBox loginContainer = new VBox(20);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.getStyleClass().add("login-container");

        // Sección del logo
        VBox logoSection = new VBox(5);
        logoSection.getStyleClass().add("logo-section");

        Label logo = new Label("BISTRO GURMAND");
        logo.getStyleClass().add("logo");

        Label tagline = new Label("Sistema de Gestión Integral");
        tagline.getStyleClass().add("tagline");

        logoSection.getChildren().addAll(logo, tagline);

        // Formulario
        VBox form = new VBox(20);

        // Campo de usuario
        VBox usernameGroup = new VBox(5);
        usernameGroup.getStyleClass().add("input-group");

        Label usernameLabel = new Label("Usuario");
        usernameLabel.getStyleClass().add("input-label");

        HBox usernameFieldContainer = new HBox();
        usernameFieldContainer.getStyleClass().add("input-container");

        ImageView userIcon = new ImageView(
                new Image(getClass().getResource("/jemb/bistrogurmand/Icons/ic--round-email.png").toString())
        );
        userIcon.getStyleClass().add("input-icon");
        userIcon.setPreserveRatio(true);
        userIcon.setFitWidth(20);
        userIcon.setFitHeight(20);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Ingrese su nombre de usuario");
        usernameField.getStyleClass().add("text-field");

        usernameFieldContainer.getChildren().addAll(userIcon, usernameField);
        usernameGroup.getChildren().addAll(usernameLabel, usernameFieldContainer);

        // Campo de contraseña
        VBox passwordGroup = new VBox(5);
        passwordGroup.getStyleClass().add("input-group");

        Label passwordLabel = new Label("Contraseña");
        passwordLabel.getStyleClass().add("input-label");

        HBox passwordFieldContainer = new HBox();
        passwordFieldContainer.getStyleClass().add("input-container");

        //Label passwordIcon = new Label("");
        ImageView passwordIcon = new ImageView(
                new Image(getClass().getResource("/jemb/bistrogurmand/Icons/mdi--password.png").toString())
        );
        passwordIcon.getStyleClass().add("input-icon");
        passwordIcon.setPreserveRatio(true);
        passwordIcon.setFitWidth(20);
        passwordIcon.setFitHeight(20);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese su contraseña");
        passwordField.getStyleClass().add("password-field");

        passwordFieldContainer.getChildren().addAll(passwordIcon, passwordField);
        passwordGroup.getChildren().addAll(passwordLabel, passwordFieldContainer);

        // Botón de login
        Button loginButton = new Button("INICIAR SESIÓN");
        loginButton.getStyleClass().add("login-button");

        // Enlaces del footer
        HBox footerLinks = new HBox(15);
        footerLinks.getStyleClass().add("footer-links");

        Hyperlink forgotPassword = new Hyperlink("¿Olvidó su contraseña?");

        footerLinks.getChildren().addAll(forgotPassword);

        // Versión
        Label version = new Label("Versión 1.0.0 - © 2025 BISTRO GURMAND");
        version.getStyleClass().add("version");

        // Construcción de la interfaz
        form.getChildren().addAll(usernameGroup, passwordGroup, loginButton, footerLinks);
        loginContainer.getChildren().addAll(logoSection, form, version);
        view.getChildren().add(loginContainer);

        loginButton.setOnAction(e -> {
            // Lógica de validación
            LoginController loginController = new LoginController();
            boolean loginIsValid = loginController.TryLogin(usernameField.getText(), passwordField.getText());
            if (loginIsValid) {
                App.loadView("dashboard");
            }else {
                App.loadView("login");
                System.out.println("Datos invalidos");
            }

        });
    }

    public VBox getView() {
        return view;
    }
}