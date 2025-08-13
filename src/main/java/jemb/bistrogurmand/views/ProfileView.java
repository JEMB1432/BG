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
import org.mindrot.jbcrypt.BCrypt;

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

    // Campos de contraseña simplificados
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField visiblePasswordField;
    private TextField visibleConfirmPasswordField;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    private String originalImageUrl;
    private String newImageUrl;

    private WaiterController waiterController;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,49}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{1,3}?\\s?[0-9]{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

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
        grid.setPadding(new Insets(15));

        createAvatarSection();
        createForm();
        setupButtonActions();
        setupValidationPauses();

        view.setCenter(grid);
    }

    private void createAvatarSection() {
        VBox avatarContainer = new VBox(5);
        avatarContainer.setAlignment(Pos.CENTER);
        avatarContainer.getStyleClass().add("avatar-section");

        originalImageUrl = currentUser.getUserImage() != null ?
                currentUser.getUserImage() :
                "/jemb/bistrogurmand/Icons/user.png";

        Image image = new Image(originalImageUrl, true);
        userAvatar = new ImageView(image);
        userAvatar.setFitWidth(140);
        userAvatar.setFitHeight(140);
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
        BorderPane.setMargin(avatarContainer, new Insets(0, 0, 10, 0));
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

        Label passwordLabel = new Label("Nueva Contraseña");
        StackPane passwordContainer = createPasswordFieldWithOverlayIcon(true);

        Label confirmPasswordLabel = new Label("Confirmar Contraseña");
        StackPane confirmPasswordContainer = createPasswordFieldWithOverlayIcon(false);

        firstNameLabel.getStyleClass().add("input-label");
        lastNameLabel.getStyleClass().add("input-label");
        phoneLabel.getStyleClass().add("input-label");
        emailLabel.getStyleClass().add("input-label");
        passwordLabel.getStyleClass().add("input-label");
        confirmPasswordLabel.getStyleClass().add("input-label");

        grid.add(firstNameLabel, 0, 0);
        grid.add(firstNameField, 0, 1);
        grid.add(lastNameLabel, 1, 0);
        grid.add(lastNameField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 0, 3);
        grid.add(emailLabel, 1, 2);
        grid.add(emailField, 1, 3);
        grid.add(passwordLabel, 0, 4);
        grid.add(passwordContainer, 0, 5);
        grid.add(confirmPasswordLabel, 1, 4);
        grid.add(confirmPasswordContainer, 1, 5);

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
        grid.add(buttonBox, 0, 6, 2, 1);
    }

    // NUEVA IMPLEMENTACIÓN: Campo con icono superpuesto
    private StackPane createPasswordFieldWithOverlayIcon(boolean isPasswordField) {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER_RIGHT);

        if (isPasswordField) {
            // Campo de contraseña principal
            passwordField = new PasswordField();
            passwordField.setDisable(true);
            passwordField.getStyleClass().add("profile-field");
            passwordField.setMaxWidth(Double.MAX_VALUE);

            visiblePasswordField = new TextField();
            visiblePasswordField.setDisable(true);
            visiblePasswordField.getStyleClass().add("profile-field");
            visiblePasswordField.setMaxWidth(Double.MAX_VALUE);
            visiblePasswordField.setVisible(false);

            // Sincronización de contenido
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordVisible && !visiblePasswordField.getText().equals(newVal)) {
                    visiblePasswordField.setText(newVal);
                }
            });

            visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordVisible && !passwordField.getText().equals(newVal)) {
                    passwordField.setText(newVal);
                }
            });

            container.getChildren().addAll(passwordField, visiblePasswordField);
        } else {
            // Campo de confirmación de contraseña
            confirmPasswordField = new PasswordField();
            confirmPasswordField.setDisable(true);
            confirmPasswordField.getStyleClass().add("profile-field");
            confirmPasswordField.setMaxWidth(Double.MAX_VALUE);

            visibleConfirmPasswordField = new TextField();
            visibleConfirmPasswordField.setDisable(true);
            visibleConfirmPasswordField.getStyleClass().add("profile-field");
            visibleConfirmPasswordField.setMaxWidth(Double.MAX_VALUE);
            visibleConfirmPasswordField.setVisible(false);

            // Sincronización de contenido
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (confirmPasswordVisible && !visibleConfirmPasswordField.getText().equals(newVal)) {
                    visibleConfirmPasswordField.setText(newVal);
                }
            });

            visibleConfirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (confirmPasswordVisible && !confirmPasswordField.getText().equals(newVal)) {
                    confirmPasswordField.setText(newVal);
                }
            });

            container.getChildren().addAll(confirmPasswordField, visibleConfirmPasswordField);
        }

        // Botón de toggle superpuesto
        Button toggleButton = new Button();
        toggleButton.getStyleClass().addAll("eye-button", "overlay-button");
        toggleButton.setFocusTraversable(false);

        // Posicionar el botón en la esquina derecha del campo
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleButton, new Insets(0, 10, 0, 0));

        updateToggleIcon(toggleButton, isPasswordField ? passwordVisible : confirmPasswordVisible);

        toggleButton.setOnAction(e -> {
            if (isPasswordField) {
                togglePasswordVisibility();
                updateToggleIcon(toggleButton, passwordVisible);
            } else {
                toggleConfirmPasswordVisibility();
                updateToggleIcon(toggleButton, confirmPasswordVisible);
            }
        });

        container.getChildren().add(toggleButton);
        return container;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
        }
    }

    private void toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible;

        if (confirmPasswordVisible) {
            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            confirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setVisible(true);
        } else {
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setVisible(false);
        }
    }

    private void updateToggleIcon(Button button, boolean isVisible) {
        button.setText(isVisible ? "🙈" : "👁");
        button.setTooltip(new Tooltip(isVisible ? "Ocultar contraseña" : "Mostrar contraseña"));

        String iconPath = isVisible ?
            "/jemb/bistrogurmand/Icons/eye-off.png" :
            "/jemb/bistrogurmand/Icons/eye.png";

        try {
            ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            eyeIcon.setFitWidth(16);
            eyeIcon.setFitHeight(16);
            button.setGraphic(eyeIcon);
            button.setText("");
        } catch (Exception e) {
            button.setText(isVisible ? "🙈" : "👁");
        }

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
        initValidationPause(passwordField, PASSWORD_PATTERN, 250);
        initValidationPause(visiblePasswordField, PASSWORD_PATTERN, 250);
        initPasswordConfirmationPause(confirmPasswordField, 250);
        initPasswordConfirmationPause(visibleConfirmPasswordField, 250);
    }

    private void initPasswordConfirmationPause(TextField field, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> validatePasswordConfirmation(field));
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (field.isDisabled()) return;
            field.getStyleClass().removeAll("input-error", "input-valid");
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validatePasswordConfirmation(TextField field) {
        if (field.isDisabled()) return;
        String text = field.getText().trim();
        String password = getCurrentPassword();
        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) return;

        if (!text.equals(password)) {
            field.getStyleClass().add("input-error");
        } else {
            field.getStyleClass().add("input-valid");
        }
    }

    private void initValidationPause(TextField field, Pattern pattern, int delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> validateField(field, pattern));
        validationPauses.put(field, pause);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (field.isDisabled()) return;
            field.getStyleClass().removeAll("input-error", "input-valid");
            PauseTransition fieldPause = validationPauses.get(field);
            fieldPause.stop();
            fieldPause.playFromStart();
        });
    }

    private void validateField(TextField field, Pattern pattern) {
        if (field.isDisabled()) return;
        String text = field.getText().trim();
        boolean isValid = pattern.matcher(text).matches();
        field.getStyleClass().removeAll("input-error", "input-valid");

        if (text.isEmpty()) {
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
        passwordField.setDisable(false);
        visiblePasswordField.setDisable(false);
        confirmPasswordField.setDisable(false);
        visibleConfirmPasswordField.setDisable(false);

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
        passwordField.setDisable(true);
        visiblePasswordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        visibleConfirmPasswordField.setDisable(true);

        clearFieldStyles();

        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        changeImageButton.setVisible(false);
        editButton.setVisible(true);
    }

    private void clearFieldStyles() {
        firstNameField.getStyleClass().removeAll("input-error", "input-valid");
        lastNameField.getStyleClass().removeAll("input-error", "input-valid");
        phoneField.getStyleClass().removeAll("input-error", "input-valid");
        emailField.getStyleClass().removeAll("input-error", "input-valid");
        passwordField.getStyleClass().removeAll("input-error", "input-valid");
        visiblePasswordField.getStyleClass().removeAll("input-error", "input-valid");
        confirmPasswordField.getStyleClass().removeAll("input-error", "input-valid");
        visibleConfirmPasswordField.getStyleClass().removeAll("input-error", "input-valid");
    }

    private String getCurrentPassword() {
        return passwordVisible ? visiblePasswordField.getText().trim() : passwordField.getText().trim();
    }

    private String getCurrentConfirmPassword() {
        return confirmPasswordVisible ? visibleConfirmPasswordField.getText().trim() : confirmPasswordField.getText().trim();
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

        String password = getCurrentPassword();
        String confirmPassword = getCurrentConfirmPassword();

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            TextField activePasswordField = passwordVisible ? visiblePasswordField : passwordField;
            if (!validateFieldOnSave(activePasswordField, PASSWORD_PATTERN)) {
                errores.add("Contraseña inválida: Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial");
            }
            if (!password.equals(confirmPassword)) {
                errores.add("Las contraseñas no coinciden");
                TextField activeConfirmField = confirmPasswordVisible ? visibleConfirmPasswordField : confirmPasswordField;
                activeConfirmField.getStyleClass().add("input-error");
            }
        }

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
            showAlert("Éxito", "Perfil actualizado correctamente. \n Por favor vuelva a iniciar sesión para visualizar los cambios.", Alert.AlertType.INFORMATION);
        } else {
            showAlert(null, "Error al actualizar el usuario", Alert.AlertType.ERROR);
        }

        if (!password.isEmpty()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            if (!waiterController.updatePassword(
                    Integer.parseInt(currentUser.getUserID()),
                    hashedPassword))
            {
                showAlert("Error", "No se pudo actualizar la contraseña", Alert.AlertType.ERROR);
            }
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

        // Limpiar campos de contraseña y resetear visibilidad
        passwordField.clear();
        visiblePasswordField.clear();
        confirmPasswordField.clear();
        visibleConfirmPasswordField.clear();

        passwordVisible = false;
        confirmPasswordVisible = false;
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);
        confirmPasswordField.setVisible(true);
        visibleConfirmPasswordField.setVisible(false);

        clearFieldStyles();
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
                File processedFile = cropAndResizeCenterJPG(selectedFile, 200, 200, 0.7f);

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
            param.setCompressionQuality(quality);
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

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/jemb/bistrogurmand/CSS/Dialog.css").toExternalForm()
        );
    }

    public BorderPane getView() {
        return view;
    }
}