package com.alerts.system;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<MissingPerson> alertsTable;
    @FXML
    private TableColumn<MissingPerson, String> personNameColumn;
    @FXML
    private TableColumn<MissingPerson, String> lastSeenDateColumn;
    @FXML
    private TableColumn<MissingPerson, String> locationColumn;
    @FXML
    private TableColumn<MissingPerson, String> statusColumn;

    @FXML
    private Label totalMissingLabel;
    @FXML
    private Label totalFoundLabel;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label welcomeMessageLabel;
    @FXML
    private Label lastUpdatedLabel;
    @FXML
    private Label systemStatusLabel;

    @FXML
    private VBox sidebar;
    @FXML
    private VBox tableViewSection;
    @FXML
    private VBox statisticsContainer;
    @FXML
    private VBox recentActivityContainer;
    @FXML
    private VBox mapViewSection;
    @FXML
    private VBox mapContainer;
    @FXML
    private Label mapStatusLabel;
    @FXML
    private Label mapInfoLabel;
    @FXML
    private VBox reportViewSection;
    @FXML
    private VBox reportContainer;
    @FXML
    private Label reportStatusLabel;
    @FXML
    private Label reportInfoLabel;

    private boolean sidebarCollapsed = false;
    private DatabaseManager dbManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ObservableList<MissingPerson> missingPersonsList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private boolean isMapViewActive = false;
    private boolean isReportViewActive = false;

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("[DEBUG] SettingsController initialized");
        dbManager = DatabaseManager.getInstance();
        missingPersonsList = FXCollections.observableArrayList();

        // Set up columns with better formatting
        personNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastSeenDateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getLastSeenDate() != null
                ? cellData.getValue().getLastSeenDate().format(dateFormatter) : ""));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("lastSeenLocation"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set data
        alertsTable.setItems(missingPersonsList);

        // Set up welcome message
        setupWelcomeMessage();

        // Set up system status
        setupSystemStatus();

        // Load data from database
        loadMissingPersonsFromDatabase();

        // Update profile name
        if (profileNameLabel != null) {
            profileNameLabel.setText("Welcome, " + Main.currentUsername + "!");
        }

        // Set up search functionality
        setupSearchFunctionality();
    }

    private void setupWelcomeMessage() {
        if (welcomeMessageLabel != null) {
            String timeOfDay = getTimeOfDay();
            welcomeMessageLabel.setText(timeOfDay + ", " + Main.currentUsername + "!");
            welcomeMessageLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            welcomeMessageLabel.setTextFill(Color.web("#2E86AB"));
        }
    }

    private String getTimeOfDay() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) {
            return "Good Morning"; 
        }else if (hour < 17) {
            return "Good Afternoon"; 
        }else {
            return "Good Evening";
        }
    }

    private void setupSystemStatus() {
        if (systemStatusLabel != null) {
            executor.submit(() -> {
                boolean dbConnected = dbManager.testConnection();
                Platform.runLater(() -> {
                    if (dbConnected) {
                        systemStatusLabel.setText("🟢 System Online");
                        systemStatusLabel.setTextFill(Color.GREEN);
                    } else {
                        systemStatusLabel.setText("🔴 Database Offline");
                        systemStatusLabel.setTextFill(Color.RED);
                    }
                });
            });
        }
    }

    private void setupSearchFunctionality() {
        // Add real-time search as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadMissingPersonsFromDatabase();
            } else {
                // Debounce search to avoid too many database calls
                executor.submit(() -> {
                    try {
                        Thread.sleep(300); // Wait 300ms after user stops typing
                        if (searchField.getText().equals(newValue)) {
                            performSearch(newValue);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        });
    }

    private void performSearch(String query) {
        try {
            List<MissingPerson> searchResults = dbManager.searchMissingPersons(query, Main.currentUserId);
            Platform.runLater(() -> {
                missingPersonsList.clear();
                missingPersonsList.addAll(searchResults);
                updateSummaryStats();
                updateLastUpdatedLabel();
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                showErrorAlert("Search Error", "Failed to search: " + e.getMessage());
            });
        }
    }

    public void loadMissingPersonsFromDatabase() {
        System.out.println("[DEBUG] Loading missing persons from database...");
        executor.submit(() -> {
            try {
                List<MissingPerson> persons = dbManager.getAllMissingPersons();
                Platform.runLater(() -> {
                    missingPersonsList.clear();
                    missingPersonsList.addAll(persons);
                    updateSummaryStats();
                    updateLastUpdatedLabel();
                    updateRecentActivity();
                    
                    // Refresh report view if it's currently active
                    if (isReportViewActive && reportContainer != null) {
                        createDetailedReportDisplay();
                    }
                    
                    System.out.println("[DEBUG] Dashboard refreshed. Total persons: " + persons.size());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showErrorAlert("Database Error", "Failed to load missing persons data: " + e.getMessage());
                    System.err.println("[ERROR] Failed to load missing persons: " + e.getMessage());
                });
            }
        });
    }

    private void updateSummaryStats() {
        int missing = 0, found = 0, total = 0;
        LocalDate today = LocalDate.now();
        int recentReports = 0; // Reports from last 7 days

        for (MissingPerson p : missingPersonsList) {
            total++;
            if ("Missing".equalsIgnoreCase(p.getStatus())) {
                missing++;
            } else if ("Found".equalsIgnoreCase(p.getStatus())) {
                found++;
            }

            // Check for recent reports (last 7 days)
            if (p.getLastSeenDate() != null
                    && p.getLastSeenDate().isAfter(today.minusDays(7))) {
                recentReports++;
            }
        }

        if (totalMissingLabel != null) {
            totalMissingLabel.setText(String.format("🔍 Missing: %d", missing));
            totalMissingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        }
        if (totalFoundLabel != null) {
            totalFoundLabel.setText(String.format("✅ Found: %d", found));
            totalFoundLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        }

        // Add recent activity indicator
        if (recentActivityContainer != null) {
            recentActivityContainer.getChildren().clear();
            Label recentLabel = new Label(String.format("📅 Recent Activity: %d reports in last 7 days", recentReports));
            recentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");
            recentActivityContainer.getChildren().add(recentLabel);
        }
    }

    private void updateLastUpdatedLabel() {
        if (lastUpdatedLabel != null) {
            lastUpdatedLabel.setText("Last updated: " + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss")));
        }
    }

    private void updateRecentActivity() {
        if (statisticsContainer != null) {
            statisticsContainer.getChildren().clear();

            // Add interesting statistics
            Label statsTitle = new Label("📊 Quick Statistics");
            statsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            statisticsContainer.getChildren().add(statsTitle);
            // Calculate some interesting stats
            long totalCount = missingPersonsList.size();
            long missingCount = missingPersonsList.stream()
                    .filter(p -> "Missing".equalsIgnoreCase(p.getStatus())).count();
            long foundCount = missingPersonsList.stream()
                    .filter(p -> "Found".equalsIgnoreCase(p.getStatus())).count();

            if (totalCount > 0) {
                double successRate = (double) foundCount / totalCount * 100;
                Label successRateLabel = new Label(String.format("Success Rate: %.1f%%", successRate));
                successRateLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                statisticsContainer.getChildren().add(successRateLabel);
            }

            // Add age distribution
            long childrenCount = missingPersonsList.stream()
                    .filter(p -> p.getAge() < 18).count();
            long adultsCount = missingPersonsList.stream()
                    .filter(p -> p.getAge() >= 18).count();

            Label ageLabel = new Label(String.format("Age Distribution: %d children, %d adults", childrenCount, adultsCount));
            ageLabel.setStyle("-fx-text-fill: #7f8c8d;");
            statisticsContainer.getChildren().add(ageLabel);
        }
    }

    @FXML
    protected void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            loadMissingPersonsFromDatabase();
            return;
        }
        performSearch(query);
    }
    @FXML
    protected void handleDashboardClick() {
        System.out.println("Dashboard button clicked. Current state - Map: " + isMapViewActive + ", Report: " + isReportViewActive);

        // Show only the dashboard (table view)
        tableViewSection.setVisible(true);
        tableViewSection.setManaged(true);

        reportViewSection.setVisible(false);
        reportViewSection.setManaged(false);

        mapViewSection.setVisible(false);
        mapViewSection.setManaged(false);

        isMapViewActive = false;
        isReportViewActive = false;

        // Update navigation button styles
        updateNavigationButtonStyles();

        // Refresh the dashboard data
        loadMissingPersonsFromDatabase();

        // Force layout update
        if (tableViewSection.getParent() != null) {
            tableViewSection.getParent().requestLayout();
        }

        System.out.println("Switched to dashboard view");
    }

    @FXML
    protected void handleReportViewClick() {
        System.out.println("Report View button clicked. Current state - Map: " + isMapViewActive + ", Report: " + isReportViewActive);

        // Always switch to report view (remove the condition that prevents switching)
        System.out.println("Switching to report view...");

        // Switch to report view
        tableViewSection.setVisible(false);
        mapViewSection.setVisible(false);
        reportViewSection.setVisible(true);
        isReportViewActive = true;
        isMapViewActive = false;

        // Update navigation button styles
        updateNavigationButtonStyles();

        // Load and display reports
        loadReportView();

        System.out.println("Successfully switched to report view");
    }

    @FXML
    protected void handleMapClick() {
        System.out.println("Map View button clicked. Launching MapViewApp...");

        try {
            // Launch the map view in a new window using MapViewApp
            Stage mapStage = new Stage();
            MapViewApp mapApp = new MapViewApp();

            // Pass the current missing persons data to the map
            mapApp.setMissingPersons(missingPersonsList);

            // Set up stage properties
            mapStage.setTitle("Missing Persons Map View");
            mapStage.setMinWidth(800);
            mapStage.setMinHeight(600);

            // Start the map application
            mapApp.start(mapStage);

            System.out.println("MapViewApp launched successfully");

        } catch (Exception e) {
            System.err.println("Error launching MapViewApp: " + e.getMessage());
            showErrorAlert("Map Error", "Failed to load map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleTableViewClick() {
        System.out.println("Table View button clicked. Current state - Map: " + isMapViewActive + ", Report: " + isReportViewActive);
        
        // Show only the dashboard (table view)
        tableViewSection.setVisible(true);
        tableViewSection.setManaged(true);

        reportViewSection.setVisible(false);
        reportViewSection.setManaged(false);

        mapViewSection.setVisible(false);
        mapViewSection.setManaged(false);

        isMapViewActive = false;
        isReportViewActive = false;

        // Update navigation button styles
        updateNavigationButtonStyles();

        // Refresh the dashboard data
        loadMissingPersonsFromDatabase();

        // Force layout update
        if (tableViewSection.getParent() != null) {
            tableViewSection.getParent().requestLayout();
        }

        System.out.println("Successfully switched to table view");
    }

    private void updateNavigationButtonStyles() {
        // Update the visual state of navigation buttons to show which one is active
        String currentView = isMapViewActive ? "Map View" : isReportViewActive ? "Report View" : "Table View";
        System.out.println("View mode changed to: " + currentView);
        System.out.println("Section visibility - Table: " + tableViewSection.isVisible() + 
                          ", Report: " + reportViewSection.isVisible() + 
                          ", Map: " + mapViewSection.isVisible());

        // In a more complete implementation, you would update the CSS classes of the buttons
        // For now, we'll just log the state change
        // Example of what could be done:
        // dashboardButton.getStyleClass().removeAll("nav-button-active");
        // reportButton.getStyleClass().removeAll("nav-button-active");
        // mapButton.getStyleClass().removeAll("nav-button-active");
        // 
        // if (isMapViewActive) {
        //     mapButton.getStyleClass().add("nav-button-active");
        // } else if (isReportViewActive) {
        //     reportButton.getStyleClass().add("nav-button-active");
        // } else {
        //     dashboardButton.getStyleClass().add("nav-button-active");
        // }
    }

    private void loadReportView() {
        if (reportStatusLabel != null) {
            reportStatusLabel.setText("Loading detailed reports...");
        }

        executor.submit(() -> {
            try {
                // Simulate report loading
                Thread.sleep(500);

                Platform.runLater(() -> {
                    if (reportStatusLabel != null) {
                        reportStatusLabel.setText("Reports loaded successfully!");
                    }

                    if (reportInfoLabel != null) {
                        long totalCount = missingPersonsList.size();
                        long missingCount = missingPersonsList.stream()
                                .filter(p -> "Missing".equalsIgnoreCase(p.getStatus())).count();
                        long foundCount = missingPersonsList.stream()
                                .filter(p -> "Found".equalsIgnoreCase(p.getStatus())).count();
                        reportInfoLabel.setText(String.format("Showing detailed analysis for %d total cases (%d missing, %d found)",
                                totalCount, missingCount, foundCount));
                    }

                    // Create detailed report display
                    createDetailedReportDisplay();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    if (reportStatusLabel != null) {
                        reportStatusLabel.setText("Report loading interrupted");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (reportStatusLabel != null) {
                        reportStatusLabel.setText("Error loading reports: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void createDetailedReportDisplay() {
        if (reportContainer != null) {
            reportContainer.getChildren().clear();

            // Create a detailed report view
            VBox reportContent = new VBox(15);
            reportContent.setAlignment(javafx.geometry.Pos.TOP_CENTER);
            reportContent.setPadding(new javafx.geometry.Insets(40));

            Label reportTitle = new Label("📊 Detailed Analysis Report");
            reportTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Summary statistics
            // Set a fixed height for summaryBox for consistent appearance
            VBox summaryBox = new VBox(10);
            summaryBox.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 15; -fx-border-color: #4caf50; -fx-border-width: 1; -fx-border-radius: 5;");
            summaryBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
            summaryBox.setPrefHeight(140); // Box height determined here

            long totalCount = missingPersonsList.size();
            long missingCount = missingPersonsList.stream()
                    .filter(p -> "Missing".equalsIgnoreCase(p.getStatus())).count();
            long foundCount = missingPersonsList.stream()
                    .filter(p -> "Found".equalsIgnoreCase(p.getStatus())).count();

            Label summaryTitle = new Label("📈 Summary Statistics");
            summaryTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32; -fx-font-size: 16px;");

            // Create a grid layout for better organization
            VBox statsGrid = new VBox(8);
            statsGrid.setAlignment(javafx.geometry.Pos.CENTER);

            Label totalLabel = new Label("Total Cases: " + totalCount);
            totalLabel.setStyle("-fx-text-fill: #1b5e20; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label missingLabel = new Label("Currently Missing: " + missingCount);
            missingLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label foundLabel = new Label("Successfully Found: " + foundCount);
            foundLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold; -fx-font-size: 14px;");

            statsGrid.getChildren().addAll(totalLabel, missingLabel, foundLabel);

            // Success rate in a separate centered section
            double successRate = totalCount > 0 ? (double) foundCount / totalCount * 100 : 0;
            VBox successRateBox = new VBox(5);
            successRateBox.setAlignment(javafx.geometry.Pos.CENTER);
            successRateBox.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 10; -fx-border-color: #1976d2; -fx-border-width: 1; -fx-border-radius: 5; -fx-margin: 10 0 0 0;");
            successRateBox.setPrefHeight(60); // Box height determined here
            Label successRateTitle = new Label("🎯 Success Rate");
            successRateTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1565c0; -fx-font-size: 12px;");

            Label successRateLabel = new Label(String.format("%.1f%%", successRate));
            successRateLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-font-size: 24px;");

            successRateBox.getChildren().addAll(successRateTitle, successRateLabel);

            summaryBox.getChildren().addAll(summaryTitle, statsGrid, successRateBox);

            // Age distribution analysis
            VBox ageAnalysisBox = new VBox(10);
            ageAnalysisBox.setStyle("-fx-background-color: #fff3e0; -fx-padding: 15; -fx-border-color: #ff9800; -fx-border-width: 1; -fx-border-radius: 5;");
            ageAnalysisBox.setPrefHeight(90); // Box height determined here

            Label ageTitle = new Label("👥 Age Distribution Analysis");
            ageTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100; -fx-font-size: 16px;");

            long childrenCount = missingPersonsList.stream()
                    .filter(p -> p.getAge() < 18).count();
            long adultsCount = missingPersonsList.stream()
                    .filter(p -> p.getAge() >= 18).count();

            Label childrenLabel = new Label("Children (< 18): " + childrenCount + " cases");
            childrenLabel.setStyle("-fx-text-fill: #bf360c;");

            Label adultsLabel = new Label("Adults (≥ 18): " + adultsCount + " cases");
            adultsLabel.setStyle("-fx-text-fill: #bf360c;");

            ageAnalysisBox.getChildren().addAll(ageTitle, childrenLabel, adultsLabel);

            // Recent activity
            VBox recentActivityBox = new VBox(10);
            recentActivityBox.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 15; -fx-border-color: #2196f3; -fx-border-width: 1; -fx-border-radius: 5;");
            recentActivityBox.setPrefHeight(80); // Box height determined here

            Label recentTitle = new Label("📅 Recent Activity (Last 7 Days)");
            recentTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1565c0; -fx-font-size: 16px;");

            LocalDate today = LocalDate.now();
            long recentReports = missingPersonsList.stream()
                    .filter(p -> p.getLastSeenDate() != null
                    && p.getLastSeenDate().isAfter(today.minusDays(7))).count();

            Label recentLabel = new Label("New Reports: " + recentReports + " cases");
            recentLabel.setStyle("-fx-text-fill: #0d47a1;");

            recentActivityBox.getChildren().addAll(recentTitle, recentLabel);

            // Add all sections to the report
            reportContent.getChildren().addAll(reportTitle, summaryBox, ageAnalysisBox, recentActivityBox);

            // Create ScrollPane to make the report scrollable
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(reportContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            scrollPane.setPadding(new javafx.geometry.Insets(10));

            // Add the scroll pane to the report container
            reportContainer.getChildren().add(scrollPane);
        }
    }

    @FXML
    protected void handleViewAlertsClick() {
        // This method is now replaced by handleReportViewClick
        handleReportViewClick();
    }

    @FXML
    protected void handleNewAlertClick() {
        try {
            Main.loadScene("ReportPersonForm.fxml", "Report Missing Person");
        } catch (IOException e) {
            // Log error and show user-friendly message
            showErrorAlert("Navigation Error", "Failed to load the report form. Please try again.");
        }
    }

    @FXML
    protected void handleProfileClick() {
        try {
            Main.loadScene("ProfileView.fxml", "My Profile");
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load profile page. Please try again.");
        }
    }

    @FXML
    protected void handleLogoutClick() {
        try {
            // Navigate back to the login page
            Main.loadScene("LoginPage.fxml", "Login to Missing Person Alert System");
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to logout. Please try again.");
        }
    }

    @FXML
    protected void onViewDetailsButtonClick(ActionEvent event) {
        MissingPerson selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Selection Error", "Please select a person to view details.");
            return;
        }
        try {
            // Pass selected person via static field
            PersonDetailsController.selectedPerson = selected;
            Main.loadScene("PersonDetailsView.fxml", "Person Details - " + selected.getName());
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load person details. Please try again.");
        }
    }

    @FXML
    protected void onEditAlertButtonClick(ActionEvent event) {
        MissingPerson selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Selection Error", "Please select a person to edit.");
            return;
        }
        ReportPersonFormController.editingPerson = selected;
        try {
            Main.loadScene("ReportPersonForm.fxml", "Edit Missing Person - " + selected.getName());
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load edit form. Please try again.");
        }
    }

    @FXML
    protected void onDeleteAlertButtonClick(ActionEvent event) {
        MissingPerson selected = alertsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Selection Error", "Please select a person to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Missing Person Alert");
        confirm.setContentText("Are you sure you want to delete the alert for '" + selected.getName() + "'?\n\nThis action cannot be undone.");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                executor.submit(() -> {
                    boolean success = dbManager.removeMissingPerson(selected);
                    Platform.runLater(() -> {
                        if (success) {
                            loadMissingPersonsFromDatabase();
                            showInfoAlert("Success", "Alert deleted successfully.");
                        } else {
                            showErrorAlert("Delete Error", "Failed to delete the alert. Please try again.");
                        }
                    });
                });
            }
        });
    }

    @FXML
    protected void handleSettingsClick() {
//        try {
//            Main.loadScene("SettingsView.fxml", "Settings");
//        } catch (Exception e) {
//            showErrorAlert("Navigation Error", "Failed to load settings page. Please try again.");
//        }
    }

    @FXML
    private void toggleSidebar() {
        if (SidebarUtils.toggleSidebar(sidebar, sidebarCollapsed)) {
            sidebarCollapsed = true; 
        }else {
            sidebarCollapsed = false;
        }
    }

    /**
     * Helper method to show error alerts to the user
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Helper method to show info alerts to the user
     */
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Cleanup resources when controller is destroyed
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
