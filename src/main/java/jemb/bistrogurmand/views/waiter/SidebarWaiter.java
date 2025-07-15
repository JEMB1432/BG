package jemb.bistrogurmand.views.waiter;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;
import jemb.bistrogurmand.application.App;

import java.util.Arrays;
import java.util.function.Consumer;


public class SidebarWaiter extends VBox {
    private Button btnJornada;
    private Button btnMesasAsignadas;
    private Button btnTomarPedido;
    private Button btnModificarPedido;

    User currentUser = new UserSession().getCurrentUser();

    private Consumer<String> viewChangeListener;

    public void setViewChangeListener(Consumer<String> listener) {
        this.viewChangeListener = listener;
    }
}

