package com.alerts.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ReportPersonFormController {

    @FXML
    private ImageView personImageView;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField lastSeenLocationField;
    @FXML
    private DatePicker lastSeenDateField;
    @FXML
    private TextField contactPersonField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private VBox notificationContainer;
    private NotificationManager notificationManager;
    private File selectedPhotoFile; // To store the selected image file
    private DatabaseManager dbManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static MissingPerson editingPerson = null;

    @FXML
    public void initialize() {
        try {
            dbManager = DatabaseManager.getInstance();
            if (!dbManager.testConnection()) {
                notificationManager = new NotificationManager(notificationContainer);
                notificationManager.showNotification("Warning: Database connection failed. Some features may not work.", NotificationManager.NotificationType.WARNING);
            }
        } catch (Exception e) {
            System.err.println("Error initializing database manager: " + e.getMessage());
        }
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/alerts/system/placeholder_profile.jpg")));
            personImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
        }
        notificationManager = new NotificationManager(notificationContainer);
        if (editingPerson != null) {
            nameField.setText(editingPerson.getName());
            ageField.setText(String.valueOf(editingPerson.getAge()));
            genderComboBox.setValue(editingPerson.getGender());
            lastSeenLocationField.setText(editingPerson.getLastSeenLocation());
            lastSeenDateField.setValue(editingPerson.getLastSeenDate());
            contactPersonField.setText(editingPerson.getContactPerson());
            contactNumberField.setText(editingPerson.getContactNumber());
            descriptionArea.setText(editingPerson.getDescription());
            // Load photo if available
            try {
                String path = editingPerson.getPhotoPath();
                if (path != null && !path.isEmpty()) {
                    Image image = new Image(getClass().getResourceAsStream(path));
                    personImageView.setImage(image);
                }
            } catch (Exception e) {
                // fallback to placeholder
            }
        }
    }

    @FXML
    protected void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Person Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(personImageView.getScene().getWindow());
        if (file != null) {
            selectedPhotoFile = file;
            Image image = new Image(file.toURI().toString());
            personImageView.setImage(image);
        }
    }

    @FXML
    protected void handleSubmitReport() {
        // Validate input fields
        if (nameField.getText().isEmpty() || lastSeenLocationField.getText().isEmpty()
                || lastSeenDateField.getValue() == null || contactPersonField.getText().isEmpty()
                || contactNumberField.getText().isEmpty()) {
            notificationManager.showNotification("Error: Please fill in all required fields.", NotificationManager.NotificationType.ERROR);
            return;
        }

        String name = nameField.getText().trim();
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age <= 0 || age > 120) {
                notificationManager.showNotification("Error: Please enter a valid age between 1 and 120.", NotificationManager.NotificationType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            notificationManager.showNotification("Error: Please enter a valid age.", NotificationManager.NotificationType.ERROR);
            return;
        }

        String gender = genderComboBox.getValue();
        String lastSeenLocation = lastSeenLocationField.getText().trim();
        LocalDate lastSeenDate = lastSeenDateField.getValue();
        String contactPerson = contactPersonField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (gender == null || (!gender.equals("Male") && !gender.equals("Female") && !gender.equals("Other"))) {
            notificationManager.showNotification("Error: Please select a valid gender.", NotificationManager.NotificationType.ERROR);
            return;
        }

        if (lastSeenDate == null) {
            notificationManager.showNotification("Error: Please select a valid last seen date.", NotificationManager.NotificationType.ERROR);
            return;
        }

        // Validate date is not in the future
        if (lastSeenDate.isAfter(LocalDate.now())) {
            notificationManager.showNotification("Error: Last seen date cannot be in the future.", NotificationManager.NotificationType.ERROR);
            return;
        }

        // Handle the photo saving
        final String[] photoPath = new String[1];
        photoPath[0] = null;
        if (selectedPhotoFile != null) {
            try {
                File photosDir = new File("photos");
                if (!photosDir.exists()) {
                    photosDir.mkdirs();
                }
                // Generate unique filename to avoid conflicts
                String timestamp = String.valueOf(System.currentTimeMillis());
                String extension = "";
                int i = selectedPhotoFile.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedPhotoFile.getName().substring(i);
                }
                String uniqueFileName = "person_" + timestamp + extension;
                File destFile = new File(photosDir, uniqueFileName);
                Files.copy(selectedPhotoFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                photoPath[0] = destFile.getAbsolutePath();
                System.out.println("Photo saved at: " + photoPath[0]);
            } catch (IOException e) {
                notificationManager.showNotification("Error: Failed to save photo. Please try again.", NotificationManager.NotificationType.ERROR);
                System.err.println("Photo save error: " + e.getMessage());
                photoPath[0] = null;
            }
        } else if (editingPerson != null) {
            photoPath[0] = editingPerson.getPhotoPath();
        }

        // Show loading notification
        notificationManager.showNotification("Processing your report...", NotificationManager.NotificationType.INFO);

        // If editing an existing person, update their data
        if (editingPerson != null) {
            executor.submit(() -> {
                try {
                    editingPerson.setName(name);
                    editingPerson.setAge(age);
                    editingPerson.setGender(gender);
                    editingPerson.setLastSeenLocation(lastSeenLocation);
                    editingPerson.setLastSeenDate(lastSeenDate);
                    editingPerson.setContactPerson(contactPerson);
                    editingPerson.setContactNumber(contactNumber);
                    editingPerson.setDescription(description);
                    editingPerson.setPhotoPath(photoPath[0]);
                    editingPerson.setStatus("Missing");  // Ensure status is set to "Missing"

                    final boolean success = dbManager.updateMissingPerson(editingPerson, editingPerson.getPersonId());

                    Platform.runLater(() -> {
                        if (success) {
                            notificationManager.showNotification("Success: Alert updated successfully!", NotificationManager.NotificationType.SUCCESS);
                            editingPerson = null;
                            try {
                                FXMLLoader loader = Main.loadSceneWithController("Dashboard.fxml", "Dashboard");
                                Object controller = loader.getController();
                                if (controller instanceof DashboardController) {
                                    ((DashboardController) controller).loadMissingPersonsFromDatabase();
                                }
                            } catch (IOException | NullPointerException ex) {
                                System.err.println("[ERROR] Failed to reload dashboard: " + ex.getMessage());
                            }
                        } else {
                            notificationManager.showNotification("Error: Failed to update alert. Please try again.", NotificationManager.NotificationType.ERROR);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error updating alert: " + e.getMessage());
                    Platform.runLater(() -> {
                        notificationManager.showNotification("Error updating alert: " + e.getMessage(), NotificationManager.NotificationType.ERROR);
                    });
                }
            });
        } else {
            executor.submit(() -> {
                boolean success = false;
                String errorMsg = null;
                int personId = -1;

                try {
                    MissingPerson newPerson = new MissingPerson(
                            name, age, gender, lastSeenLocation, lastSeenDate,
                            contactPerson, contactNumber, description, photoPath[0], "Missing"
                    );

                    // Try to add using stored procedure first
                    personId = dbManager.addMissingPerson(newPerson, Main.currentUserId);

                    if (personId > 0) {
                        success = true;
                        System.out.println("Successfully added missing person with ID: " + personId);
                    } else {
                        // Fallback: try direct INSERT if stored procedure fails
                        System.out.println("Stored procedure failed, trying direct INSERT...");
                        personId = dbManager.addMissingPersonDirect(newPerson, Main.currentUserId);
                        success = personId > 0;
                    }

                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    System.err.println("Error submitting report: " + errorMsg);
                    e.printStackTrace();
                }

                final String finalErrorMsg = errorMsg;
                final boolean finalSuccess = success;
                final int finalPersonId = personId;

                Platform.runLater(() -> {
                    if (finalSuccess) {
                        notificationManager.showNotification("Success: Missing person report submitted successfully! (ID: " + finalPersonId + ")", NotificationManager.NotificationType.SUCCESS);
                        try {
                            FXMLLoader loader = Main.loadSceneWithController("Dashboard.fxml", "Dashboard");
                            Object controller = loader.getController();
                            if (controller instanceof DashboardController) {
                                ((DashboardController) controller).loadMissingPersonsFromDatabase();
                            }
                        } catch (Exception ex) {
                            System.err.println("[ERROR] Failed to reload dashboard: " + ex.getMessage());
                        }
                        handleCancel();
                    } else {
                        String msg = (finalErrorMsg != null && !finalErrorMsg.isEmpty())
                                ? "Error submitting report: " + finalErrorMsg
                                : "Error: Failed to submit report. Please check your connection and try again.";
                        notificationManager.showNotification(msg, NotificationManager.NotificationType.ERROR);
                    }
                });
            });
        }
    }

    @FXML
    protected void handleCancel() {
        try {
            Main.loadScene("Dashboard.fxml", "Dashboard - Missing Person Alert System");
        } catch (IOException e) {
            // Show user-friendly error message
            if (notificationManager != null) {
                notificationManager.showNotification("Failed to return to dashboard. Please try again.", NotificationManager.NotificationType.ERROR);
            }
        }
    }

    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
