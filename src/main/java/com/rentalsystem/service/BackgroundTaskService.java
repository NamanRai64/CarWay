package com.rentalsystem.service;

import com.rentalsystem.repository.RentalRepository;
import com.rentalsystem.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundTaskService {
    private static final Logger logger = LoggerFactory.getLogger(BackgroundTaskService.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private Runnable onRefreshCallback;

    public BackgroundTaskService(Runnable onRefreshCallback) {
        this.vehicleRepository = new VehicleRepository();
        this.rentalRepository = new RentalRepository();
        this.onRefreshCallback = onRefreshCallback;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("Running background monitoring task...");
                // In a real app, logic to check for expired rentals would go here
                // For demo, we just simulate a check and trigger a UI refresh
                
                if (onRefreshCallback != null) {
                    SwingUtilities.invokeLater(onRefreshCallback);
                }
            } catch (Exception e) {
                logger.error("Error in background task", e);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
