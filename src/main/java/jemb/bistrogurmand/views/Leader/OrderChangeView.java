package jemb.bistrogurmand.views.Leader;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class OrderChangeView {
    private BorderPane view;

    public OrderChangeView() {
        view = new BorderPane();

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        principalContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        BorderPane.setAlignment(principalContent, Pos.CENTER);

        view.setCenter(principalContent);
    }

    public BorderPane getView() {
        return view;
    }

}