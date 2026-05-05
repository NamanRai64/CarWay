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
                private BufferedImage scaledImg;
                private int lastW, lastH;

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (imgs[idx] == null) return;
                    
                    int w = getWidth(), h = getHeight();
                    if (w <= 0 || h <= 0) return;
                    
                    if (w != lastW || h != lastH || scaledImg == null) {
                        lastW = w; lastH = h;
                        scaledImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = scaledImg.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        int arc = 40;
                        java.awt.geom.RoundRectangle2D rr = idx == 0
                            ? new java.awt.geom.RoundRectangle2D.Double(0, 0, w + arc, h, arc, arc)
                            : new java.awt.geom.RoundRectangle2D.Double(-arc, 0, w + arc, h, arc, arc);
                        g2.setClip(rr);

                        BufferedImage img = imgs[idx];
                        double scaleX = (double) w / img.getWidth();
                        double scaleY = (double) h / img.getHeight();
                        double scale  = Math.max(scaleX, scaleY); 
                        int drawW = (int)(img.getWidth()  * scale);
                        int drawH = (int)(img.getHeight() * scale);
                        g2.drawImage(img, (w - drawW) / 2, (h - drawH) / 2, drawW, drawH, null);
                        g2.dispose();
                    }
                    g.drawImage(scaledImg, 0, 0, null);
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
        JPanel infoGrid = new JPanel(new GridLayout(1, 4, 30, 0));
        infoGrid.setOpaque(false);

        infoGrid.add(createInfoCard("Premium Fleet", "Luxury Tesla, Porsche, and BMW assets.", "🚗"));
        infoGrid.add(createChartCard("Utilization", 78));
        infoGrid.add(createInfoCard("Secure Pay", "Unified encryption for every transaction.", "💳"));
        infoGrid.add(createInfoCard("Concierge", "24/7 dedicated support for elite members.", "📞"));
        content.add(infoGrid, gbc);

        add(new JScrollPane(content) {{
            putClientProperty(FlatClientProperties.STYLE, "background: @background");
            getViewport().putClientProperty(FlatClientProperties.STYLE, "background: @background");
            setOpaque(true);
            getViewport().setOpaque(true);
            setBorder(null);
            getVerticalScrollBar().setUnitIncrement(16);
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

    private JPanel createChartCard(String title, int percentage) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.putClientProperty(FlatClientProperties.STYLE, "background: #F8FAFC; arc: 30; border: 1,1,1,1,#E2E8F0");
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Inter", Font.BOLD, 18));
        t.setForeground(new Color(15, 23, 42));
        card.add(t, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Background track
                g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(226, 232, 240));
                g2.drawArc(x, y, size, size, 0, 360);

                // Progress
                g2.setColor(new Color(99, 102, 241));
                int angle = (int) (360 * (percentage / 100.0));
                g2.drawArc(x, y, size, size, 90, -angle);

                // Text
                g2.setFont(new Font("Inter", Font.BOLD, 24));
                String text = percentage + "%";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, x + (size - fm.stringWidth(text)) / 2, y + (size + fm.getAscent() - fm.getDescent()) / 2);

                g2.dispose();
            }
        };
        chart.setOpaque(false);
        card.add(chart, BorderLayout.CENTER);

        return card;
    }
}
