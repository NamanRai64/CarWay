package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.rentalsystem.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(ProfilePanel.class);
    private JTextField txtName, txtEmail, txtPhone, txtLicense;
    private int currentUserId = 1; // Default for demo, should be set on login

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        initUI();
        loadUserData();
    }

    private void initUI() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;

        JLabel title = new JLabel("Personal Profile");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        gbc.insets = new Insets(0, 0, 10, 0);
        container.add(title, gbc);

        JLabel sub = new JLabel("Manage your account details and fleet preferences.");
        sub.setForeground(Color.GRAY);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 40, 0);
        container.add(sub, gbc);

        // Form Fields
        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        txtName = createStyledField("Full Name");
        container.add(createLabelField("NAME", txtName), gbc);

        gbc.gridy++;
        txtEmail = createStyledField("Email Address");
        container.add(createLabelField("EMAIL", txtEmail), gbc);

        gbc.gridy++;
        txtPhone = createStyledField("Phone Number");
        container.add(createLabelField("PHONE", txtPhone), gbc);

        gbc.gridy++;
        txtLicense = createStyledField("Driver License");
        container.add(createLabelField("LICENSE NUMBER", txtLicense), gbc);

        // Buttons Panel
        gbc.gridy++; gbc.insets = new Insets(20, 0, 0, 0);
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);

        JButton btnSave = new JButton("Save Profile Changes");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 56; background: #6366F1; foreground: #FFF; borderWidth: 0; font: bold 14");
        btnSave.addActionListener(e -> saveUserData());
        btnPanel.add(btnSave);

        JButton btnTheme = new JButton("Toggle Dark Mode 🌙");
        btnTheme.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 56; background: #1E293B; foreground: #FFF; borderWidth: 0; font: bold 14");
        btnTheme.addActionListener(e -> toggleTheme());
        btnPanel.add(btnTheme);

        container.add(btnPanel, gbc);

        add(container, BorderLayout.NORTH);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 60; padding: 0,20,0,20; background: #F8FAFC; borderWidth: 1; borderColor: #E2E8F0");
        return f;
    }

    private JPanel createLabelField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Inter", Font.BOLD, 11));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("phone"));
                txtLicense.setText(rs.getString("license_number"));
            }
        } catch (SQLException e) {
            logger.error("Failed to load user data", e);
        }
    }

    private void saveUserData() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE customers SET name = ?, email = ?, phone = ?, license_number = ? WHERE id = ?")) {
            ps.setString(1, txtName.getText());
            ps.setString(2, txtEmail.getText());
            ps.setString(3, txtPhone.getText());
            ps.setString(4, txtLicense.getText());
            ps.setInt(5, currentUserId);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            logger.error("Failed to save user data", e);
            JOptionPane.showMessageDialog(this, "Error saving profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleTheme() {
        try {
            if (UIManager.getLookAndFeel() instanceof FlatDarkLaf) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            FlatLaf.updateUI();
            
            Container parent = getTopLevelAncestor();
            if (parent instanceof MainFrame) {
                ((MainFrame) parent).updateNavState();
            }
        } catch (Exception ex) {
            logger.error("Failed to toggle theme", ex);
        }
    }
}
