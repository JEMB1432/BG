package jemb.bistrogurmand.views.waiter;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;
import jemb.bistrogurmand.views.Leader.SidebarLeader;

public class DayView {

    private BorderPane view;

    public DayView() {
        createDay();
    }

    private void createDay() {
        view = new BorderPane();

        SidebarWaiter sidebar = new SidebarWaiter();
        sidebar.setViewChangeListener(this::changeCentralContent);

        Region principalContent = new Region();
        principalContent.setStyle("-fx-background-color: #f5f5f5;");
        principalContent.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //BorderPane.setAlignment(principalContent, Pos.CENTER);
        view.setLeft(sidebar);
        view.setCenter(principalContent);
        changeCentralContent("jornada");
    }


    private void changeCentralContent(String views) {
        Region newContent = null;
        switch (views.toLowerCase()) {
            case "jornada": case "resumen de turno":
                newContent = new DayViewInfo().getView();
                break;
            case "asignaciones":
                newContent = new AssignedTablesView().getView();
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