package com.rentalsystem.model;

public class Motorcycle extends Vehicle {
    public Motorcycle(int id, String model, double baseRate, Status status, String imageUrl) {
        super(id, model, baseRate, status, imageUrl);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return getBaseRate() * days; // Flat rate
    }
}
