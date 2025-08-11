package jemb.bistrogurmand.utils.Modals;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.utils.Category;
import jemb.bistrogurmand.utils.ImgBBUploader;
import jemb.bistrogurmand.utils.Product;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class EditProductDialog extends Dialog<EditProductDialog.ProductWithCategories> {

    private TextField nameField;
    private TextField priceField;
    private TextArea descriptionArea;
    private ImageView imageView;
    private String newImageUrl;
    private ToggleGroup availableToggleGroup;
    private ListView<Category> categoriesListView;
    private Set<Integer> selectedCategoryIds;
    private List<Category> availableCategories;
    private List<Category> originalCategories;

    // Patrones de validación consistentes con AddProductDialog
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L}\\p{N} .'-]{1,99}$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();

    public EditProductDialog(Product product, List<Category> availableCategories, List<Category> currentCategories) {
        this.availableCategories = availableCategories;
        this.originalCategories = new ArrayList<>(currentCategories);

        // Inicializar con los IDs de las categorías actuales
        this.selectedCategoryIds = new HashSet<>();
        for (Category category : currentCategories) {
            selectedCategoryIds.add(category.getID_Category());
        }

        setTitle("Editar Producto");
        setHeaderText("Editando producto: " + product.getName());

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
        GridPane grid = createFormGrid(product);
        getDialogPane().setContent(grid);

        // Configurar validaciones en tiempo real
        setupValidationPauses();

        // Validar campos inicialmente
        validateFields();

        // Configurar conversor de resultados
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return updateProductWithCategoriesFromForm(product);
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

        // Campos del formulario con placeholders y clases CSS específicas
        nameField = createTextField(product.getName(), "Nombre del producto");
        priceField = createTextField(String.valueOf(product.getPrice()), "0.00");

        descriptionArea = new TextArea(product.getDescription());
        descriptionArea.getStyleClass().add("text-area");
        descriptionArea.setWrapText(true);
        descriptionArea.setPromptText("Descripción del producto (opcional)");
        descriptionArea.setPrefRowCount(3);

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

        // Lista de categorías con checkboxes
        categoriesListView = new ListView<>();
        categoriesListView.getStyleClass().add("list-view");
        categoriesListView.setCellFactory(param -> new CategoryListCell());
        categoriesListView.setItems(FXCollections.observableArrayList(availableCategories));
        categoriesListView.setPrefHeight(150);

        // Etiquetas con estilos
        Label nameLabel = new Label("Nombre:");
        nameLabel.getStyleClass().add("form-label");

        Label priceLabel = new Label("Precio:");
        priceLabel.getStyleClass().add("form-label");

        Label descriptionLabel = new Label("Descripción:");
        descriptionLabel.getStyleClass().add("form-label");

        Label availabilityLabel = new Label("Disponibilidad:");
        availabilityLabel.getStyleClass().add("form-label");

        Label imageLabel = new Label("Imagen:");
        imageLabel.getStyleClass().add("form-label");

        Label categoriesLabel = new Label("Categorías:");
        categoriesLabel.getStyleClass().add("form-label");

        // Añadir controles al grid
        grid.add(nameLabel, 1, 0);
        grid.add(nameField, 2, 0);

        grid.add(priceLabel, 1, 1);
        grid.add(priceField, 2, 1);

        grid.add(descriptionLabel, 1, 2);
        grid.add(descriptionArea, 2, 2, 2, 1);

        grid.add(availabilityLabel, 1, 3);
        grid.add(availabilityHBox, 2, 3);

        grid.add(imageLabel, 1, 4);
        grid.add(imageView, 2, 4);
        grid.add(changeImageBtn, 3, 4);

        grid.add(categoriesLabel, 1, 5);
        grid.add(categoriesListView, 2, 5, 2, 1);

        return grid;
    }

    private TextField createTextField(String initialValue, String promptText) {
        TextField textField = new TextField(initialValue);
        textField.getStyleClass().addAll("text-field", "product-field");
        textField.setPromptText(promptText);
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

        return new HBox(10, availableRadio, unavailableRadio);
    }

    private class CategoryListCell extends ListCell<Category> {
        private final CheckBox checkBox = new CheckBox();
        private final HBox hbox = new HBox(checkBox);

        public CategoryListCell() {
            super();
            hbox.setSpacing(10);
            checkBox.setOnAction(event -> {
                Category category = getItem();
                if (checkBox.isSelected()) {
                    if (!selectedCategoryIds.contains(category.getID_Category())) {
                        selectedCategoryIds.add(category.getID_Category());
                    }
                } else {
                    selectedCategoryIds.remove(category.getID_Category());
                }
                validateFields(); // Revalidar cuando cambian las categorías
            });
        }

        @Override
        protected void updateItem(Category category, boolean empty) {
            super.updateItem(category, empty);
            if (empty || category == null) {
                setGraphic(null);
            } else {
                checkBox.setText(category.getName());
                checkBox.setSelected(selectedCategoryIds.contains(category.getID_Category()));
                setGraphic(hbox);
            }
        }
    }

    private void loadImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageView.setImage(new Image(imageUrl, true));
                imageView.getStyleClass().remove("product-image-placeholder");
                imageView.getStyleClass().add("product-image-loaded");
            } else {
                // Imagen placeholder por defecto
                imageView.setImage(new Image(getClass().getResourceAsStream("/jemb/bistrogurmand/Icons/no-image.png")));
                imageView.getStyleClass().add("product-image-placeholder");
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

        File selectedFile = fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Validar tamaño del archivo (3MB máximo)
                long fileSizeInMB = selectedFile.length() / (1024 * 1024);
                if (fileSizeInMB > 3) {
                    showAlert("Error", "La imagen no debe exceder 3MB", Alert.AlertType.ERROR);
                    return;
                }

                // Subir la imagen a ImgBB
                newImageUrl = ImgBBUploader.uploadImage(selectedFile.getAbsolutePath());
                if (newImageUrl != null) {
                    loadImage(newImageUrl);
                    validateFields(); // Revalidar cuando se carga una imagen
                } else {
                    showAlert("Error", "No se pudo subir la imagen", Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                showAlert("Error", "Error al subir la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void setupValidationPauses() {
        initValidationPause(nameField, NAME_PATTERN, 100);
        initValidationPause(priceField, PRICE_PATTERN, 100);

        // Listener para categorías seleccionadas
        categoriesListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            validateFields();
        });
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            validateField(field, pattern);
            validateFields(); // Revalidar el formulario completo
        });
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            // Limpiar estilos previos mientras se escribe
            field.getStyleClass().removeAll("input-error", "input-valid");

            // Reiniciar timer de validación
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validateField(TextField field, Pattern pattern) {
        String text = field.getText().trim();
        boolean isValid = !text.isEmpty() && pattern.matcher(text).matches();

        // Limpiar clases previas
        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) {
            // Campo vacío - sin estilo especial pero es inválido
            return;
        }

        if (isValid) {
            field.getStyleClass().add("input-valid");
        } else {
            field.getStyleClass().add("input-error");
        }
    }

    private void validateFields() {
        Button saveButton = (Button) getDialogPane().lookupButton(getDialogPane().getButtonTypes().get(1));

        boolean nameValid = !nameField.getText().trim().isEmpty() &&
                NAME_PATTERN.matcher(nameField.getText().trim()).matches();

        boolean priceValid = !priceField.getText().trim().isEmpty() &&
                PRICE_PATTERN.matcher(priceField.getText().trim()).matches();

        boolean availabilityValid = availableToggleGroup.getSelectedToggle() != null;
        boolean categoriesValid = !selectedCategoryIds.isEmpty();

        boolean isValid = nameValid && priceValid && availabilityValid && categoriesValid;
        saveButton.setDisable(!isValid);
    }

    private ProductWithCategories updateProductWithCategoriesFromForm(Product product) {
        // Validación final antes de actualizar el producto
        List<String> errors = new ArrayList<>();

        if (nameField.getText().trim().isEmpty()) {
            errors.add("El nombre es requerido");
        } else if (!NAME_PATTERN.matcher(nameField.getText().trim()).matches()) {
            errors.add("Nombre inválido");
        }

        if (priceField.getText().trim().isEmpty()) {
            errors.add("El precio es requerido");
        } else if (!PRICE_PATTERN.matcher(priceField.getText().trim()).matches()) {
            errors.add("Precio inválido");
        }

        if (availableToggleGroup.getSelectedToggle() == null) {
            errors.add("Debe seleccionar la disponibilidad");
        }

        if (selectedCategoryIds.isEmpty()) {
            errors.add("Debe seleccionar al menos una categoría");
        }

        if (!errors.isEmpty()) {
            showAlert("Errores de validación", String.join("\n", errors), Alert.AlertType.ERROR);
            return null;
        }

        try {
            // Convertir IDs seleccionados a objetos Category
            List<Category> selectedCategories = new ArrayList<>();
            for (Category category : availableCategories) {
                if (selectedCategoryIds.contains(category.getID_Category())) {
                    selectedCategories.add(category);
                }
            }

            // Actualizar el producto
            product.setName(nameField.getText().trim());
            product.setPrice(Double.parseDouble(priceField.getText().trim()));
            product.setDescription(descriptionArea.getText().trim());
            product.setAvailable(availableToggleGroup.getSelectedToggle().getUserData().toString());

            // Actualizar la URL de la imagen solo si se seleccionó una nueva
            if (newImageUrl != null) {
                product.setUrlImage(newImageUrl);
            }

            return new ProductWithCategories(product, selectedCategories);
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

    public static class ProductWithCategories {
        private final Product product;
        private final List<Category> selectedCategories;

        public ProductWithCategories(Product product, List<Category> selectedCategories) {
            this.product = product;
            this.selectedCategories = new ArrayList<>(selectedCategories);
        }

        public Product getProduct() {
            return product;
        }

        public List<Category> getSelectedCategories() {
            return new ArrayList<>(selectedCategories);
        }
    }
}