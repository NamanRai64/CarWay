package com.rentalsystem.model;

public class Customer {
    private int id;
    private String name;
    private String email;
    private String licenseNumber;

    public Customer(int id, String name, String email, String licenseNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.licenseNumber = licenseNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getLicenseNumber() { return licenseNumber; }
}
