package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.rentalsystem.model.Vehicle;
import com.rentalsystem.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VehiclePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(VehiclePanel.class);
    private final VehicleRepository repository = new VehicleRepository();
    private JPanel grid;
    private List<Vehicle> masterList = new ArrayList<>();
    
    private JComboBox<String> cbCategory;
    private JComboBox<String> cbPrice;

    public VehiclePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        initHeader();
        initGrid();
        loadVehicles();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("Premium Fleet");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        header.add(title, BorderLayout.WEST);

        // Filter Bar
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filters.setOpaque(false);

        cbCategory = new JComboBox<>(new String[]{"All Categories", "Car", "Truck", "Motorcycle"});
        cbPrice = new JComboBox<>(new String[]{"All Prices", "Under $100", "$100 - $150", "Over $150"});
        
        String comboStyle = "arc: 12; background: #F1F5F9; borderWidth: 0; padding: 5,10,5,10";
        cbCategory.putClientProperty(FlatClientProperties.STYLE, comboStyle);
        cbPrice.putClientProperty(FlatClientProperties.STYLE, comboStyle);

        cbCategory.addActionListener(e -> applyFilters());
        cbPrice.addActionListener(e -> applyFilters());

        JButton btnRefresh = new JButton("↻ Refresh");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #6366F1; foreground: #FFFFFF; borderWidth: 0; padding: 5,10,5,10; font: bold 12");
        btnRefresh.addActionListener(e -> loadVehicles());

        filters.add(new JLabel("Filter by:"));
        filters.add(cbCategory);
        filters.add(cbPrice);
        filters.add(btnRefresh);
        
        header.add(filters, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void initGrid() {
        grid = new JPanel(new GridLayout(0, 3, 30, 30));
        grid.putClientProperty(FlatClientProperties.STYLE, "background: @background");
        grid.setOpaque(true);
        
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.putClientProperty(FlatClientProperties.STYLE, "background: @background");
        scroll.getViewport().putClientProperty(FlatClientProperties.STYLE, "background: @background");
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scroll, BorderLayout.CENTER);
    }

    private void loadVehicles() {
        new SwingWorker<List<Vehicle>, Void>() {
            @Override protected List<Vehicle> doInBackground() throws Exception {
                return repository.findAll();
            }
            @Override protected void done() {
                try {
                    masterList = get();
                    applyFilters();
                } catch (Exception e) { logger.error("Load failed", e); }
            }
        }.execute();
    }

    private void applyFilters() {
        String category = (String) cbCategory.getSelectedItem();
        String priceRange = (String) cbPrice.getSelectedItem();

        List<Vehicle> filtered = masterList.stream()
            .filter(v -> category.equals("All Categories") || v.getClass().getSimpleName().equalsIgnoreCase(category))
            .filter(v -> {
                if (priceRange.equals("All Prices")) return true;
                double rate = v.getBaseRate();
                if (priceRange.equals("Under $100")) return rate < 100;
                if (priceRange.equals("$100 - $150")) return rate >= 100 && rate <= 150;
                if (priceRange.equals("Over $150")) return rate > 150;
                return true;
            })
            .collect(Collectors.toList());

        updateGrid(filtered);
    }

    private void updateGrid(List<Vehicle> vehicles) {
        grid.removeAll();
        for (Vehicle v : vehicles) {
            grid.add(createVehicleCard(v));
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel createVehicleCard(Vehicle v) {
        JPanel card = new JPanel(new BorderLayout());
        card.putClientProperty(FlatClientProperties.STYLE, "background: #FFFFFF; arc: 30; border: 1,1,1,1,#E2E8F0");
        
        JPanel imgBox = new JPanel() {
            private BufferedImage img;
            private BufferedImage scaledImg;
            private int lastW, lastH;
            {
                try (InputStream is = getClass().getResourceAsStream(v.getImageUrl())) {
                    if (is != null) img = ImageIO.read(is);
                } catch (Exception e) { }
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    int w = getWidth(), h = getHeight();
                    if (w <= 0 || h <= 0) return;
                    if (w != lastW || h != lastH || scaledImg == null) {
                        lastW = w; lastH = h;
                        scaledImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = scaledImg.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 30, 30));
                        double aspect = (double) img.getWidth() / img.getHeight();
                        int drawW = w, drawH = (int)(w / aspect);
                        if (drawH < h) { drawH = h; drawW = (int)(drawH * aspect); }
                        g2.drawImage(img, (w-drawW)/2, (h-drawH)/2, drawW, drawH, null);
                        g2.dispose();
                    }
                    g.drawImage(scaledImg, 0, 0, null);
                }
            }
        };
        imgBox.setPreferredSize(new Dimension(0, 200));
        imgBox.setOpaque(false);
        card.add(imgBox, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;

        JLabel model = new JLabel(v.getModel());
        model.setFont(new Font("Inter", Font.BOLD, 18));
        info.add(model, gbc);

        boolean isAvailable = v.getStatus() == Vehicle.Status.AVAILABLE;

        gbc.gridy++; gbc.insets = new Insets(5, 0, 20, 0);
        JLabel meta = new JLabel(v.getClass().getSimpleName() + " • $" + v.getBaseRate() + " / day");
        meta.setForeground(Color.GRAY);
        info.add(meta, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnRent = new JButton(isAvailable ? "Reserve Now" : "Currently Rented");
        btnRent.setEnabled(isAvailable);
        
        String btnStyle = isAvailable ? 
            "arc: 12; height: 45; background: #6366F1; foreground: #FFF; borderWidth: 0" :
            "arc: 12; height: 45; background: #E2E8F0; foreground: #94A3B8; borderWidth: 0";
        btnRent.putClientProperty(FlatClientProperties.STYLE, btnStyle);
        
        btnRent.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter number of days for rental:", "Reservation", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int days = Integer.parseInt(input.trim());
                    if (days <= 0) throw new NumberFormatException();
                    
                    MainFrame mf = (MainFrame) btnRent.getTopLevelAncestor();
                    mf.setSelectedVehicle(v, days);
                    mf.showCard("PAYMENTS");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number of days (greater than 0).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        info.add(btnRent, gbc);
        
        if (!isAvailable) {
            card.setToolTipText("This vehicle is currently undergoing a live rental session.");
        }

        card.add(info, BorderLayout.CENTER);
        return card;
    }
}
