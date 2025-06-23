package jemb.bistrogurmand.views.Lider;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ResumenView {

    private BorderPane view;

    public ResumenView() {
        createResumen();
    }

    private void createResumen() {
        view = new BorderPane();

        SidebarLider sidebar = new SidebarLider();
        sidebar.setViewChangeListener(this::changeCentralContent);

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //BorderPane.setAlignment(principalContent, Pos.CENTER);
        view.setLeft(sidebar);
        view.setCenter(principalContent);
    }


    private void changeCentralContent(String views) {
        Region newContent;
        switch (views.toLowerCase()) {
            case "resumen":
                newContent = new Label("Vista de Dashboard");
                break;
            case "cambio":
                newContent = new Label("Vista de Meseros");
                break;
            case "planificacion":
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

