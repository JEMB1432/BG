package jemb.bistrogurmand.views.Admin;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;


public class MenuView {
    private BorderPane view;

    public MenuView() {
        view = new BorderPane();

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: transparent;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        principalContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        BorderPane.setAlignment(principalContent, Pos.CENTER);

        view.setCenter(principalContent);
    }

    public BorderPane getView() {
        return view;
    }

}
