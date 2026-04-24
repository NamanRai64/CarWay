package com.rentalsystem.model;

public abstract class Vehicle {
    private int id;
    private String model;
    private double baseRate;
    private Status status;
    private String imageUrl;

    public enum Status {
        AVAILABLE, RENTED, MAINTENANCE
    }

    public Vehicle(int id, String model, double baseRate, Status status, String imageUrl) {
        this.id = id;
        this.model = model;
        this.baseRate = baseRate;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public abstract double calculateRentalPrice(int days);

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("%s - %s ($%.2f/day)", this.getClass().getSimpleName(), model, baseRate);
    }
}
