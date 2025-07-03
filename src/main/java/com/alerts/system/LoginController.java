package com.alerts.system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox notificationContainer; // Injected from FXML

    private NotificationManager notificationManager; // Instance of our notification utility
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        // Initialize the NotificationManager with the FXML-defined container
        notificationManager = new NotificationManager(notificationContainer);
        dbManager = DatabaseManager.getInstance();
        
        // Test database connection
        if (!dbManager.testConnection()) {
            notificationManager.showNotification("Database connection failed. Please check your database configuration.", NotificationManager.NotificationType.ERROR);
        } else {
            notificationManager.showNotification("Welcome! Please log in.", NotificationManager.NotificationType.INFO);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            notificationManager.showNotification("Please enter both username and password.", NotificationManager.NotificationType.ERROR);
            return;
        }

        // Use executor service for database operations to avoid blocking UI
        executor.submit(() -> {
            try {
                // Authenticate user against database
                Optional<Integer> userId = dbManager.authenticateUser(username, password);
                
                Platform.runLater(() -> {
                    if (userId.isPresent()) {
                        // Store current user information
                        Main.currentUsername = username;
                        Main.currentUserId = userId.get();
                        
                        notificationManager.showNotification("Login successful! Redirecting...", NotificationManager.NotificationType.SUCCESS);
                        
                        // Navigate to dashboard after a short delay
                        executor.submit(() -> {
                            try {
                                Thread.sleep(1500); // Wait for 1.5 seconds
                                Platform.runLater(() -> {
                                    try {
                                        Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
                                    } catch (Exception ex) {
                                        notificationManager.showNotification("Failed to load dashboard.", NotificationManager.NotificationType.ERROR);
                                    }
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
                    } else {
                        notificationManager.showNotification("Invalid username or password. Please try again.", NotificationManager.NotificationType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    notificationManager.showNotification("Login failed. Please check your connection and try again.", NotificationManager.NotificationType.ERROR);
                });
            }
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            Main.loadScene("SignupPage.fxml", "Sign Up - Missing Person Alert System");
        } catch (Exception e) {
            notificationManager.showNotification("Failed to load registration page.", NotificationManager.NotificationType.ERROR);
        }
    }

    /**
     * Cleanup method to be called when the controller is destroyed
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}