package com.rentalsystem.model;

public class Truck extends Vehicle {
    private static final double CAPACITY_MULTIPLIER = 1.5;

    public Truck(int id, String model, double baseRate, Status status, String imageUrl) {
        super(id, model, baseRate, status, imageUrl);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return getBaseRate() * days * CAPACITY_MULTIPLIER;
    }
}
