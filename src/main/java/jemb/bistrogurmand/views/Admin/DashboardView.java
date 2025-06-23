package jemb.bistrogurmand.views.Admin;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardView {
    private BorderPane view;

    public DashboardView() {
        createDashboard();
    }

    private void createDashboard() {
        view = new BorderPane();

        Sidebar sidebar = new Sidebar();
        sidebar.setViewChangeListener(this::changeCentralContent);

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        view.setLeft(sidebar);
        view.setCenter(principalContent);
        changeCentralContent("dashboard");
    }

    private void changeCentralContent(String views) {
        Region newContent = null;
        switch (views.toLowerCase()) {
            case "dashboard":
                newContent = new GenerateDashboardInfo().getView();
                break;
            case "meseros":
                newContent = new WaiterView().getView();
                break;
            case "productos":
                newContent = new MenuView().getView();
                break;
            case "reportes":
                newContent = new Label("Vista de Reportes");
                break;
            default:
                newContent = new Label("Vista no encontrada");
        }

        view.setCenter(newContent);
    }

    public BorderPane getView() {
        return view;
    }
}
