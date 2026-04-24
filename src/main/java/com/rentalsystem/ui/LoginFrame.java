package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.rentalsystem.config.DatabaseConfig;
import com.rentalsystem.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;

public class LoginFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(LoginFrame.class);
    private JPanel cards;
    private JTextField loginEmail, regName, regEmail, regPhone, regLicense;
    private JPasswordField loginPass, regPass;

    public LoginFrame() {
        setTitle("CarWay | Business Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1100, (int) (screen.width * 0.8));
        int height = Math.min(750, (int) (screen.height * 0.8));
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initUI();
    }

    private void initUI() {
        JPanel split = new JPanel(new GridLayout(1, 2));
        split.setBackground(Color.WHITE);
        setContentPane(split);

        // --- LEFT HERO (CarWay style) ---
        JPanel hero = new JPanel(new GridBagLayout()) {
            private BufferedImage img;
            {
                try (var is = getClass().getResourceAsStream("/images/carway_hero.png")) {
                    if (is != null) img = javax.imageio.ImageIO.read(is);
                } catch (Exception e) { }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Double(20, 20, getWidth()-40, getHeight()-40, 40, 40));
                    double aspect = (double) img.getWidth() / img.getHeight();
                    int w = getWidth(), h = (int)(w / aspect);
                    if (h < getHeight()) { h = getHeight(); w = (int)(h * aspect); }
                    g2.drawImage(img, (getWidth()-w)/2, (getHeight()-h)/2, w, h, null);
                    g2.dispose();
                }
            }
        };
        hero.setBackground(Color.WHITE);
        split.add(hero);

        // --- RIGHT AUTH CARDS ---
        cards = new JPanel(new CardLayout());
        cards.setOpaque(false);
        cards.add(createSignInPanel(), "SIGN_IN");
        cards.add(createSignUpPanel(), "SIGN_UP");
        
        JPanel rightContainer = new JPanel(new GridBagLayout());
        rightContainer.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 70, 0, 70);
        rightContainer.add(cards, gbc);
        
        split.add(rightContainer);
    }

    private JPanel createSignInPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;

        JLabel brand = new JLabel("🚗 CarWay");
        brand.setFont(new Font("Inter", Font.BOLD, 22));
        brand.setForeground(new Color(99, 102, 241));
        gbc.insets = new Insets(-40, 0, 10, 0);
        p.add(brand, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0);
        JLabel title = new JLabel("Sign In to your account");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        p.add(title, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 40, 0);
        JLabel sub = new JLabel("Enter your details to proceed further");
        sub.setForeground(Color.GRAY);
        p.add(sub, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 20, 0);
        loginEmail = createField("debra.holt@example.com", false);
        p.add(createLabelField("Email", loginEmail), gbc);

        gbc.gridy++;
        loginPass = (JPasswordField) createField("••••••••", true);
        p.add(createLabelField("Your password", loginPass), gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 30, 0);
        JButton btnIn = new JButton("Sign In");
        btnIn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 60; background: #6366F1; foreground: #FFF; borderWidth: 0; font: 16");
        btnIn.addActionListener(e -> handleLogin());
        p.add(btnIn, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnToggle = new JButton("<html>Don't have an account? <font color='#6366F1'>Sign Up</font></html>");
        btnToggle.putClientProperty(FlatClientProperties.STYLE, "background: transparent; borderWidth: 0; focusWidth: 0");
        btnToggle.addActionListener(e -> ((CardLayout)cards.getLayout()).show(cards, "SIGN_UP"));
        p.add(btnToggle, gbc);

        return p;
    }

    private JPanel createSignUpPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;

        JLabel title = new JLabel("Join the Elite");
        title.setFont(new Font("Inter", Font.BOLD, 28));
        gbc.insets = new Insets(0, 0, 30, 0);
        p.add(title, gbc);

        regName = createField("Full Name", false);
        regEmail = createField("Email Address", false);
        regPhone = createField("Phone Number", false);
        regLicense = createField("License Number", false);
        regPass = (JPasswordField) createField("Create Password", true);

        JTextField[] fields = {regName, regEmail, regPhone, regLicense, regPass};
        for (JTextField f : fields) {
            gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0);
            p.add(f, gbc);
        }

        gbc.gridy++; gbc.insets = new Insets(20, 0, 15, 0);
        JButton btnUp = new JButton("Create Account");
        btnUp.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 50; background: #6366F1; foreground: #FFF; borderWidth: 0");
        btnUp.addActionListener(e -> handleSignUp());
        p.add(btnUp, gbc);

        gbc.gridy++;
        JButton btnToggle = new JButton("<html>Back to <font color='#6366F1'>Sign In</font></html>");
        btnToggle.putClientProperty(FlatClientProperties.STYLE, "background: transparent; borderWidth: 0; focusWidth: 0");
        btnToggle.addActionListener(e -> ((CardLayout)cards.getLayout()).show(cards, "SIGN_IN"));
        p.add(btnToggle, gbc);

        return p;
    }

    private JTextField createField(String placeholder, boolean isPassword) {
        JTextField f = isPassword ? new JPasswordField() : new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 12; height: 52; padding: 0,15,0,15; background: #FFF; borderWidth: 1; borderColor: #E2E8F0");
        return f;
    }

    private JPanel createLabelField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Inter", Font.BOLD, 12));
        l.setForeground(new Color(100, 116, 139));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void handleLogin() {
        String email = loginEmail.getText();
        String pass = SecurityUtils.hashPassword(new String(loginPass.getPassword()));
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE email = ? AND password = ?")) {
            ps.setString(1, email); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dispose(); new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) { logger.error("Login failed", e); }
    }

    private void handleSignUp() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO customers (name, email, password, phone, license_number) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, regName.getText()); 
            ps.setString(2, regEmail.getText());
            ps.setString(3, SecurityUtils.hashPassword(new String(regPass.getPassword())));
            ps.setString(4, regPhone.getText()); 
            ps.setString(5, regLicense.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            ((CardLayout)cards.getLayout()).show(cards, "SIGN_IN");
        } catch (SQLException e) { logger.error("Sign up failed", e); }
    }
}
