package jemb.bistrogurmand.views.Leader;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import jemb.bistrogurmand.views.Admin.WaiterView;

public class SummaryView {

    private BorderPane view;

    public SummaryView() {
        createSummary();
    }

    private void createSummary() {
        view = new BorderPane();

        SidebarLeader sidebar = new SidebarLeader();
        sidebar.setViewChangeListener(this::changeCentralContent);

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //BorderPane.setAlignment(principalContent, Pos.CENTER);
        view.setLeft(sidebar);
        view.setCenter(principalContent);
        changeCentralContent("summary");
    }


    private void changeCentralContent(String views) {
        Region newContent;
        switch (views.toLowerCase()) {
            case "summary":
                newContent = new SumTable().getView();
                break;
            case "orderchange":
                newContent = new Label("Vista de Cambio de pedido");
                break;
            case "planification":
                newContent = new Label("Vista de Planificacion");
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

