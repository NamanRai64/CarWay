package com.rentalsystem.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.rentalsystem.model.Rental;
import com.rentalsystem.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RentalHistoryPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(RentalHistoryPanel.class);
    private final RentalRepository repository = new RentalRepository();
    private JTable table;
    private DefaultTableModel tableModel;

    public RentalHistoryPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        initHeader();
        initTable();
        loadData();
    }

    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Booking History");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh Ledger");
        refreshBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #1F2937");
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Vehicle ID", "Cust ID", "Start Date", "End Date", "Cost", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.putClientProperty(FlatClientProperties.STYLE, 
            "showHorizontalLines: true; " +
            "rowHeight: 50; " +
            "selectionBackground: rgba(99, 102, 241, 0.15); " +
            "selectionForeground: $Text.foreground");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,0,0");
        scrollPane.getViewport().putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background");
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        new SwingWorker<List<Rental>, Void>() {
            @Override
            protected List<Rental> doInBackground() throws Exception {
                return repository.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Rental> rentals = get();
                    tableModel.setRowCount(0);
                    for (Rental r : rentals) {
                        tableModel.addRow(new Object[]{
                            r.getId(),
                            r.getVehicleId(),
                            r.getCustomerId(),
                            r.getStartDate(),
                            r.getEndDate(),
                            String.format("$%.2f", r.getTotalCost()),
                            r.getStatus()
                        });
                    }
                } catch (Exception e) {
                    logger.error("Failed to load rental data", e);
                }
            }
        }.execute();
    }
}
