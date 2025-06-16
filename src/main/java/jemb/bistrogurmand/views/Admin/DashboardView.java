package jemb.bistrogurmand.views.Admin;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class DashboardView {
    private BorderPane view;

    public DashboardView() {
        createDashboard();
    }

    private void createDashboard() {
        view = new BorderPane();

        Sidebar sidebar = new Sidebar();
        sidebar.setPrefWidth(250); // Ancho fijo o ajustable
        sidebar.setMaxHeight(Double.MAX_VALUE); // Que se expanda a lo alto

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(600, 400);
        principalContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        BorderPane.setAlignment(principalContent, Pos.CENTER);

        view.setLeft(sidebar);
        view.setCenter(principalContent);
    }

    public BorderPane getView() {
        return view;
    }
}
