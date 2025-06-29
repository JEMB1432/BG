package jemb.bistrogurmand.views.Admin;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GenerateDashboardInfo {
    private BorderPane view;

    public GenerateDashboardInfo() {
        view = new BorderPane();
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/styles.css").toExternalForm());


        Region principalContent = new Region();
        principalContent.setPrefSize(10,10);
        principalContent.setStyle("-fx-background-color: transparent;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        principalContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        BorderPane.setAlignment(principalContent, Pos.CENTER);

        VBox topContent = new VBox();

        HBox titleCardView = new HBox();
        titleCardView.getStyleClass().add("title-card-view");

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/stat.png").toString()));
        iconTitle.setFitHeight(57);
        iconTitle.setFitWidth(57);
        Label titleView = new Label("Dashboard");
        titleView.getStyleClass().add("title");

        HBox cards = new HBox(50);
        topContent.getStyleClass().add("top-content");

        float saleInfo = 1850.00F;
        VBox cardSale = new VBox(20);
        cardSale.getStyleClass().add("card-sale");
        Label titleSale = new Label("Ventas hoy");
        titleSale.getStyleClass().add("title-card");
        Label totalSale = new Label("$" + saleInfo);
        totalSale.getStyleClass().add("total-card");
        cardSale.getChildren().addAll(titleSale, totalSale);

        int orderInfo = 10;
        VBox cardOrder = new VBox(20);
        cardOrder.getStyleClass().add("card-sale");
        Label titleOrder = new Label("Órdenes hoy");
        titleOrder.getStyleClass().add("title-card");
        Label totalOrder = new Label(""+ orderInfo);
        totalOrder.getStyleClass().add("total-card");
        cardOrder.getChildren().addAll(titleOrder, totalOrder);

        titleCardView.getChildren().addAll(iconTitle, titleView);
        cards.getChildren().addAll(cardSale,cardOrder);
        topContent.getChildren().addAll(titleCardView,cards);
        view.setTop(topContent);
        view.setCenter(principalContent);
    }

    public BorderPane getView() {
        return view;
    }
}
