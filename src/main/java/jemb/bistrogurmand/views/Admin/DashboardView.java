package jemb.bistrogurmand.views.Admin;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
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
        Region newContent;

        switch (views.toLowerCase()) {
            case "dashboard":
                newContent = new GenerateDashboardInfo().getView();
                newContent.setMaxHeight(Region.USE_COMPUTED_SIZE);
                break;
            case "meseros":
                newContent = new WaiterView().getView();
                break;
            case "productos":
                newContent = new ProductView().getView();
                break;
            case "mesas":
                newContent = new TablesView().getView();
                break;
            default:
                newContent = new Label("Vista no encontrada");
        }

        ScrollPane scrollable = new ScrollPane(newContent);
        scrollable.setFitToWidth(true);
        scrollable.setFitToHeight(false);
        scrollable.setStyle("-fx-background-color: transparent; -fx-border-width: 0");

        view.setCenter(scrollable);
    }

    public BorderPane getView() {
        return view;
    }
}