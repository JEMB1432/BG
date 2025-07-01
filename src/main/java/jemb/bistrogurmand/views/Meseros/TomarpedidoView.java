package jemb.bistrogurmand.views.Meseros;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Set;
import java.util.stream.Collectors;

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

        //obtener categorias unicas desde los productos
        Set<String> categorias = productos.stream().map(Producto::getCategoria).collect(Collectors.toSet());

        // Crear un botón para cada categoría
        for (String cat : categorias) {
            Button btn = new Button(cat);
            btn.getStyleClass().add("pill-button");
            btn.setOnAction(e -> mostrarModalCategoria(cat)); // Muestra productos al dar clic
            categoriasBox.getChildren().add(btn);
        }
        // Área del resumen del pedido
        Label resumenLabel = new Label("Resumen de pedido:");
        resumenLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        resumenPedido = new ListView<>();
        resumenPedido.setPrefHeight(200);

        // Botones de acción debajo del resumen
        HBox botonesBox = new HBox(10);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnEnviar = new Button("Enviar pedido");
        btnEnviar.getStyleClass().add("btn-success");

        Button btnQuitar = new Button("Quitar producto");
        btnQuitar.getStyleClass().add("btn-danger");

        botonesBox.getChildren().addAll(btnEnviar, btnQuitar);

        private void mostrarModalCategoria(String categoria) {
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Categoría: " + categoria);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));

            TableView<ProductoRow> tabla = new TableView<>();


            // Columnas de la tabla
            TableColumn<ProductoRow, String> colNombre = new TableColumn<>("Producto");
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            TableColumn<ProductoRow, String> colPrecio = new TableColumn<>("Precio");
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

            TableColumn<ProductoRow, TextField> colObs = new TableColumn<>("Observaciones");
            colObs.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

            TableColumn<ProductoRow, Button> colAgregar = new TableColumn<>("Acciones");
            colAgregar.setCellValueFactory(new PropertyValueFactory<>("botonAgregar"));

            tabla.getColumns().addAll(colNombre, colPrecio, colCantidad, colObs, colAgregar);

            // Filtrar productos por categoría seleccionada
            List<Producto> filtrados = productos.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());

            // Convertir productos a filas con control de cantidad/observaciones
            ObservableList<ProductoRow> rows = FXCollections.observableArrayList();
            for (Producto p : filtrados) {
                ProductoRow row = new ProductoRow(p);
                row.getBotonAgregar().setOnAction(e -> {
                    int cantidad = row.getCantidad().getValue();
                    String obs = row.getObservaciones().getText();
                    if (cantidad > 0) {
                        resumenPedido.getItems().add(cantidad + "x " + p.getNombre() + (obs.isEmpty() ? "" : " (" + obs + ")"));
                    }
                });
                rows.add(row);
            }

            tabla.setItems(rows);

            // Botón para cerrar el modal
            Button cerrarBtn = new Button("Cerrar");
            cerrarBtn.setOnAction(e -> modal.close());

            layout.getChildren().addAll(tabla, cerrarBtn);
            Scene scene = new Scene(layout, 600, 400);
            modal.setScene(scene);
            modal.showAndWait();
        }





    }




}
