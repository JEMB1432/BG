package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jemb.bistrogurmand.utils.ImgBBUploader;
import jemb.bistrogurmand.utils.Product;

import java.io.File;
import java.io.IOException;

public class EditProductDialog extends Dialog<Product> {
    private TextField nameField;
    private TextField priceField;
    private TextArea descriptionArea;
    private ImageView imageView;
    private String newImageUrl;
    private ToggleGroup availableToggleGroup;

    public EditProductDialog(Product selectedProduct) {
        setTitle("Editar Producto");
        setHeaderText("Editando producto: " + selectedProduct.getName());

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/logo.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid(selectedProduct);
        getDialogPane().setContent(grid);

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateProductFromForm(selectedProduct);
            }
            return null;
        });
    }

    private GridPane createFormGrid(Product product) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        nameField = createTextField(product.getName());
        priceField = createTextField(String.valueOf(product.getPrice()));
        descriptionArea = new TextArea(product.getDescription());
        descriptionArea.getStyleClass().add("text-area");
        descriptionArea.setWrapText(true);

        // Configurar vista de imagen
        imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        loadImage(product.getUrlImage());

        // Botón para cambiar imagen
        Button changeImageBtn = new Button("Cambiar Imagen");
        changeImageBtn.getStyleClass().add("secondary-button");
        changeImageBtn.setOnAction(e -> changeProductImage());

        // Grupo de radio buttons para disponibilidad
        availableToggleGroup = new ToggleGroup();
        HBox availabilityHBox = createAvailabilityRadioButtons(product.getAvailable());

        // Añadir controles al grid
        grid.add(new Label("Nombre:"), 1, 0);
        grid.add(nameField, 2, 0);

        grid.add(new Label("Precio:"), 1, 1);
        grid.add(priceField, 2, 1);

        grid.add(new Label("Descripción:"), 1, 2);
        grid.add(descriptionArea, 2, 2, 2, 1);

        grid.add(new Label("Disponibilidad:"), 1, 3);
        grid.add(availabilityHBox, 2, 3);

        grid.add(new Label("Imagen:"), 1, 4);
        grid.add(imageView, 2, 4);
        grid.add(changeImageBtn, 3, 4);

        return grid;
    }

    private TextField createTextField(String initialValue) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().add("text-field");
        return textField;
    }

    private HBox createAvailabilityRadioButtons(String currentAvailability) {
        RadioButton availableRadio = new RadioButton("Disponible");
        availableRadio.setUserData("1");
        availableRadio.getStyleClass().add("radio-button");

        RadioButton unavailableRadio = new RadioButton("No Disponible");
        unavailableRadio.setUserData("0");
        unavailableRadio.getStyleClass().add("radio-button");

        availableRadio.setToggleGroup(availableToggleGroup);
        unavailableRadio.setToggleGroup(availableToggleGroup);

        if ("1".equals(currentAvailability)) {
            availableRadio.setSelected(true);
        } else {
            unavailableRadio.setSelected(true);
        }

        HBox hbox = new HBox(5, availableRadio, unavailableRadio);
        return hbox;
    }

    private void loadImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Image image = new Image(imageUrl, true);
                imageView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }

    private void changeProductImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Subir la imagen a ImgBB
                String imageUrl = ImgBBUploader.uploadImage(selectedFile.getAbsolutePath());
                if (imageUrl != null) {
                    newImageUrl = imageUrl;
                    loadImage(imageUrl);
                } else {
                    showAlert("Error", "No se pudo subir la imagen", Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Error al subir la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private Product updateProductFromForm(Product product) {
        try {
            product.setName(nameField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setDescription(descriptionArea.getText());
            product.setAvailable(availableToggleGroup.getSelectedToggle().getUserData().toString());

            // Actualizar la URL de la imagen solo si se seleccionó una nueva
            if (newImageUrl != null) {
                product.setUrlImage(newImageUrl);
            }

            return product;
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor ingrese un precio válido", Alert.AlertType.ERROR);
            return null;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
