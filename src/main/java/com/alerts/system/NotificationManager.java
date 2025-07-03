package com.alerts.system;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NotificationManager {

    private final VBox notificationContainer; // The VBox where notifications will be added

    public NotificationManager(VBox container) {
        this.notificationContainer = container;
    }

    // Enum to define different types of notifications for styling
    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }

    /**
     * Displays a temporary notification message in the designated container.
     * All styling is applied directly in Java.
     * @param message The text content of the notification.
     * @param type The type of notification (INFO, SUCCESS, WARNING, ERROR) for styling.
     */
    public void showNotification(String message, NotificationType type) {
        // Create the individual notification box (HBox for message + close button)
        HBox notificationBox = new HBox(10); // Spacing of 10px between elements
        notificationBox.setAlignment(Pos.CENTER_LEFT);
        notificationBox.setPadding(new Insets(10, 15, 10, 15));
        notificationBox.setPrefHeight(40);
        notificationBox.setStyle("-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2);");

        // Apply type-specific styles directly
        String boxBackgroundColor = "#ffffff";
        String boxBorderColor = "#cccccc";
        String labelTextColor = "#333333";
        String icon = "";
        switch (type) {
            case INFO:
                boxBackgroundColor = "#e0f7fa";
                boxBorderColor = "#4dd0e1";
                labelTextColor = "#00bcd4";
                icon = "\u2139"; // info
                break;
            case SUCCESS:
                boxBackgroundColor = "#e8f5e9";
                boxBorderColor = "#81c784";
                labelTextColor = "#4CAF50";
                icon = "\u2714"; // check
                break;
            case WARNING:
                boxBackgroundColor = "#fffde7";
                boxBorderColor = "#ffd54f";
                labelTextColor = "#FFC107";
                icon = "\u26A0"; // warning
                break;
            case ERROR:
                boxBackgroundColor = "#ffebee";
                boxBorderColor = "#ef5350";
                labelTextColor = "#F44336";
                icon = "\u2716"; // error
                break;
        }
        notificationBox.setStyle(notificationBox.getStyle() +
                String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1px;",
                        boxBackgroundColor, boxBorderColor));

        // Icon label
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 8px 0 0; -fx-text-fill: " + labelTextColor + ";");

        // Message label
        Label messageLabel = new Label(message);
        messageLabel.setStyle(String.format("-fx-font-size: 14px; -fx-text-fill: %s; -fx-wrap-text: true;", labelTextColor));
        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        // Close button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #888888; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 5px; -fx-cursor: hand;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(closeButton.getStyle() + "-fx-text-fill: #000000;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(closeButton.getStyle().replace("-fx-text-fill: #000000;", "-fx-text-fill: #888888;")));
        closeButton.setOnAction(e -> notificationContainer.getChildren().remove(notificationBox));

        notificationBox.getChildren().addAll(iconLabel, messageLabel, closeButton);

        // --- Snackbar/Toast effect ---
        // Remove any existing notifications (for single snackbar style)
        notificationContainer.getChildren().clear();
        notificationContainer.setAlignment(Pos.BOTTOM_CENTER);
        notificationContainer.setMouseTransparent(true);
        notificationBox.setMouseTransparent(false);
        notificationContainer.getChildren().add(notificationBox);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(300), notificationBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Auto-fade out after 4 seconds
        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.millis(500), notificationBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(javafx.util.Duration.seconds(4));
        fadeOut.setOnFinished(e -> notificationContainer.getChildren().remove(notificationBox));
        fadeOut.play();
    }
}