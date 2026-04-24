package com.rentalsystem;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.rentalsystem.ui.MainFrame;
import com.rentalsystem.ui.LoginFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Global Error Handling
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Uncaught exception in thread " + t.getName(), e);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, 
                    "An unexpected error occurred: " + e.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
            });
        });

        // Setup FlatLaf
        setupTheme();

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            logger.info("Application started successfully.");
        });
    }

    private static void setupTheme() {
        try {
            // Modern UI Tweaks
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            UIManager.put("TitlePane.unifiedBackground", true);
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            
            FlatLightLaf.setup();
            FlatLaf.updateUI();
        } catch (Exception e) {
            logger.error("Failed to initialize FlatLaf", e);
        }
    }
}
