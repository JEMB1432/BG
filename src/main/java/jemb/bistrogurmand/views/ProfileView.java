package jemb.bistrogurmand.views;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import jemb.bistrogurmand.Controllers.WaiterController;
import jemb.bistrogurmand.utils.ImgBBUploader;
import jemb.bistrogurmand.utils.User;
import jemb.bistrogurmand.utils.UserSession;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileView {
    private final BorderPane view;
    private final GridPane grid;
    private final User currentUser;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField phoneField;
    private TextField emailField;
    private ImageView userAvatar;
    private Button editButton;
    private Button saveButton;
    private Button cancelButton;
    private Button changeImageButton;
    private boolean editMode = false;

    private String originalImageUrl;
    private String newImageUrl;

    private WaiterController waiterController;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{1,3}?\\s?[0-9]{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final Map<TextField, PauseTransition> validationPauses = new HashMap<>();

    public ProfileView() {
        view = new BorderPane();
        grid = new GridPane();
        currentUser = new UserSession().getCurrentUser();
        waiterController = new WaiterController();

        newImageUrl = currentUser.getUserImage();

        view.getStylesheets().add(getClass().getResource("/jemb/bistrogurmand/CSS/profile.css").toExternalForm());
        view.getStyleClass().add("profile-container");
        grid.getStyleClass().add("form-grid");

        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(30));

        createAvatarSection();
        createForm();
        setupButtonActions();
        setupValidationPauses();

        view.setCenter(grid);
    }

    private void createAvatarSection() {
        VBox avatarContainer = new VBox(15);
        avatarContainer.setAlignment(Pos.CENTER);
        avatarContainer.getStyleClass().add("avatar-section");

        originalImageUrl = currentUser.getUserImage() != null ?
                currentUser.getUserImage() :
                "/jemb/bistrogurmand/Icons/user.png";

        Image image = new Image(originalImageUrl, true);
        userAvatar = new ImageView(image);
        userAvatar.setFitWidth(120);
        userAvatar.setFitHeight(120);
        userAvatar.setPreserveRatio(true);

        Circle clip = new Circle(60, 60, 60);
        userAvatar.setClip(clip);
        userAvatar.getStyleClass().add("user-avatar");

        String cUserName = currentUser.getFirstName() + " " + currentUser.getLastName();
        Label userName = new Label(cUserName);
        userName.getStyleClass().add("user-name");

        changeImageButton = new Button("Cambiar Imagen");
        changeImageButton.getStyleClass().add("action-button");
        changeImageButton.setVisible(false);

        avatarContainer.getChildren().addAll(userAvatar, userName, changeImageButton);
        view.setTop(avatarContainer);
        BorderPane.setMargin(avatarContainer, new Insets(0, 0, 30, 0));
    }

    private void createForm() {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Label firstNameLabel = new Label("Nombre");
        firstNameField = createTextField(currentUser.getFirstName());

        Label lastNameLabel = new Label("Apellido");
        lastNameField = createTextField(currentUser.getLastName());

        Label phoneLabel = new Label("Teléfono");
        phoneField = createTextField(currentUser.getPhone());

        Label emailLabel = new Label("Email");
        emailField = createTextField(currentUser.getEmail());

        firstNameLabel.getStyleClass().add("input-label");
        lastNameLabel.getStyleClass().add("input-label");
        phoneLabel.getStyleClass().add("input-label");
        emailLabel.getStyleClass().add("input-label");

        grid.add(firstNameLabel, 0, 0);
        grid.add(firstNameField, 0, 1);
        grid.add(lastNameLabel, 1, 0);
        grid.add(lastNameField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 0, 3);
        grid.add(emailLabel, 1, 2);
        grid.add(emailField, 1, 3);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getStyleClass().add("button-container");

        editButton = new Button("Editar");
        editButton.getStyleClass().add("primary-button");

        saveButton = new Button("Guardar");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setVisible(false);

        cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setVisible(false);

        buttonBox.getChildren().addAll(cancelButton, saveButton, editButton);
        grid.add(buttonBox, 0, 4, 2, 1);
    }

    private TextField createTextField(String value) {
        TextField field = new TextField(value);
        field.setDisable(true);
        field.getStyleClass().add("profile-field");
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private void setupValidationPauses() {
        initValidationPause(firstNameField, NAME_PATTERN, 250);
        initValidationPause(lastNameField, NAME_PATTERN, 250);
        initValidationPause(phoneField, PHONE_PATTERN, 250);
        initValidationPause(emailField, EMAIL_PATTERN, 250);
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> validateField(field, pattern));
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (field.isDisabled()) return;

            field.getStyleClass().removeAll("input-error", "input-valid");

            // Reiniciar el timer de validación
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validateField(TextField field, Pattern pattern) {
        if (field.isDisabled()) return;

        String text = field.getText().trim();
        boolean isValid = pattern.matcher(text).matches();

        // Limpiar clases previas
        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) {
            // No aplicar ningún estilo si está vacío
            return;
        }

        if (isValid) {
            field.getStyleClass().add("input-valid");
        } else {
            field.getStyleClass().add("input-error");
        }
    }

    private void setupButtonActions() {
        editButton.setOnAction(e -> enableEditMode());
        saveButton.setOnAction(e -> saveChanges());
        cancelButton.setOnAction(e -> cancelChanges());
        changeImageButton.setOnAction(e -> changeProfileImage());
    }

    private void enableEditMode() {
        editMode = true;
        firstNameField.setDisable(false);
        lastNameField.setDisable(false);
        phoneField.setDisable(false);
        emailField.setDisable(false);

        // Validar inmediatamente al entrar en modo edición
        validateField(firstNameField, NAME_PATTERN);
        validateField(lastNameField, NAME_PATTERN);
        validateField(phoneField, PHONE_PATTERN);
        validateField(emailField, EMAIL_PATTERN);

        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        changeImageButton.setVisible(true);
        editButton.setVisible(false);
    }

    private void disableEditMode() {
        editMode = false;
        firstNameField.setDisable(true);
        lastNameField.setDisable(true);
        phoneField.setDisable(true);
        emailField.setDisable(true);

        // Limpiar estados de validación
        firstNameField.getStyleClass().removeAll("input-error", "input-valid");
        lastNameField.getStyleClass().removeAll("input-error", "input-valid");
        phoneField.getStyleClass().removeAll("input-error", "input-valid");
        emailField.getStyleClass().removeAll("input-error", "input-valid");

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        changeImageButton.setVisible(false);
        editButton.setVisible(true);
    }

    private void saveChanges() {
        List<String> errores = new ArrayList<>();

        if (!validateFieldOnSave(firstNameField, NAME_PATTERN))
            errores.add("Nombre inválido");
        if (!validateFieldOnSave(lastNameField, NAME_PATTERN))
            errores.add("Apellido inválido");
        if (!validateFieldOnSave(phoneField, PHONE_PATTERN))
            errores.add("Teléfono inválido");
        if (!validateFieldOnSave(emailField, EMAIL_PATTERN))
            errores.add("Email inválido");

        if (!errores.isEmpty()) {
            showAlert("Error", String.join("\n", errores), Alert.AlertType.ERROR);
            return;
        }

        User updateUser = new User();
        updateUser.setFirstName(firstNameField.getText().trim());
        updateUser.setLastName(lastNameField.getText().trim());
        updateUser.setPhone(phoneField.getText().trim());
        updateUser.setEmail(emailField.getText().trim());
        updateUser.setRolUser(currentUser.getRolUser());
        updateUser.setStateUser(currentUser.getStateUser());
        updateUser.setUserID(currentUser.getUserID());
        updateUser.setUserImage(newImageUrl);

        if(waiterController.updateWaiterProfile(updateUser)) {
            currentUser.setFirstName(firstNameField.getText().trim());
            currentUser.setLastName(lastNameField.getText().trim());
            currentUser.setPhone(phoneField.getText().trim());
            currentUser.setEmail(emailField.getText().trim());
            currentUser.setUserImage(newImageUrl);
            showAlert("Éxito", "Perfil actualizado correctamente", Alert.AlertType.INFORMATION);
        }else {
            showAlert(null, "Error al actualizar el usuario", Alert.AlertType.ERROR);
        }

        disableEditMode();
    }

    private boolean validateFieldOnSave(TextField field, Pattern pattern) {
        String text = field.getText().trim();
        boolean isValid = pattern.matcher(text).matches();

        if (text.isEmpty()) {
            field.getStyleClass().add("input-error");
            return false;
        }

        if (!isValid) {
            if (!field.getStyleClass().contains("input-error")) {
                field.getStyleClass().add("input-error");
            }
            return false;
        }

        return true;
    }

    private void cancelChanges() {
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        phoneField.setText(currentUser.getPhone());
        emailField.setText(currentUser.getEmail());
        userAvatar.setImage(new Image(originalImageUrl));

        firstNameField.getStyleClass().removeAll("input-error", "input-valid");
        lastNameField.getStyleClass().removeAll("input-error", "input-valid");
        phoneField.getStyleClass().removeAll("input-error", "input-valid");
        emailField.getStyleClass().removeAll("input-error", "input-valid");

        disableEditMode();
    }

    private void changeProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File processedFile = cropAndResizeCenterJPG(selectedFile, 200, 200, 0.7f); // Usar JPG con compresión

                String imageUrl = ImgBBUploader.uploadImage(processedFile.getAbsolutePath());
                if (imageUrl != null) {
                    userAvatar.setImage(new Image(imageUrl));
                    newImageUrl = imageUrl;
                    showAlert("Éxito", "Imagen de perfil actualizada", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "No se pudo subir la imagen", Alert.AlertType.ERROR);
                }

                processedFile.deleteOnExit();
            } catch (IOException ex) {
                showAlert("Error", "Error al procesar la imagen: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public File cropAndResizeCenterJPG(File originalFile, int targetWidth, int targetHeight, float quality) throws IOException {
        BufferedImage originalImage = ImageIO.read(originalFile);

        int cropSize = Math.min(originalImage.getWidth(), originalImage.getHeight());
        int cropX = (originalImage.getWidth() - cropSize) / 2;
        int cropY = (originalImage.getHeight() - cropSize) / 2;
        BufferedImage cropped = originalImage.getSubimage(cropX, cropY, cropSize, cropSize);

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(cropped, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        File tempFile = File.createTempFile("profile_cropped_", ".jpg");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // 0.0f - 1.0f (baja a alta calidad)
            writer.write(null, new javax.imageio.IIOImage(resized, null, null), param);
            writer.dispose();
        }

        return tempFile;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}
