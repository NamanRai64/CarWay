package com.rentalsystem.repository;

import com.rentalsystem.config.DatabaseConfig;
import com.rentalsystem.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private static final Logger logger = LoggerFactory.getLogger(VehicleRepository.class);

    public List<Vehicle> findAll() throws SQLException {
        String sql = "SELECT * FROM vehicles";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapToVehicle(rs));
            }
        }
        return vehicles;
    }

    public void updateStatus(int vehicleId, Vehicle.Status status) throws SQLException {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, vehicleId);
            ps.executeUpdate();
        }
    }

    public void insert(String type, String model, double rate, Vehicle.Status status, String imageUrl) throws SQLException {
        String sql = "INSERT INTO vehicles (type, model, base_rate, status, image_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, model);
            ps.setDouble(3, rate);
            ps.setString(4, status.name());
            ps.setString(5, imageUrl);
            ps.executeUpdate();
        }
    }

    public void update(int vehicleId, String type, String model, double rate, Vehicle.Status status, String imageUrl) throws SQLException {
        String sql = "UPDATE vehicles SET type = ?, model = ?, base_rate = ?, status = ?, image_url = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, model);
            ps.setDouble(3, rate);
            ps.setString(4, status.name());
            ps.setString(5, imageUrl);
            ps.setInt(6, vehicleId);
            ps.executeUpdate();
        }
    }

    public void delete(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    private Vehicle mapToVehicle(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        String model = rs.getString("model");
        double rate = rs.getDouble("base_rate");
        Vehicle.Status status = Vehicle.Status.valueOf(rs.getString("status"));
        String imageUrl = rs.getString("image_url");

        return switch (type.toLowerCase()) {
            case "car" -> new Car(id, model, rate, status, imageUrl);
            case "truck" -> new Truck(id, model, rate, status, imageUrl);
            case "motorcycle" -> new Motorcycle(id, model, rate, status, imageUrl);
            default -> throw new SQLException("Unknown vehicle type: " + type);
        };
    }
}
