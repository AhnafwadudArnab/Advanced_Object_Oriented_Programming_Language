package com.alerts.system;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private VBox notificationContainer;

    private NotificationManager notificationManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        notificationManager = new NotificationManager(notificationContainer);
        dbManager = DatabaseManager.getInstance();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            notificationManager.showNotification("Please fill in all fields.", NotificationManager.NotificationType.ERROR);
            return;
        }
        if (!password.equals(confirmPassword)) {
            notificationManager.showNotification("Passwords do not match.", NotificationManager.NotificationType.ERROR);
            return;
        }
        // Optionally: add more validation for email/phone format

        executor.submit(() -> {
            try {
                boolean success = dbManager.registerUser(username, email, phone, password);
                Platform.runLater(() -> {
                    if (success) {
                        notificationManager.showNotification("Registration successful! You can now log in.", NotificationManager.NotificationType.SUCCESS);
                        // Optionally, redirect to login after a short delay
                        executor.submit(() -> {
                            try { Thread.sleep(1500); Platform.runLater(this::handleBackToLogin); } catch (InterruptedException ignored) {}
                        });
                    } else {
                        notificationManager.showNotification("Registration failed. Username or email may already exist.", NotificationManager.NotificationType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> notificationManager.showNotification("Error: " + e.getMessage(), NotificationManager.NotificationType.ERROR));
            }
        });
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Main.loadScene("LoginPage.fxml", "Login to Missing Person Alert System");
        } catch (Exception e) {
            notificationManager.showNotification("Failed to return to login page.", NotificationManager.NotificationType.ERROR);
        }
    }
} 