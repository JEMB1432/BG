package jemb.bistrogurmand.utils.Modals;

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
import jemb.bistrogurmand.utils.Category;
import jemb.bistrogurmand.utils.ImgBBUploader;
import jemb.bistrogurmand.utils.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProductDialog extends Dialog<EditProductDialog.ProductWithCategories> {

    private TextField nameField;
    private TextField priceField;
    private TextArea descriptionArea;
    private ImageView imageView;
    private String newImageUrl;
    private ToggleGroup availableToggleGroup;
    private ListView<Category> categoriesListView;
    private Set<Integer> selectedCategoryIds; // Cambiado a Set de IDs para evitar duplicados
    private List<Category> availableCategories;
    private List<Category> originalCategories;

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

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(500);
        scrollPane.setMaxHeight(600);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color: transparent;");

        getDialogPane().setContent(scrollPane);

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

        // Lista de categorías con checkboxes
        categoriesListView = new ListView<>();
        categoriesListView.getStyleClass().add("list-view");
        categoriesListView.setCellFactory(param -> new CategoryListCell());
        categoriesListView.setItems(FXCollections.observableArrayList(availableCategories));
        categoriesListView.setPrefHeight(150);

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

        grid.add(new Label("Categorías:"), 1, 5);
        grid.add(categoriesListView, 2, 5, 2, 1);

        return grid;
    }

    private class CategoryListCell extends ListCell<Category> {
        private final CheckBox checkBox = new CheckBox();
        private final HBox hbox = new HBox(checkBox);

        public CategoryListCell() {
            super();
            hbox.setSpacing(10);

            checkBox.setOnAction(event -> {
                Category category = getItem();
                if (category != null) {
                    int categoryId = category.getID_Category();

                    if (checkBox.isSelected()) {
                        selectedCategoryIds.add(categoryId);
                        System.out.println("Agregada categoría: " + category.getName() + " (ID: " + categoryId + ")");
                    } else {
                        selectedCategoryIds.remove(categoryId);
                        System.out.println("Removida categoría: " + category.getName() + " (ID: " + categoryId + ")");
                    }

                    // Debug: mostrar categorías seleccionadas actuales
                    System.out.println("Categorías seleccionadas actuales: " + selectedCategoryIds.size());
                    for (Integer id : selectedCategoryIds) {
                        System.out.println("  - ID: " + id);
                    }
                }
            });
        }

        @Override
        protected void updateItem(Category category, boolean empty) {
            super.updateItem(category, empty);
            if (empty || category == null) {
                setGraphic(null);
            } else {
                checkBox.setText(category.getName());
                // Verificar si esta categoría debe estar seleccionada
                boolean shouldBeSelected = selectedCategoryIds.contains(category.getID_Category());
                checkBox.setSelected(shouldBeSelected);
                setGraphic(hbox);

                // Debug para verificar el estado inicial
                if (shouldBeSelected) {
                    System.out.println("Marcando como seleccionada: " + category.getName() + " (ID: " + category.getID_Category() + ")");
                }
            }
        }
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

        return new HBox(5, availableRadio, unavailableRadio);
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

        File selectedFile = fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (selectedFile != null) {
            // Validar tamaño del archivo
            long fileSizeInMB = selectedFile.length() / (1024 * 1024);
            if (fileSizeInMB > 5) {
                showAlert("Error", "La imagen no debe exceder 5MB", Alert.AlertType.ERROR);
                return;
            }

            try {
                String imageUrl = ImgBBUploader.uploadImage(selectedFile.getAbsolutePath());
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
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

    private ProductWithCategories updateProductWithCategoriesFromForm(Product product) {
        try {
            // Validar nombre
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert("Error", "El nombre del producto es obligatorio", Alert.AlertType.ERROR);
                return null;
            }

            if (name.length() > 255) {
                showAlert("Error", "El nombre no puede exceder 255 caracteres", Alert.AlertType.ERROR);
                return null;
            }

            // Validar precio
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    showAlert("Error", "El precio no puede ser negativo", Alert.AlertType.ERROR);
                    return null;
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Por favor ingrese un precio válido", Alert.AlertType.ERROR);
                return null;
            }

            // Validar disponibilidad
            if (availableToggleGroup.getSelectedToggle() == null) {
                showAlert("Error", "Debe seleccionar la disponibilidad del producto", Alert.AlertType.ERROR);
                return null;
            }

            // Validar categorías
            if (selectedCategoryIds.isEmpty()) {
                showAlert("Error", "Debe seleccionar al menos una categoría", Alert.AlertType.ERROR);
                return null;
            }

            // Convertir IDs seleccionados a objetos Category
            List<Category> selectedCategories = new ArrayList<>();
            for (Category category : availableCategories) {
                if (selectedCategoryIds.contains(category.getID_Category())) {
                    selectedCategories.add(category);
                }
            }

            // Actualizar el producto
            product.setName(name);
            product.setPrice(price);
            product.setDescription(descriptionArea.getText().trim());
            product.setAvailable(availableToggleGroup.getSelectedToggle().getUserData().toString());

            // Actualizar la URL de la imagen solo si se seleccionó una nueva
            if (newImageUrl != null && !newImageUrl.trim().isEmpty()) {
                product.setUrlImage(newImageUrl);
            }

            return new ProductWithCategories(product, selectedCategories);

        } catch (Exception e) {
            System.err.println("Error al procesar datos del formulario: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error inesperado al procesar los datos", Alert.AlertType.ERROR);
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

    // Clase para devolver el producto y categorías seleccionadas
    public static class ProductWithCategories {
        private final Product product;
        private final List<Category> selectedCategories;

        public ProductWithCategories(Product product, List<Category> selectedCategories) {
            this.product = product;
            this.selectedCategories = new ArrayList<>(selectedCategories);

            // Log mejorado para debugging
            System.out.println("=============================");
            System.out.println("=== PRODUCTO ACTUALIZADO ===");
            System.out.println("ID: " + product.getID_Product());
            System.out.println("Nombre: " + product.getName());
            System.out.println("Precio: " + product.getPrice());
            System.out.println("Disponible: " + product.getAvailable());
            System.out.println("Categorías seleccionadas: " + selectedCategories.size());

            // Usar Set para verificar duplicados
            Set<Integer> uniqueIds = new HashSet<>();
            for (Category category : selectedCategories) {
                if (uniqueIds.add(category.getID_Category())) {
                    System.out.println("  - " + category.getName() + " (ID: " + category.getID_Category() + ")");
                } else {
                    System.err.println("  - DUPLICADO: " + category.getName() + " (ID: " + category.getID_Category() + ")");
                }
            }
            System.out.println("=============================");
        }

        public Product getProduct() {
            return product;
        }

        public List<Category> getSelectedCategories() {
            return new ArrayList<>(selectedCategories);
        }
    }
}