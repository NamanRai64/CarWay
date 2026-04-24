package com.rentalsystem.service;

import com.rentalsystem.exception.VehicleNotAvailableException;
import com.rentalsystem.model.Rental;
import com.rentalsystem.model.Vehicle;
import com.rentalsystem.repository.RentalRepository;
import com.rentalsystem.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class RentalService {
    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public RentalService() {
        this.vehicleRepository = new VehicleRepository();
        this.rentalRepository = new RentalRepository();
    }

    public void rentVehicle(Vehicle vehicle, int customerId, int days) throws SQLException, VehicleNotAvailableException {
        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new VehicleNotAvailableException("Vehicle " + vehicle.getModel() + " is not available.");
        }

        double totalCost = vehicle.calculateRentalPrice(days);
        Rental rental = new Rental(
            0,
            vehicle.getId(),
            customerId,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(days),
            totalCost,
            Rental.Status.ACTIVE
        );

        // Transactional update (simplified)
        rentalRepository.save(rental);
        vehicleRepository.updateStatus(vehicle.getId(), Vehicle.Status.RENTED);
        
        logger.info("Rented vehicle ID {} to customer ID {} for {} days.", vehicle.getId(), customerId, days);
    }
}
