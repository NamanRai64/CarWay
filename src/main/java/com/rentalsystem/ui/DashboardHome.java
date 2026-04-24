package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DashboardHome extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(DashboardHome.class);

    public DashboardHome() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25; gbc.weighty = 0.6;

        // --- LEFT: Text Content ---
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        GridBagConstraints lgbc = new GridBagConstraints();
        lgbc.fill = GridBagConstraints.HORIZONTAL; lgbc.gridx = 0; lgbc.gridy = 0; lgbc.weightx = 1;

        JLabel brand = new JLabel("CARWAY");
        brand.setFont(new Font("Inter", Font.BOLD, 14));
        brand.setForeground(new Color(99, 102, 241));
        lgbc.insets = new Insets(0, 0, 20, 0);
        left.add(brand, lgbc);

        lgbc.gridy++;
        JLabel hero = new JLabel("<html>Redefining<br>Modern Motion.</html>");
        hero.setFont(new Font("Inter", Font.BOLD, 64));
        hero.setForeground(new Color(15, 23, 42));
        lgbc.insets = new Insets(0, 0, 30, 0);
        left.add(hero, lgbc);

        lgbc.gridy++;
        JLabel desc = new JLabel("<html>Explore a curated collection of high-performance vehicles<br>" +
                "with a focus on sustainability and precision engineering.</html>");
        desc.setFont(new Font("Inter", Font.PLAIN, 18));
        desc.setForeground(new Color(100, 116, 139));
        lgbc.insets = new Insets(0, 0, 50, 0);
        left.add(desc, lgbc);

        content.add(left, gbc);

        // --- RIGHT: Dual-Hero Image Block ---
        gbc.gridx = 1; gbc.weightx = 0.75; gbc.insets = new Insets(0, 20, 0, 0);
        JPanel dualHero = new JPanel(new GridLayout(1, 2, 0, 0));
        dualHero.setOpaque(false);

        // Load both images
        BufferedImage[] imgs = new BufferedImage[2];
        String[] paths = {"/images/dashboard_red.png", "/images/dash_hero.png"};
        for (int i = 0; i < 2; i++) {
            try (InputStream is = getClass().getResourceAsStream(paths[i])) {
                if (is != null) imgs[i] = ImageIO.read(is);
            } catch (IOException e) {
                logger.error("Failed to load hero image: {}", paths[i], e);
            }
        }

        for (int i = 0; i < 2; i++) {
            final int idx = i;
            JPanel imgPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (imgs[idx] == null) return;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    int arc = 40;
                    java.awt.geom.RoundRectangle2D rr = idx == 0
                        ? new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth() + arc, getHeight(), arc, arc)
                        : new java.awt.geom.RoundRectangle2D.Double(-arc, 0, getWidth() + arc, getHeight(), arc, arc);
                    g2.setClip(rr);

                    // Strict cover: always fill the ENTIRE panel, crop excess from center
                    BufferedImage img = imgs[idx];
                    double scaleX = (double) getWidth()  / img.getWidth();
                    double scaleY = (double) getHeight() / img.getHeight();
                    double scale  = Math.max(scaleX, scaleY); // cover — never letterbox
                    int w = (int)(img.getWidth()  * scale);
                    int h = (int)(img.getHeight() * scale);
                    g2.drawImage(img, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
                    g2.dispose();
                }

                @Override public Dimension getPreferredSize() { return new Dimension(0, 0); }
                @Override public Dimension getMinimumSize()    { return new Dimension(0, 0); }
            };
            imgPanel.setOpaque(false);
            dualHero.add(imgPanel);
        }

        content.add(dualHero, gbc);

        // --- INFO CARDS ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0.4;
        gbc.insets = new Insets(60, 0, 0, 0);
        JPanel infoGrid = new JPanel(new GridLayout(1, 3, 30, 0));
        infoGrid.setOpaque(false);

        infoGrid.add(createInfoCard("Premium Fleet", "Luxury Tesla, Porsche, and BMW assets.", "🚗"));
        infoGrid.add(createInfoCard("Secure Pay", "Unified encryption for every transaction.", "💳"));
        infoGrid.add(createInfoCard("Concierge", "24/7 dedicated support for elite members.", "📞"));
        content.add(infoGrid, gbc);

        add(new JScrollPane(content) {{
            setBackground(Color.WHITE);
            setOpaque(true);
            getViewport().setOpaque(false);
            setBorder(null);
        }}, BorderLayout.CENTER);
    }

    private JPanel createInfoCard(String title, String text, String icon) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.putClientProperty(FlatClientProperties.STYLE, "background: #F8FAFC; arc: 30; border: 1,1,1,1,#E2E8F0");
        card.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));

        JLabel icn = new JLabel(icon);
        icn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        card.add(icn, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 8));
        info.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Inter", Font.BOLD, 18));
        t.setForeground(new Color(15, 23, 42));
        JLabel d = new JLabel("<html>" + text + "</html>");
        d.setFont(new Font("Inter", Font.PLAIN, 12));
        d.setForeground(new Color(100, 116, 139));
        info.add(t); info.add(d);
        
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private void goToVehicles(ActionEvent e) {
        Container parent = getTopLevelAncestor();
        if (parent instanceof MainFrame) {
            ((MainFrame) parent).showCard("VEHICLES");
        }
    }
}
