package com.rentalsystem.model;

import java.time.LocalDateTime;

public class Rental {
    private int id;
    private int vehicleId;
    private int customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double totalCost;
    private Status status;

    public enum Status {
        ACTIVE, COMPLETED, CANCELLED
    }

    public Rental(int id, int vehicleId, int customerId, LocalDateTime startDate, LocalDateTime endDate, double totalCost, Status status) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getVehicleId() { return vehicleId; }
    public int getCustomerId() { return customerId; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public double getTotalCost() { return totalCost; }
    public Status getStatus() { return status; }
}
