package com.alerts.system;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;

public class PersonDetailsController {

    @FXML
    private ImageView personImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label lastSeenLocationLabel;
    @FXML
    private Label lastSeenDateLabel;
    @FXML
    private Label contactPersonLabel;
    @FXML
    private Label contactNumberLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label statusLabel;
    @FXML
    private ComboBox<String> statusComboBox;

    public static MissingPerson selectedPerson = null;

    private NotificationManager notificationManager;
    private DatabaseManager dbManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MissingPerson currentPerson = null; // Store the current person being viewed

    /**
     * Initializes the controller. This method is automatically called after the FXML file has been loaded.
     * Use this to set initial data or UI elements.
     */
    @FXML
    public void initialize() {
        notificationManager = new NotificationManager(new VBox());
        dbManager = DatabaseManager.getInstance();
        
        if (selectedPerson != null) {
            currentPerson = selectedPerson; // Store the person for later use
            setPersonDetails(selectedPerson);
            if (statusComboBox != null) {
                statusComboBox.getItems().setAll("Missing", "Found", "Deceased");
                statusComboBox.setValue(selectedPerson.getStatus());
            }
            selectedPerson = null; // Clear the static field
            return;
        }
        // For demonstration, set some placeholder data.
        // In a real application, this would be set by a method like 'setPersonDetails(MissingPerson person)'.
        nameLabel.setText("Ahnaf");
        ageLabel.setText("22");
        genderLabel.setText("Male");
        lastSeenLocationLabel.setText("Badda, Dhaka, Bangladesh");
        lastSeenDateLabel.setText("2024-06-10");
        contactPersonLabel.setText("Md. Rahman");
        contactNumberLabel.setText("+880 1711-123456");
        descriptionArea.setText("Medium height, slim build, black hair, brown eyes. Last seen wearing a white t-shirt and black pants.");
        statusLabel.setText("Missing");

        // Load placeholder image
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/alerts/system/placeholder_profile.jpg"));
            personImageView.setImage(image);
        } catch (Exception e) {
            // Handle image loading error gracefully
            // In a production app, you would log this to a proper logging framework
        }
    }

    /**
     * Method to populate the UI with actual MissingPerson data.
     * This would be called by the DashboardController when viewing details.
     * @param person The MissingPerson object to display.
     */
    public void setPersonDetails(MissingPerson person) {
        nameLabel.setText(person.getName());
        ageLabel.setText(String.valueOf(person.getAge()));
        genderLabel.setText(person.getGender());
        lastSeenLocationLabel.setText(person.getLastSeenLocation());
        lastSeenDateLabel.setText(person.getLastSeenDate().toString()); // Assuming LocalDate
        contactPersonLabel.setText(person.getContactPerson());
        contactNumberLabel.setText(person.getContactNumber());
        descriptionArea.setText(person.getDescription());
        statusLabel.setText(person.getStatus());
        if (statusComboBox != null) {
            statusComboBox.getItems().setAll("Missing", "Found", "Deceased");
            statusComboBox.setValue(person.getStatus());
        }

        // Load person's image if available
        if (person.getPhotoPath() != null && !person.getPhotoPath().isEmpty()) {
            try {
                // Assuming photoPath is a valid URL or path accessible from resources
                Image image = new Image(getClass().getResourceAsStream(person.getPhotoPath()));
                personImageView.setImage(image);
            } catch (Exception e) {
                // Handle image loading error gracefully
                // Fallback to placeholder if actual image fails to load
                try {
                    Image image = new Image(getClass().getResourceAsStream("/com/alerts/system/placeholder_profile.jpg"));
                    personImageView.setImage(image);
                } catch (Exception fallbackException) {
                    // Handle fallback image loading error
                }
            }
        } else {
            // Set placeholder if no photo path is provided
            try {
                Image image = new Image(getClass().getResourceAsStream("/com/alerts/system/placeholder_profile.jpg"));
                personImageView.setImage(image);
            } catch (Exception e) {
                // Handle placeholder image loading error
            }
        }
    }

    /**
     * Handles the action when the "Back to Dashboard" button is clicked.
     * Navigates back to the main dashboard view.
     */
    @FXML
    protected void handleBackToDashboard() {
        try {
            Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
        } catch (IOException e) {
            // Show user-friendly error message
            if (notificationManager != null) {
                notificationManager.showNotification("Failed to return to dashboard. Please try again.", NotificationManager.NotificationType.ERROR);
            }
        }
    }

    @FXML
    protected void handleUpdateStatus() {
        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.isEmpty()) return;
        
        if (nameLabel.getText() != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Change status to '" + newStatus + "'?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Status Change");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Update status in database
                    executor.submit(() -> {
                        try {
                            // Get the person_id from the selected person
                            int personId = -1;
                            if (currentPerson != null) {
                                personId = currentPerson.getPersonId();
                            } else {
                                // If currentPerson is null, try to find by name
                                // This is a fallback - ideally currentPerson should always be set
                                personId = dbManager.findPersonIdByName(nameLabel.getText());
                            }
                            
                            if (personId == -1) {
                                Platform.runLater(() -> {
                                    notificationManager.showNotification("Could not find person in database. Please try again.", NotificationManager.NotificationType.ERROR);
                                });
                                return;
                            }
                            
                            boolean success = dbManager.updatePersonStatus(
                                personId,
                                newStatus,
                                null, // foundDate
                                null, // foundLocation
                                "Status updated by user", // notes
                                Main.currentUserId
                            );
                            
                            Platform.runLater(() -> {
                                if (success) {
                                    statusLabel.setText(newStatus);
                                    if (statusComboBox != null) statusComboBox.setValue(newStatus);
                                    
                                    // Update the currentPerson object
                                    if (currentPerson != null) {
                                        currentPerson.setStatus(newStatus);
                                    }
                                    
                                    notificationManager.showNotification("Status updated to: " + newStatus, NotificationManager.NotificationType.SUCCESS);
                                    
                                    // Refresh the dashboard data to update statistics
                                    refreshDashboardData();
                                } else {
                                    notificationManager.showNotification("Failed to update status. Please try again.", NotificationManager.NotificationType.ERROR);
                                }
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                notificationManager.showNotification("Error updating status: " + e.getMessage(), NotificationManager.NotificationType.ERROR);
                            });
                        }
                    });
                }
            });
        }
    }
    
    /**
     * Refresh the dashboard data to update statistics after status change
     */
    private void refreshDashboardData() {
        try {
            // Get the dashboard controller and refresh its data
            Main.refreshDashboardData();
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard data: " + e.getMessage());
        }
    }
}