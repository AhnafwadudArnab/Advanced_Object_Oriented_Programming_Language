package com.alerts.system;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage; 

public class Main extends Application {

    public static Stage primaryStage;
    public static String currentUsername = "Admin";
    public static int currentUserId = -1; 

    @Override
    public void start(Stage stage) throws IOException {
        
        primaryStage = stage;
        primaryStage.setTitle("Missing Person Alert System"); 

        // Load the Login Page as the first scene when the application starts
        loadScene("LoginPage.fxml", "Login");
    }
    /**
     * Helper method to load a new FXML scene onto the primary stage.
     * @param fxmlFileName The name of the FXML file (e.g., "dashboard-view.fxml")
     * This path is relative to the resources folder for this package.
     * @param title The new title for the application window
     * @throws IOException If the FXML file cannot be loaded (e.g., file not found, malformed FXML)
     */
    public static void loadScene(String fxmlFileName, String title) throws IOException {
        try {
            // Try multiple possible paths for the FXML file
            String[] possiblePaths = {
                fxmlFileName,
                "/com/alerts/system/" + fxmlFileName,
                "com/alerts/system/" + fxmlFileName
            };
            
            Parent root = null;
            Exception lastException = null;
            
            for (String path : possiblePaths) {
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(path)));
                    System.out.println("Successfully loaded FXML from: " + path);
                    break;
                } catch (Exception e) {
                    lastException = e;
                    System.err.println("Failed to load FXML from: " + path + " - " + e.getMessage());
                }
            }
            
            if (root == null) {
                throw new IOException("Could not load FXML file: " + fxmlFileName, lastException);
            }
            
            primaryStage.setScene(new Scene(root)); // Set the loaded FXML as the new scene
            primaryStage.setTitle(title); // Update the window title
            primaryStage.show(); // Display the stage
        } catch (Exception e) {
            System.err.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to load scene: " + fxmlFileName, e);
        }
    }

    /**
     * Helper method to load a new FXML scene and return the FXMLLoader for controller access.
     * @param fxmlFileName The name of the FXML file
     * @param title The new title for the application window
     * @return FXMLLoader instance after loading
     * @throws IOException If the FXML file cannot be loaded
     */
    public static FXMLLoader loadSceneWithController(String fxmlFileName, String title) throws IOException {
        String[] possiblePaths = {
            fxmlFileName,
            "/com/alerts/system/" + fxmlFileName,
            "com/alerts/system/" + fxmlFileName
        };
        FXMLLoader loader = null;
        Parent root = null;
        Exception lastException = null;
        for (String path : possiblePaths) {
            try {
                loader = new FXMLLoader(Main.class.getResource(path));
                root = loader.load();
                System.out.println("Successfully loaded FXML from: " + path);
                break;
            } catch (Exception e) {
                lastException = e;
                System.err.println("Failed to load FXML from: " + path + " - " + e.getMessage());
            }
        }
        if (root == null || loader == null) {
            throw new IOException("Could not load FXML file: " + fxmlFileName, lastException);
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(title);
        primaryStage.show();
        return loader;
    }

    /**
     * Helper method to refresh dashboard data from other controllers
     * This allows other controllers to trigger a refresh of the dashboard statistics
     */
    public static void refreshDashboardData() {
        try {
            // Get the current scene's controller
            if (primaryStage != null && primaryStage.getScene() != null) {
                Object controller = primaryStage.getScene().getUserData();
                if (controller instanceof DashboardController) {
                    DashboardController dashboardController = (DashboardController) controller;
                    dashboardController.loadMissingPersonsFromDatabase();
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args); // This calls the start() method
    }
}