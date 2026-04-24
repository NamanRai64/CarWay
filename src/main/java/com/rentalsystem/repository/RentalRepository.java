package com.rentalsystem.repository;

import com.rentalsystem.config.DatabaseConfig;
import com.rentalsystem.model.Rental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository {
    private static final Logger logger = LoggerFactory.getLogger(RentalRepository.class);

    public void save(Rental rental) throws SQLException {
        String sql = "INSERT INTO rentals (vehicle_id, customer_id, start_date, end_date, total_cost, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rental.getVehicleId());
            ps.setInt(2, rental.getCustomerId());
            ps.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            ps.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            ps.setDouble(5, rental.getTotalCost());
            ps.setString(6, rental.getStatus().name());
            ps.executeUpdate();
        }
    }

    public List<Rental> findAll() throws SQLException {
        String sql = "SELECT * FROM rentals";
        List<Rental> rentals = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rentals.add(mapToRental(rs));
            }
        }
        return rentals;
    }

    private Rental mapToRental(ResultSet rs) throws SQLException {
        return new Rental(
            rs.getInt("id"),
            rs.getInt("vehicle_id"),
            rs.getInt("customer_id"),
            rs.getTimestamp("start_date").toLocalDateTime(),
            rs.getTimestamp("end_date").toLocalDateTime(),
            rs.getDouble("total_cost"),
            Rental.Status.valueOf(rs.getString("status"))
        );
    }
}
