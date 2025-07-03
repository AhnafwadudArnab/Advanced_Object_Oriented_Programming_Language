package com.alerts.system;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;

public class ProfileController {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private VBox notificationContainer;
    @FXML
    private VBox sidebar;
    @FXML
    private Button hamburgerButton;
    private NotificationManager notificationManager;
    private File selectedAvatarFile;
    private boolean sidebarCollapsed = false;
    private DatabaseManager dbManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    public void initialize() {
        notificationManager = new NotificationManager(notificationContainer);
        dbManager = DatabaseManager.getInstance();
        
        // Load current user profile from database
        loadUserProfile();
        
        // Avatar is already set to placeholder
    }

    private void loadUserProfile() {
        if (Main.currentUserId <= 0) {
            notificationManager.showNotification("User not logged in properly.", NotificationManager.NotificationType.ERROR);
            return;
        }
        
        executor.submit(() -> {
            try {
                Optional<DatabaseManager.UserProfile> profile = dbManager.getUserProfile(Main.currentUserId);
                Platform.runLater(() -> {
                    if (profile.isPresent()) {
                        DatabaseManager.UserProfile userProfile = profile.get();
                        usernameField.setText(userProfile.getUsername());
                        emailField.setText(userProfile.getEmail());
                        phoneField.setText(userProfile.getPhone());
                        
                        // Load avatar if available
                        if (userProfile.getAvatarPath() != null && !userProfile.getAvatarPath().isEmpty()) {
                            try {
                                Image image = new Image(new File(userProfile.getAvatarPath()).toURI().toString());
                                avatarImageView.setImage(image);
                            } catch (Exception e) {
                                // Fallback to placeholder
                                loadPlaceholderAvatar();
                            }
                        } else {
                            loadPlaceholderAvatar();
                        }
                    } else {
                        notificationManager.showNotification("Failed to load user profile.", NotificationManager.NotificationType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    notificationManager.showNotification("Error loading profile: " + e.getMessage(), NotificationManager.NotificationType.ERROR);
                });
            }
        });
    }

    private void loadPlaceholderAvatar() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/alerts/system/placeholder_profile.jpg"));
            avatarImageView.setImage(image);
        } catch (Exception e) {
            // Handle image loading error gracefully
        }
    }

    @FXML
    protected void handleChangeAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (file != null) {
            selectedAvatarFile = file;
            Image image = new Image(file.toURI().toString());
            avatarImageView.setImage(image);
        }
    }

    @FXML
    protected void handleSaveProfile() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Basic validation
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            notificationManager.showNotification("Please fill in all required fields.", NotificationManager.NotificationType.ERROR);
            return;
        }
        
        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                notificationManager.showNotification("Passwords do not match.", NotificationManager.NotificationType.ERROR);
                return;
            }
            // TODO: Implement password update functionality
        }
        
        // Save avatar path
        String avatarPath = null;
        if (selectedAvatarFile != null) {
            avatarPath = selectedAvatarFile.getAbsolutePath();
        }
        
        // Create final copy for lambda
        final String finalAvatarPath = avatarPath;
        
        // Save profile to database
        executor.submit(() -> {
            try {
                boolean success = dbManager.updateUserProfile(Main.currentUserId, email, phone, finalAvatarPath);
                Platform.runLater(() -> {
                    if (success) {
                        notificationManager.showNotification("Profile updated successfully!", NotificationManager.NotificationType.SUCCESS);
                        // Clear password fields
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                    } else {
                        notificationManager.showNotification("Failed to update profile. Please try again.", NotificationManager.NotificationType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    notificationManager.showNotification("Error updating profile: " + e.getMessage(), NotificationManager.NotificationType.ERROR);
                });
            }
        });
    }

    @FXML
    protected void handleCancel() {
        try {
            Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
        } catch (Exception e) {
            notificationManager.showNotification("Failed to return to dashboard.", NotificationManager.NotificationType.ERROR);
        }
    }

    @FXML
    private void toggleSidebar() {
        sidebarCollapsed = SidebarUtils.toggleSidebar(sidebar, sidebarCollapsed);
    }

    @FXML
    protected void handleMapClick() {
        // TODO: Implement map functionality
        // This would load a map view or show live location updates
    }

    @FXML
    protected void handleDashboardClick() {
        try {
            Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
        } catch (Exception e) {
            // Optionally show notification or log
        }
    }

    @FXML
    protected void handleSettingsClick() {
        try {
            Main.loadScene("SettingsView.fxml", "Settings");
        } catch (Exception e) {
            // Optionally show notification or log
        }
    }

    @FXML
    protected void handleLogoutClick() {
        try {
            Main.loadScene("LoginPage.fxml", "Login to Missing Person Alert System");
        } catch (Exception e) {
            // Optionally show notification or log
        }
    }
} 