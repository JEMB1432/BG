package jemb.bistrogurmand.views.Meseros;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TomarpedidoView {
    private VBox content;
    private ListView<String> resumenPedido;
    private List<Producto> productos; // Lista simulada de productos disponibles

    public TomarpedidoView(){
        content = new VBox(20);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.TOP_LEFT);
        content.setStyle("-fx-background-color: #f5f5f5");

        //tarjeta central balnca
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label mesaLabel = new Label("Número de mesa:  #" + idMesa);
        mesaLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        //TomarPedidoView tomarPedido = new TomarPedidoView(mesaSeleccionada.getId());
        //scene.setRoot(tomarPedido.getView());

        Label categoriaLabel = new Label("Categoría:");
        categoriaLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        FlowPane categoriasBox = new FlowPane(10, 10); // Botones de categoría estilo píldora
        categoriasBox.setPrefWrapLength(500);


    }




}
