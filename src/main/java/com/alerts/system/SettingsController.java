package com.alerts.system;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.VBox;
import com.alerts.system.SidebarUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;

public class SettingsController {
    @FXML
    private CheckBox infoCheckBox;
    @FXML
    private CheckBox successCheckBox;
    @FXML
    private CheckBox warningCheckBox;
    @FXML
    private CheckBox errorCheckBox;
    @FXML
    private VBox sidebar;
    @FXML
    private Button hamburgerButton;
    private boolean sidebarCollapsed = false;

    private DatabaseManager dbManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Static fields for notification preferences (fallback)
    public static Map<String, Boolean> notificationPrefs = new HashMap<>();

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] SettingsController initialized");
        dbManager = DatabaseManager.getInstance();
        try {
            // Load notification preferences from database or use defaults
            loadNotificationPreferences();
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in SettingsController.initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadNotificationPreferences() {
        try {
            infoCheckBox.setSelected(notificationPrefs.getOrDefault("info", true));
            successCheckBox.setSelected(notificationPrefs.getOrDefault("success", true));
            warningCheckBox.setSelected(notificationPrefs.getOrDefault("warning", true));
            errorCheckBox.setSelected(notificationPrefs.getOrDefault("error", true));
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in loadNotificationPreferences: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSave() {
        // Save notification preferences
        notificationPrefs.put("info", infoCheckBox.isSelected());
        notificationPrefs.put("success", successCheckBox.isSelected());
        notificationPrefs.put("warning", warningCheckBox.isSelected());
        notificationPrefs.put("error", errorCheckBox.isSelected());
        
        // TODO: Save to database when user preferences table is implemented
        // For now, just show success message
        handleCancel();
    }

    @FXML
    protected void handleCancel() {
        try {
            Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
        } catch (Exception e) {
            // Ignore
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
    protected void handleProfileClick() {
        try {
            Main.loadScene("ProfileView.fxml", "My Profile");
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