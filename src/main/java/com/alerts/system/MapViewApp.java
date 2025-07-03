package com.alerts.system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapViewApp extends Application {

    private ObservableList<MissingPerson> missingPersons;
    private WebView webView;
    private WebEngine webEngine;
    private Stage primaryStage;

    public MapViewApp() {
        this.missingPersons = null;
    }

    public void setMissingPersons(ObservableList<MissingPerson> missingPersons) {
        this.missingPersons = missingPersons;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Check if WebView is available
        if (isWebViewAvailable()) {
            try {
                if (tryCreateWebViewMap(primaryStage)) {
                    return;
                }
            } catch (Exception e) {
                System.err.println("WebView map failed: " + e.getMessage());
                e.printStackTrace();
                // e.printStackTrace(); // Removed to avoid printing stack trace
            }
        }
        // Fallback to text-based view
        createTextBasedMap(primaryStage);
    }

    private boolean isWebViewAvailable() {
        try {
            // Try to create a WebView to test availability
            WebView testView = new WebView();
            testView.getEngine();
            return true;
        } catch (Exception e) {
            System.err.println("WebView not available: " + e.getMessage());
            return false;
        }
    }

    private boolean tryCreateWebViewMap(Stage primaryStage) {
        try {
            webView = new WebView();
            webEngine = webView.getEngine();

            // Create HTML file in a temporary location
            String filePath = createMapHtml();
            if (filePath != null) {
                File file = new File(filePath);
                webEngine.load(file.toURI().toString());
                
                // Add error handling for WebEngine
                webEngine.setOnError(event -> {
                    Platform.runLater(() -> {
                        System.err.println("WebEngine error: " + event.getMessage());
                        showWebViewError();
                    });
                });
                
                // Add load state change listener
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == javafx.concurrent.Worker.State.FAILED) {
                        Platform.runLater(() -> {
                            System.err.println("WebEngine load failed");
                            showWebViewError();
                        });
                    }
                });
                
                Scene scene = new Scene(webView, 1000, 700);
                primaryStage.setTitle("Missing Persons Map View");
                primaryStage.setScene(scene);
                primaryStage.setMinWidth(800);
                primaryStage.setMinHeight(600);
                primaryStage.show();
                return true;
            }
        } catch (Exception e) {
            System.err.println("WebView creation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void showWebViewError() {
        if (primaryStage != null) {
            createTextBasedMap(primaryStage);
        }
    }

    private void createTextBasedMap(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");
        root.setAlignment(Pos.TOP_CENTER);
        
        // Header
        Label titleLabel = new Label("Missing Persons Location View");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Info section
        VBox infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: #e8f4fd; -fx-padding: 10; -fx-border-color: #3498db; -fx-border-width: 1; -fx-border-radius: 5;");
        infoBox.setAlignment(Pos.CENTER);
        
        Label infoLabel = new Label("Interactive map view is not available in your current environment.");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2980b9; -fx-font-weight: bold;");
        
        Label suggestionLabel = new Label("This is a text-based location view. For full map functionality, use Java 17+ with JavaFX 21.");
        suggestionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        suggestionLabel.setWrapText(true);
        
        infoBox.getChildren().addAll(infoLabel, suggestionLabel);
        
        // Map content
        TextArea mapText = new TextArea();
        mapText.setEditable(false);
        mapText.setPrefRowCount(25);
        mapText.setPrefColumnCount(80);
        mapText.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-background-color: white; -fx-border-color: #bdc3c7;");
        
        // Generate text-based map
        String mapContent = generateTextBasedMap();
        mapText.setText(mapContent);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button refreshButton = new Button("Refresh Data");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        refreshButton.setOnAction(e -> {
            mapText.setText(generateTextBasedMap());
        });
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(refreshButton, closeButton);
        
        root.getChildren().addAll(titleLabel, infoBox, mapText, buttonBox);
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        Scene scene = new Scene(scrollPane, 900, 700);
        primaryStage.setTitle("Missing Persons Location View");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    private String generateTextBasedMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("MISSING PERSONS LOCATION VIEW\n");
        sb.append("=============================\n\n");
        
        if (missingPersons == null || missingPersons.isEmpty()) {
            sb.append("No missing persons data available.\n");
            sb.append("Add some missing persons through the dashboard to see them here.\n\n");
            return sb.toString();
        }
        
        sb.append("Legend:\n");
        sb.append("🔴 Missing\n");
        sb.append("🟢 Found\n");
        sb.append("⚫ Deceased\n\n");
        
        sb.append("Locations:\n");
        sb.append("----------\n");
        
        int missingCount = 0, foundCount = 0, deceasedCount = 0;
        
        for (MissingPerson person : missingPersons) {
            String status = person.getStatus();
            String icon = "🔴";
            if ("Found".equalsIgnoreCase(status)) {
                icon = "🟢";
                foundCount++;
            } else if ("Deceased".equalsIgnoreCase(status)) {
                icon = "⚫";
                deceasedCount++;
            } else {
                missingCount++;
            }
            
            sb.append(String.format("%s %s (%d years old)\n", 
                icon, 
                person.getName() != null ? person.getName() : "Unknown",
                person.getAge()));
            
            String location = person.getLastSeenLocation();
            if (location != null && !location.trim().isEmpty()) {
                sb.append(String.format("   📍 Location: %s\n", location));
            } else {
                sb.append("   📍 Location: Unknown\n");
            }
            
            if (person.getLastSeenDate() != null) {
                sb.append(String.format("   📅 Last seen: %s\n", person.getLastSeenDate()));
            }
            
            if (person.getContactPerson() != null && !person.getContactPerson().trim().isEmpty()) {
                sb.append(String.format("   👤 Contact: %s\n", person.getContactPerson()));
            }
            
            sb.append("\n");
        }
        
        // Add summary
        sb.append("Summary:\n");
        sb.append("--------\n");
        sb.append(String.format("🔴 Missing: %d\n", missingCount));
        sb.append(String.format("🟢 Found: %d\n", foundCount));
        sb.append(String.format("⚫ Deceased: %d\n", deceasedCount));
        sb.append(String.format("📊 Total: %d\n", missingPersons.size()));
        
        return sb.toString();
    }

    private String createMapHtml() {
        try {
            // Create a temporary file in the system temp directory
            Path tempDir = Files.createTempDirectory("missingpersonsmap");
            String filePath = tempDir.resolve("map.html").toString();
            
            // Generate markers for missing persons
            String markersHtml = generateMissingPersonMarkers();
            
            String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Missing Persons Map</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        html, body, #map {\n" +
                "            height: 100%;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .info {\n" +
                "            padding: 6px 8px;\n" +
                "            font: 14px/16px Arial, Helvetica, sans-serif;\n" +
                "            background: white;\n" +
                "            background: rgba(255,255,255,0.9);\n" +
                "            box-shadow: 0 0 15px rgba(0,0,0,0.2);\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .info h4 {\n" +
                "            margin: 0 0 5px;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "        .legend {\n" +
                "            padding: 6px 8px;\n" +
                "            font: 12px Arial, Helvetica, sans-serif;\n" +
                "            background: white;\n" +
                "            background: rgba(255,255,255,0.9);\n" +
                "            box-shadow: 0 0 15px rgba(0,0,0,0.2);\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .legend i {\n" +
                "            width: 18px;\n" +
                "            height: 18px;\n" +
                "            float: left;\n" +
                "            margin-right: 8px;\n" +
                "            border-radius: 50%;\n" +
                "            border: 2px solid white;\n" +
                "            box-shadow: 0 1px 4px rgba(0,0,0,0.3);\n" +
                "            display: inline-block;\n" +
                "            vertical-align: middle;\n" +
                "        }\n" +
                "        .legend div {\n" +
                "            margin: 4px 0;\n" +
                "            line-height: 20px;\n" +
                "            clear: both;\n" +
                "        }\n" +
                "        .legend div:after {\n" +
                "            content: '';\n" +
                "            display: table;\n" +
                "            clear: both;\n" +
                "        }\n" +
                "        .zoom-controls {\n" +
                "            position: absolute;\n" +
                "            top: 10px;\n" +
                "            right: 10px;\n" +
                "            z-index: 1000;\n" +
                "            background: white;\n" +
                "            padding: 5px;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 0 10px rgba(0,0,0,0.2);\n" +
                "        }\n" +
                "        .zoom-controls button {\n" +
                "            margin: 2px;\n" +
                "            padding: 5px 10px;\n" +
                "            border: none;\n" +
                "            background: #3498db;\n" +
                "            color: white;\n" +
                "            border-radius: 3px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        .zoom-controls button:hover {\n" +
                "            background: #2980b9;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/leaflet/dist/leaflet.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"map\"></div>\n" +
                "<div class=\"zoom-controls\">\n" +
                "    <button onclick=\"fitBounds()\">Fit All Markers</button>\n" +
                "    <button onclick=\"resetZoom()\">Reset Zoom</button>\n" +
                "</div>\n" +
                "<script>\n" +
                "    // Initialize the map centered on Dhaka with better zoom level\n" +
                "    var map = L.map('map').setView([23.8103, 90.4125], 8);\n" +
                "    \n" +
                "    // Add OpenStreetMap tiles\n" +
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "        attribution: '© <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors',\n" +
                "        maxZoom: 18,\n" +
                "        minZoom: 3\n" +
                "    }).addTo(map);\n" +
                "    \n" +
                "    // Create marker group for better management\n" +
                "    var markerGroup = L.layerGroup().addTo(map);\n" +
                "    \n" +
                "    // Dynamic icon creation function that scales with zoom\n" +
                "    function createScaledIcon(color, status) {\n" +
                "        return L.divIcon({\n" +
                "            html: '<div style=\"background-color: ' + color + '; width: 24px; height: 24px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; color: white;\">' + status.charAt(0).toUpperCase() + '</div>',\n" +
                "            className: 'scaled-marker',\n" +
                "            iconSize: [24, 24],\n" +
                "            iconAnchor: [12, 12],\n" +
                "            popupAnchor: [0, -12]\n" +
                "        });\n" +
                "    }\n" +
                "    \n" +
                "    // Icon definitions with better scaling\n" +
                "    var missingIcon = createScaledIcon('#ff4444', 'Missing');\n" +
                "    var foundIcon = createScaledIcon('#44ff44', 'Found');\n" +
                "    var deceasedIcon = createScaledIcon('#666666', 'Deceased');\n" +
                "    \n" +
                "    // Add missing persons markers\n" +
                markersHtml +
                "    \n" +
                "    // Function to fit all markers in view\n" +
                "    function fitBounds() {\n" +
                "        if (markerGroup.getLayers().length > 0) {\n" +
                "            var group = new L.featureGroup(markerGroup.getLayers());\n" +
                "            map.fitBounds(group.getBounds().pad(0.1));\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    // Function to reset zoom\n" +
                "    function resetZoom() {\n" +
                "        map.setView([23.8103, 90.4125], 8);\n" +
                "    }\n" +
                "    \n" +
                "    // Auto-fit bounds if there are markers\n" +
                "    setTimeout(function() {\n" +
                "        if (markerGroup.getLayers().length > 1) {\n" +
                "            fitBounds();\n" +
                "        }\n" +
                "    }, 500);\n" +
                "    \n" +
                "    // Add zoom level indicator\n" +
                "    map.on('zoomend', function() {\n" +
                "        var zoom = map.getZoom();\n" +
                "        console.log('Current zoom level: ' + zoom);\n" +
                "    });\n" +
                "    \n" +
                "    // Add info control\n" +
                "    var info = L.control();\n" +
                "    info.onAdd = function (map) {\n" +
                "        this._div = L.DomUtil.create('div', 'info');\n" +
                "        this.update();\n" +
                "        return this._div;\n" +
                "    };\n" +
                "    info.update = function (props) {\n" +
                "        this._div.innerHTML = '<h4>Missing Persons Map</h4>' + \n" +
                "            'Click on markers to see details<br>' +\n" +
                "            'Use zoom controls to adjust view';\n" +
                "    };\n" +
                "    info.addTo(map);\n" +
                "    \n" +
                "    // Add legend\n" +
                "    var legend = L.control({position: 'bottomright'});\n" +
                "    legend.onAdd = function (map) {\n" +
                "        var div = L.DomUtil.create('div', 'legend');\n" +
                "        div.innerHTML = '<h4>Legend</h4>' +\n" +
                "            '<div><i style=\"background: #ff4444\"></i>Missing</div>' +\n" +
                "            '<div><i style=\"background: #44ff44\"></i>Found</div>' +\n" +
                "            '<div><i style=\"background: #666666\"></i>Deceased</div>';\n" +
                "        return div;\n" +
                "    };\n" +
                "    legend.addTo(map);\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
                
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(html);
                return filePath;
            } catch (IOException e) {
                System.err.println("Error creating HTML file: " + e.getMessage());
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error creating temporary directory: " + e.getMessage());
            return null;
        }
    }

    private String generateMissingPersonMarkers() {
        if (missingPersons == null || missingPersons.isEmpty()) {
            // Return default Dhaka location if no missing persons data
            return "    // Add default Dhaka marker\n" +
                   "    var dhakaMarker = L.marker([23.8103, 90.4125]).addTo(map);\n" +
                   "    dhakaMarker.bindPopup('<b>Dhaka</b><br>Capital of Bangladesh<br>No missing persons data available').openPopup();\n";
        }

        StringBuilder markers = new StringBuilder();
        markers.append("    // Add missing persons markers\n");
        
        for (MissingPerson person : missingPersons) {
            // Parse location or use default coordinates
            double lat = 23.8103; // Default Dhaka coordinates
            double lng = 90.4125;
            
            // Try to parse location if it contains coordinates
            String location = person.getLastSeenLocation();
            if (location != null && !location.trim().isEmpty()) {
                // Simple coordinate parsing - you might want to implement geocoding here
                if (location.contains(",")) {
                    try {
                        String[] coords = location.split(",");
                        if (coords.length >= 2) {
                            lat = Double.parseDouble(coords[0].trim());
                            lng = Double.parseDouble(coords[1].trim());
                        }
                    } catch (NumberFormatException e) {
                        // Use default coordinates if parsing fails
                    }
                }
            }
            
            // Choose icon based on status
            String iconVar = "missingIcon";
            if ("Found".equalsIgnoreCase(person.getStatus())) {
                iconVar = "foundIcon";
            } else if ("Deceased".equalsIgnoreCase(person.getStatus())) {
                iconVar = "deceasedIcon";
            }
            
            // Create popup content
            String popupContent = String.format(
                "'<b>%s</b><br>Status: %s<br>Last Seen: %s<br>Location: %s'",
                person.getName() != null ? person.getName().replace("'", "\\'") : "Unknown",
                person.getStatus() != null ? person.getStatus() : "Unknown",
                person.getLastSeenDate() != null ? person.getLastSeenDate().toString() : "Unknown",
                person.getLastSeenLocation() != null ? person.getLastSeenLocation().replace("'", "\\'") : "Unknown"
            );
            
            markers.append(String.format(
                "    L.marker([%.6f, %.6f], {icon: %s}).addTo(map).bindPopup(%s);\n",
                lat, lng, iconVar, popupContent
            ));
        }
        
        return markers.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
