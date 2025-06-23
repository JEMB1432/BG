package jemb.bistrogurmand.views.Lider;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import jemb.bistrogurmand.views.Admin.Sidebar;

public class ResumenView {

    private BorderPane view;

    public ResumenView() {
        createResumen();
    }

    private void createResumen() {
        view = new BorderPane();

        Sidebar sidebar = new Sidebar();
        sidebar.setViewChangeListener(this::changeCentralContent);

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //BorderPane.setAlignment(principalContent, Pos.CENTER);
        view.setLeft(sidebar);
        view.setCenter(principalContent);
    }

}
