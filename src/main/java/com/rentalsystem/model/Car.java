package com.rentalsystem.model;

public class Car extends Vehicle {
    private static final double LUXURY_MULTIPLIER = 1.2;

    public Car(int id, String model, double baseRate, Status status, String imageUrl) {
        super(id, model, baseRate, status, imageUrl);
    }

    @Override
    public double calculateRentalPrice(int days) {
        return getBaseRate() * days * LUXURY_MULTIPLIER;
    }
}
