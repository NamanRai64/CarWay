package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.rentalsystem.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private JPanel contentPanel;
    private Vehicle selectedVehicle;
    private int rentalDays = 1;
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String currentCard = "DASHBOARD";

    public MainFrame() {
        setTitle("CarWay Mobility | Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1440, (int) (screen.width * 0.9));
        int height = Math.min(960, (int) (screen.height * 0.9));
        setSize(width, height);
        setLocationRelativeTo(null);
        
        initUI();
        setAppIcon();
    }

    private void setAppIcon() {
        try {
            java.net.URL url = getClass().getResource("/images/carway_logo.png");
            if (url != null) setIconImage(new ImageIcon(url).getImage());
        } catch (Exception e) { logger.error("Icon load failed", e); }
    }

    private void initUI() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(Color.WHITE);
        setContentPane(workspace);

        workspace.add(createUnifiedSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(Color.WHITE);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(new DashboardHome(), "DASHBOARD");
        contentPanel.add(new VehiclePanel(), "VEHICLES");
        contentPanel.add(new PaymentPanel(), "PAYMENTS");
        contentPanel.add(new RentalHistoryPanel(), "RENTALS");
        contentPanel.add(new FleetManagementPanel(), "FLEET_ADMIN");
        contentPanel.add(new ProfilePanel(), "PROFILE");
        
        mainArea.add(contentPanel, BorderLayout.CENTER);
        workspace.add(mainArea, BorderLayout.CENTER);
        
        updateNavState();
    }

    private JPanel createUnifiedSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.putClientProperty(FlatClientProperties.STYLE, "background: @background");
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        gbc.insets = new Insets(40, 15, 60, 15);

        // Logo Container
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoIcon = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/images/carway_logo.png");
            if (imgUrl != null) {
                // Final scale bump (64px) for hero presence
                ImageIcon icon = new ImageIcon(new ImageIcon(imgUrl).getImage()
                    .getScaledInstance(64, 64, Image.SCALE_SMOOTH));
                logoIcon.setIcon(icon);
            }
        } catch (Exception e) { logger.error("Logo load failed", e); }
        
        JLabel brand = new JLabel("CarWay");
        brand.setFont(new Font("Inter", Font.BOLD, 22));
        brand.setForeground(new Color(15, 23, 42));
        
        logoPanel.add(logoIcon);
        logoPanel.add(brand);
        sidebar.add(logoPanel, gbc);

        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.gridy++; sidebar.add(createNavBtn("Dashboard", "🏠", "DASHBOARD"), gbc);
        gbc.gridy++; sidebar.add(createNavBtn("Rental Fleet", "🚗", "VEHICLES"), gbc);
        gbc.gridy++; sidebar.add(createNavBtn("Payments", "💳", "PAYMENTS"), gbc);
        gbc.gridy++; sidebar.add(createNavBtn("History", "📝", "RENTALS"), gbc);
        gbc.gridy++; sidebar.add(createNavBtn("Fleet Admin", "⚙️", "FLEET_ADMIN"), gbc);

        // --- SPACER ---
        gbc.weighty = 1; gbc.gridy++; sidebar.add(new JLabel(""), gbc);
        
        // --- BOTTOM PROFILE SECTION ---
        gbc.weighty = 0; gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        sidebar.add(createProfileSection(), gbc);
        
        return sidebar;
    }

    private JPanel createProfileSection() {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { showCard("PROFILE"); }
        });

        // Avatar
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatar.putClientProperty(FlatClientProperties.STYLE, "background: #E2E8F0; arc: 999; width: 48; height: 48");
        avatar.setOpaque(true);
        p.add(avatar, BorderLayout.WEST);

        // Text
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        JLabel name = new JLabel("Admin User");
        name.setFont(new Font("Inter", Font.BOLD, 14));
        JLabel view = new JLabel("View Account");
        view.setFont(new Font("Inter", Font.PLAIN, 12));
        view.setForeground(Color.GRAY);
        text.add(name); text.add(view);
        p.add(text, BorderLayout.CENTER);

        return p;
    }

    private JButton createNavBtn(String text, String icon, String card) {
        JButton btn = new JButton("  " + icon + "    " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Inter", Font.PLAIN, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showCard(card));
        navButtons.put(card, btn);
        return btn;
    }

    public void showCard(String cardName) {
        if (cardName.equals("PAYMENTS") && selectedVehicle != null) {
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof PaymentPanel) ((PaymentPanel) comp).setSelectedVehicle(selectedVehicle, rentalDays);
            }
        }
        this.currentCard = cardName;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, cardName);
        updateNavState();
    }

    public void updateNavState() {
        boolean isDark = UIManager.getLookAndFeel() instanceof com.formdev.flatlaf.FlatDarkLaf;
        String activeBg = isDark ? "#334155" : "#FFFFFF";
        String activeFg = isDark ? "#F8FAFC" : "#0F172A";
        String inactiveFg = isDark ? "#94A3B8" : "#64748B";

        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(currentCard)) {
                btn.putClientProperty(FlatClientProperties.STYLE, 
                    "background: " + activeBg + "; foreground: " + activeFg + "; arc: 15; height: 56; borderWidth: 0; focusWidth: 0; font: bold 16");
                btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            } else {
                btn.putClientProperty(FlatClientProperties.STYLE, 
                    "background: transparent; foreground: " + inactiveFg + "; arc: 15; height: 56; borderWidth: 0; focusWidth: 0; font: plain 16");
                btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            }
        }
    }

    public void setSelectedVehicle(Vehicle v, int days) { 
        this.selectedVehicle = v; 
        this.rentalDays = days;
    }
}
