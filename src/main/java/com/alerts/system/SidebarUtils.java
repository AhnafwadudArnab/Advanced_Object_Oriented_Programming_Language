package com.alerts.system;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Utility class to handle sidebar toggle functionality.
 * This reduces code duplication across multiple controllers.
 */
public class SidebarUtils {
    
    /**
     * Toggles the sidebar between expanded and collapsed states.
     * 
     * @param sidebar The VBox containing the sidebar
     * @param sidebarCollapsed Current state of the sidebar
     * @return The new state of the sidebar (true if collapsed, false if expanded)
     */
    public static boolean toggleSidebar(VBox sidebar, boolean sidebarCollapsed) {
        if (sidebarCollapsed) {
            // Expand sidebar
            sidebar.getStyleClass().remove("sidebar-collapsed");
            sidebar.setPrefWidth(220);
            sidebar.setMinWidth(220);
            sidebar.setMaxWidth(220);
            
            for (javafx.scene.Node node : sidebar.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.getStyleClass().remove("nav-button-collapsed");
                    String text = btn.getUserData() != null ? btn.getUserData().toString() : btn.getText();
                    btn.setText(text);
                }
            }
            return false;
        } else {
            // Collapse sidebar
            sidebar.getStyleClass().add("sidebar-collapsed");
            sidebar.setPrefWidth(48);
            sidebar.setMinWidth(48);
            sidebar.setMaxWidth(48);
            
            for (javafx.scene.Node node : sidebar.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    btn.getStyleClass().add("nav-button-collapsed");
                    btn.setUserData(btn.getText());
                    String text = btn.getText();
                    int spaceIdx = text.indexOf(' ');
                    if (spaceIdx > 0) {
                        btn.setText(text.substring(0, spaceIdx));
                    }
                }
            }
            return true;
        }
    }
} 