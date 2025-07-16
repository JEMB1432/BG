package jemb.bistrogurmand.utils.Modals;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
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


public class AddProductDialog extends Dialog<Product> {
    // Declaración de campos como variables de clase
    private TextField nameField;
    private TextField priceField;
    private TextArea descriptionArea;
    private ImageView imageView;
    private String imageUrl;
    private ToggleGroup availableToggleGroup;

    public AddProductDialog() {
        setTitle("Agregar Nuevo Producto");
        setHeaderText("Complete los datos del nuevo producto");

        // Configurar estilos
        getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        // Configurar icono
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/logo.png")));

        // Configurar botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancelButtonType, saveButtonType);

        // Crear formulario
        GridPane grid = createFormGrid();
        getDialogPane().setContent(grid);

        // Validar campos antes de habilitar el botón Guardar
        validateFields();

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createProductFromForm();
            }
            return null;
        });
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("grid-pane");
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Inicialización de campos
        this.nameField = new TextField();
        this.nameField.getStyleClass().add("text-field");

        this.priceField = new TextField();
        this.priceField.getStyleClass().add("text-field");

        this.descriptionArea = new TextArea();
        this.descriptionArea.getStyleClass().add("text-area");
        this.descriptionArea.setWrapText(true);

        // Configurar vista de imagen
        this.imageView = new ImageView();
        this.imageView.setFitHeight(150);
        this.imageView.setFitWidth(150);
        this.imageView.setPreserveRatio(true);

        // Grupo de radio buttons para disponibilidad
        this.availableToggleGroup = new ToggleGroup();
        HBox availabilityHBox = createAvailabilityRadioButtons("1");

        // Botón para seleccionar imagen
        Button selectImageBtn = new Button("Seleccionar Imagen");
        selectImageBtn.getStyleClass().add("secondary-button");
        selectImageBtn.setOnAction(e -> selectProductImage());

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
        grid.add(selectImageBtn, 3, 4);

        return grid;
    }

    private HBox createAvailabilityRadioButtons(String defaultAvailability) {
        RadioButton availableRadio = new RadioButton("Disponible");
        availableRadio.setUserData("1");
        availableRadio.getStyleClass().add("radio-button");

        RadioButton unavailableRadio = new RadioButton("No Disponible");
        unavailableRadio.setUserData("0");
        unavailableRadio.getStyleClass().add("radio-button");

        availableRadio.setToggleGroup(availableToggleGroup);
        unavailableRadio.setToggleGroup(availableToggleGroup);

        if ("1".equals(defaultAvailability)) {
            availableRadio.setSelected(true);
        } else {
            unavailableRadio.setSelected(true);
        }

        return new HBox(10, availableRadio, unavailableRadio);
    }

    private void selectProductImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Subir la imagen a ImgBB
                this.imageUrl = ImgBBUploader.uploadImage(selectedFile.getAbsolutePath());
                if (this.imageUrl != null) {
                    imageView.setImage(new Image(this.imageUrl, true));
                } else {
                    showAlert("Error", "No se pudo subir la imagen", Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Error al subir la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void validateFields() {
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(0));
        saveButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
        priceField.textProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
    }

    private void validateForm(Button saveButton) {
        boolean isValid = !nameField.getText().trim().isEmpty()
                && !priceField.getText().trim().isEmpty()
                && isNumeric(priceField.getText())
                && imageUrl != null;

        saveButton.setDisable(!isValid);
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Product createProductFromForm() {
        try {
            Product product = new Product();
            product.setName(nameField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setDescription(descriptionArea.getText());
            product.setAvailable(availableToggleGroup.getSelectedToggle().getUserData().toString());
            product.setUrlImage(imageUrl);

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
