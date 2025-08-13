package jemb.bistrogurmand.views.Admin;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.CategoryController;
import jemb.bistrogurmand.Controllers.ProductController;
import jemb.bistrogurmand.utils.Category;
import jemb.bistrogurmand.utils.Modals.AddCategoryDialog;
import jemb.bistrogurmand.utils.Modals.AddProductDialog;
import jemb.bistrogurmand.utils.Modals.EditCategoryDialog;
import jemb.bistrogurmand.utils.Modals.EditProductDialog;
import jemb.bistrogurmand.utils.Product;
import jemb.bistrogurmand.utils.ProductColumnFactory;

import java.util.List;
import java.util.Optional;

import static jemb.bistrogurmand.utils.ProductColumnFactory.*;

public class ProductView {
    private final BorderPane view;
    private final TableView<Product> table;
    private final ProductController productController;
    private final CategoryController categoryController;
    private final TextField searchField;
    private final Pagination pagination;
    private Label paginationInfo;
    private final int rowsPerPage = 10;

    private ObservableList<Product> masterProductList;
    private ObservableList<Product> currentDisplayedList;

    public ProductView() {
        masterProductList = FXCollections.observableArrayList();
        currentDisplayedList = FXCollections.observableArrayList();

        view = new BorderPane();
        view.getStyleClass().add("root");
        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/tables.css").toExternalForm());
        view.setPadding(new Insets(20));
        productController = new ProductController();
        categoryController = new CategoryController();

        searchField = new TextField();
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        pagination = new Pagination();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(event -> filterAndPaginateTable());
            pause.playFromStart();
        });

        createTopSection();
        configureTable();
        configurePagination();
        createBottomSection();

        loadInitialData();
    }

    private void createTopSection() {
        VBox globalSection = new VBox();

        HBox topBox = new HBox();
        topBox.getStyleClass().add("top-section");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0,0,20,0));

        HBox titleContent = new HBox();
        titleContent.setAlignment(Pos.BOTTOM_LEFT);
        titleContent.setSpacing(10);

        ImageView iconTitle = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/dish.png").toString()));
        iconTitle.setFitHeight(57);
        iconTitle.setFitWidth(57);
        Label title = new Label("Gestionar Productos");
        title.getStyleClass().add("title");
        title.setFont(new Font(20));

        ImageView imageViewCategory = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/add.png").toString()));
        imageViewCategory.setFitHeight(16);
        imageViewCategory.setFitWidth(16);
        Button addCategoryButton = new Button("Agregar Categoria");
        addCategoryButton.setGraphic(imageViewCategory);
        addCategoryButton.getStyleClass().add("category-button");
        addCategoryButton.setOnAction(event -> addCategoryForm());

        Button editCategoryButton = new Button("Editar Categoria");
        editCategoryButton.getStyleClass().add("category-button-out");
        editCategoryButton.setOnAction(event -> {
            editCategories();
        });

        titleContent.getChildren().addAll(iconTitle, title);

        searchField.setPromptText("Buscar roducto ...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(Double.MAX_VALUE);

        ImageView imageViewAdd = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/add.png").toString()));
        imageViewAdd.setFitHeight(16);
        imageViewAdd.setFitWidth(16);
        Button addbutton = new Button("Agregar producto");
        addbutton.setGraphic(imageViewAdd);
        addbutton.getStyleClass().add("primary-button");
        addbutton.setOnAction(event -> addProductForm());

        ImageView imageViewUpdate = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/update.png").toString()));
        imageViewUpdate.setFitHeight(16);
        imageViewUpdate.setFitWidth(16);
        Button refreshButton = new Button("Actualizar");
        refreshButton.setGraphic(imageViewUpdate);
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(e -> refreshTable());

        topBox.getChildren().addAll(editCategoryButton, addCategoryButton, searchField, addbutton, refreshButton);
        globalSection.getChildren().addAll(titleContent, topBox);
        view.setTop(globalSection);
    }

    private void configureTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        table.getColumns().addAll(
                ProductColumnFactory.createIndexColumn(pagination, rowsPerPage),
                createNameColumn(),
                createDescriptionColumn(),
                createPriceColumn(),
                createStateColumn()
        );
    }

    private void configurePagination() {
        paginationInfo = new Label();

        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateTableForPage(newVal.intValue());
            updatePaginationInfo(newVal.intValue());
        });

        VBox paginationBox = new VBox(10);
        paginationBox.getChildren().addAll(table, paginationInfo, pagination);
        view.setCenter(paginationBox);
    }

    private void updatePaginationInfo(int pageIndex) {
        int from = pageIndex * rowsPerPage + 1;
        int to = Math.min((pageIndex + 1) * rowsPerPage, currentDisplayedList.size());
        int total = currentDisplayedList.size();
        paginationInfo.setText(String.format("Mostrando %d-%d de %d resultados", from, to, total));
    }

    private void loadInitialData() {
        refreshTable();
    }

    private void createBottomSection() {
        HBox buttonBox = new HBox(20);
        buttonBox.getStyleClass().add("bottom-section");
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        ImageView imageViewEdit = new ImageView(new Image(getClass().getResource("/jemb/bistrogurmand/Icons/edit.png").toString()));
        imageViewEdit.setFitHeight(16);
        imageViewEdit.setFitWidth(16);
        Button editButton = new Button("Editar producto seleccionado");
        editButton.setGraphic(imageViewEdit);
        editButton.getStyleClass().add("primary-button");
        editButton.setOnAction(e -> editSelectedProduct());

        Button deleteButton = new Button("Eliminar");
        //deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedProduct());

        buttonBox.getChildren().addAll(editButton);
        view.setBottom(buttonBox);
    }

    private void refreshTable() {
        masterProductList.setAll(productController.getProducts());
        searchField.clear();
        filterAndPaginateTable();
    }

    private void filterAndPaginateTable() {
        String filter = searchField.getText().toLowerCase();
        ObservableList<Product> filteredList = FXCollections.observableArrayList();

        if (filter.isEmpty()) {
            filteredList.setAll(masterProductList);
        } else {
            for (Product product : masterProductList) {
                if (matchesFilter(product, filter)) {
                    filteredList.add(product);
                }
            }
        }

        currentDisplayedList.setAll(filteredList);
        updatePagination();
    }

    private boolean matchesFilter(Product product, String filter) {
        return product.getName().toLowerCase().contains(filter) ||
                product.getDescription().toLowerCase().contains(filter);
    }

    private void updatePagination() {
        int itemCount = currentDisplayedList.size();
        int pageCount = (int) Math.ceil((double) itemCount / rowsPerPage);

        // Asegurar que pageCount sea al menos 1
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);

        // Si estamos en una página que ya no existe, volver a la página 0
        if (pagination.getCurrentPageIndex() >= pageCount && pageCount > 0) {
            pagination.setCurrentPageIndex(0);
        }

        updateTableForPage(pagination.getCurrentPageIndex());
    }

    private void updateTableForPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, currentDisplayedList.size());

        if (currentDisplayedList.isEmpty()) {
            table.setItems(FXCollections.observableArrayList());
        } else {
            table.setItems(FXCollections.observableArrayList(
                    currentDisplayedList.subList(fromIndex, toIndex)
            ));
        }

        updatePaginationInfo(pageIndex);
        table.refresh();
    }

    private void editSelectedProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selección requerida", "Por favor seleccione un producto para editar", 2);
            return;
        }

        try {
            // Obtener categorías disponibles y las actuales del producto
            List<Category> availableCategories = categoryController.getCategories();
            List<Category> currentCategories = productController.getProductCategories(selected.getID_Product());

            // Validar que se obtuvieron las categorías
            if (availableCategories == null || availableCategories.isEmpty()) {
                showAlert("Error", "No se pudieron obtener las categorías disponibles", 3);
                return;
            }

            EditProductDialog dialog = new EditProductDialog(selected, availableCategories, currentCategories);
            Optional<EditProductDialog.ProductWithCategories> result = dialog.showAndWait();

            result.ifPresent(updated -> {
                if (updated != null && updated.getProduct() != null) {
                    // Validar que las categorías seleccionadas no estén vacías
                    if (updated.getSelectedCategories() == null || updated.getSelectedCategories().isEmpty()) {
                        showAlert("Error", "Debe seleccionar al menos una categoría", 2);
                        return;
                    }

                    // Actualizar producto con categorías
                    boolean success = productController.updateProductWithCategories(
                            updated.getProduct(),
                            updated.getSelectedCategories()
                    );

                    if (success) {
                        showAlert("Producto Actualizado", "El producto se ha actualizado correctamente", 1);
                        refreshTable();
                    } else {
                        showAlert("Error", "Ocurrió un error al actualizar el producto", 3);
                    }
                } else {
                    showAlert("Error", "Los datos del producto no son válidos", 3);
                }
            });

        } catch (Exception e) {
            System.err.println("Error en editSelectedProduct: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage(), 3);
        }
    }

    private void deleteSelectedProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Eliminar Producto?");
            alert.setContentText("Está a punto de eliminar a " + selected.getName() + ". ¿Continuar?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Eliminar de todas las listas
                masterProductList.remove(selected);
                currentDisplayedList.remove(selected);

                // Actualizar la paginación y la tabla
                updatePagination();

                // Si la lista está vacía, forzar actualización
                if (currentDisplayedList.isEmpty()) {
                    table.getItems().clear();
                    updatePaginationInfo(0);
                }
            }
        } else {
            showAlert("Selección requerida", "Por favor seleccione un producto para eliminar.", 2);
        }
    }

    private void showAlert(String title, String message, int type) {
        /*
        1 -ALERTA DE INFORMACION
        2 -ALERTA DE WARNING
        3 -ALERTA DE ERROR
         */
        Alert alert;
        switch (type){
            case 1:
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
            case 2:
                alert = new Alert(Alert.AlertType.WARNING);
                break;
            case 3:
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            default:
                alert = new Alert(Alert.AlertType.NONE);
        }

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getScene().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addProductForm() {
        // Obtener las categorías disponibles
        List<Category> availableCategories = categoryController.getCategories();

        AddProductDialog dialog = new AddProductDialog(availableCategories);

        Optional<AddProductDialog.ProductWithCategories> result = dialog.showAndWait();
        result.ifPresent(productWithCategories -> {
            if (productWithCategories != null) {
                Product product = productWithCategories.getProduct();
                List<Category> categories = productWithCategories.getCategories();

                if (productController.addProductWithCategories(product, categories)) {
                    showAlert("Producto Agregado", "El producto se ha agregado correctamente", 1);
                    refreshTable();
                } else {
                    showAlert("Error", "Ocurrió un error al agregar el producto", 3);
                }
            }
        });
    }

    private void addCategoryForm() {
        AddCategoryDialog dialog = new AddCategoryDialog();
        CategoryController categoryController = new CategoryController();

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(categoryData -> {
            if (categoryData != null && categoryData.length == 2) {
                String name = categoryData[0];
                String state = categoryData[1];

                if (categoryController.addCategory(name, state)) {
                    showAlert("Categoría Agregada", "La categoría se ha agregado correctamente", 1);
                    // Actualizar la lista de categorías si es necesario
                } else {
                    showAlert("Error", "Ocurrió un error al agregar la categoría", 3);
                }
            }
        });
    }

    private void editCategories() {
        EditCategoryDialog dialog = new EditCategoryDialog();
        dialog.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }

}
