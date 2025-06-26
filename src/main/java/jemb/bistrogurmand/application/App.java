package jemb.bistrogurmand.application;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jemb.bistrogurmand.views.Admin.DashboardView;
import jemb.bistrogurmand.views.Admin.MenuView;
import jemb.bistrogurmand.views.Admin.User;
import jemb.bistrogurmand.views.Leader.SummaryView;
import jemb.bistrogurmand.views.LoginView;

public class App extends Application {
    private static User currentUser;

    public static void setCurrentUser(User usuario) {
        currentUser = usuario;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private static Stage primaryStage;
    private static Scene mainScene;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Image image = new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/ico.png"));

        primaryStage.setTitle("Bistro Gurmand");
        primaryStage.getIcons().add(image);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        loadView("login");
        primaryStage.show();
    }

    /* Metodo público para cambiar vistas*/
    public static void loadView(String nombreVista) {
        try {
            Pane root;

            switch(nombreVista.toLowerCase()) {
                case "login":
                    root = new LoginView().getView();
                    break;
                case "dashboard":
                    root = new DashboardView().getView();
                    break;
                case "menu":
                    root = new MenuView().getView();
                    break;
                case "summary":
                    root = new SummaryView().getView();
                    break;
                default:
                    throw new IllegalArgumentException("Vista no encontrada: " + nombreVista);
            }

            if (mainScene == null) {
                mainScene = new Scene(root);
                mainScene.setFill(Color.web("#232323"));
                mainScene.getStylesheets().add(
                        App.class.getResource("/jemb/bistrogurmand/CSS/login.css").toExternalForm()
                );
                primaryStage.setScene(mainScene);
            } else {
                mainScene.setRoot(root);
            }

        } catch (Exception e) {
            System.err.println("Error al cargar vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}