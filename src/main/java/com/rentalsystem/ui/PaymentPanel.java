package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.rentalsystem.model.Rental;
import com.rentalsystem.model.Vehicle;
import com.rentalsystem.repository.RentalRepository;
import com.rentalsystem.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public class PaymentPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(PaymentPanel.class);
    private final RentalRepository rentalRepository = new RentalRepository();
    private final VehicleRepository vehicleRepository = new VehicleRepository();
    
    private Vehicle selectedVehicle;
    private int rentalDays = 1;
    private JLabel lblTotalPay, lblVehicleModel, lblVehicleRate;

    public PaymentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
    }

    public void setSelectedVehicle(Vehicle v, int days) {
        this.selectedVehicle = v;
        this.rentalDays = days;
        if (v != null) {
            lblVehicleModel.setText(v.getModel());
            lblVehicleRate.setText(String.format("$%.2f x %d days", v.getBaseRate(), days));
            double baseTotal = v.getBaseRate() * days;
            lblTotalPay.setText(String.format("$%.2f", baseTotal * 1.2)); // Including 20% VAT
        }
    }

    private void initUI() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        
        // --- LEFT COLUMN: PAYMENT FORM (60% Width) ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6; gbc.weighty = 1;
        container.add(createCheckoutForm(), gbc);
        
        // --- RIGHT COLUMN: ORDER SUMMARY (40% Width) ---
        gbc.gridx = 1; gbc.weightx = 0.4; gbc.insets = new Insets(0, 40, 0, 0);
        container.add(createOrderSidebar(), gbc);
        
        add(container, BorderLayout.CENTER);
    }

    private JPanel createCheckoutForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.gridy = 0; g.weightx = 1;

        // Branding + Timer
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel brand = new JLabel("CarWay Pay");
        brand.setFont(new Font("Inter", Font.BOLD, 24));
        brand.setForeground(new Color(99, 102, 241));
        header.add(brand, BorderLayout.WEST);
        p.add(header, g);

        // Form Fields
        g.gridy++; g.insets = new Insets(40, 0, 0, 0);
        p.add(createModernField("Card Number", "Enter the 16-digit card number", "1234 - 5678 - 9012 - 3446"), g);

        // Row for CVV and Expiry
        JPanel row = new JPanel(new GridLayout(1, 2, 40, 0));
        row.setOpaque(false);
        row.add(createModernField("CVV Number", "Enter 3-digit security code", "446"));
        row.add(createModernField("Expiry Date", "Month / Year", "10 / 24"));
        
        g.gridy++; g.insets = new Insets(30, 0, 0, 0);
        p.add(row, g);

        g.gridy++;
        p.add(createModernField("Password", "Enter your Dynamic password", "********"), g);

        // Pay Button
        g.gridy++; g.insets = new Insets(50, 0, 0, 0);
        JButton btnPay = new JButton("Pay Now");
        btnPay.putClientProperty(FlatClientProperties.STYLE, "arc: 20; height: 70; background: #0066FF; foreground: #FFF; font: 16; borderWidth: 0");
        btnPay.addActionListener(this::processPayment);
        p.add(btnPay, g);

        return p;
    }

    private JPanel createOrderSidebar() {
        JPanel s = new JPanel(new BorderLayout());
        s.putClientProperty(FlatClientProperties.STYLE, "background: #F8FAFC; arc: 40; border: 1,1,1,1,#E2E8F0");
        s.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Virtual Card
        s.add(createVirtualCard(), BorderLayout.NORTH);

        // Receipt Details
        JPanel receipt = new JPanel(new GridBagLayout());
        receipt.setOpaque(false);
        receipt.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        GridBagConstraints rg = new GridBagConstraints();
        rg.fill = GridBagConstraints.HORIZONTAL; rg.gridx = 0; rg.gridy = 0; rg.weightx = 1;

        lblVehicleModel = new JLabel("Select Vehicle");
        lblVehicleRate = new JLabel("$0.00");
        
        receipt.add(createReceiptRow("Company", "CarWay"), rg);
        rg.gridy++; receipt.add(createReceiptRow("Product", lblVehicleModel), rg);
        rg.gridy++; receipt.add(createReceiptRow("VAT (20%)", "Included"), rg);
        
        // Total
        rg.gridy++; rg.insets = new Insets(40, 0, 0, 0);
        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setOpaque(false);
        JLabel tl = new JLabel("You have to Pay");
        tl.setForeground(Color.GRAY);
        lblTotalPay = new JLabel("$0.00");
        lblTotalPay.setFont(new Font("Inter", Font.BOLD, 32));
        totalBox.add(tl, BorderLayout.NORTH);
        totalBox.add(lblTotalPay, BorderLayout.CENTER);
        receipt.add(totalBox, rg);

        s.add(receipt, BorderLayout.CENTER);
        return s;
    }

    private JPanel createVirtualCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setPreferredSize(new Dimension(240, 160));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);

        JLabel chip = new JLabel("💳");
        chip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        card.add(chip, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridLayout(2, 1));
        details.setOpaque(false);
        JLabel name = new JLabel("John Doe");
        name.setFont(new Font("Inter", Font.BOLD, 14));
        JLabel num = new JLabel("•••• 4462");
        num.setFont(new Font("Inter", Font.BOLD, 18));
        details.add(name); details.add(num);
        card.add(details, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createModernField(String title, String subtitle, String placeholder) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        
        JPanel labelBox = new JPanel(new BorderLayout());
        labelBox.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(new Font("Inter", Font.BOLD, 14));
        JLabel sl = new JLabel(subtitle);
        sl.setFont(new Font("Inter", Font.PLAIN, 11));
        sl.setForeground(Color.GRAY);
        labelBox.add(l, BorderLayout.NORTH);
        labelBox.add(sl, BorderLayout.SOUTH);
        
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 15; height: 65; padding: 0,20,0,20; background: #FFF; borderWidth: 1; borderColor: #E2E8F0");
        
        p.add(labelBox, BorderLayout.NORTH);
        p.add(f, BorderLayout.CENTER);
        return p;
    }

    private JPanel createReceiptRow(String label, Object value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel l = new JLabel(label);
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.WEST);
        
        if (value instanceof String) {
            JLabel v = new JLabel((String)value);
            v.setFont(new Font("Inter", Font.BOLD, 13));
            p.add(v, BorderLayout.EAST);
        } else {
            p.add((Component)value, BorderLayout.EAST);
        }
        return p;
    }

    private void processPayment(ActionEvent e) {
        if (selectedVehicle == null) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                double totalCost = selectedVehicle.getBaseRate() * rentalDays;
                rentalRepository.save(new Rental(0, selectedVehicle.getId(), 1, LocalDateTime.now(), LocalDateTime.now().plusDays(rentalDays), totalCost, Rental.Status.ACTIVE));
                vehicleRepository.updateStatus(selectedVehicle.getId(), Vehicle.Status.RENTED);
                return null;
            }
            @Override protected void done() {
                JOptionPane.showMessageDialog(PaymentPanel.this, "Payment Successful!", "CarWay Pay", JOptionPane.INFORMATION_MESSAGE);
                ((MainFrame)getTopLevelAncestor()).showCard("RENTALS");
            }
        }.execute();
    }
}
