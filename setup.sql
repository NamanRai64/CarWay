CREATE DATABASE IF NOT EXISTS vehicle_rental_db;
USE vehicle_rental_db;

CREATE TABLE IF NOT EXISTS vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    base_rate DOUBLE NOT NULL,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    image_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) DEFAULT 'password',
    phone VARCHAR(20),
    license_number VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT,
    customer_id INT,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_date DATETIME,
    total_cost DOUBLE,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Seed default customer
INSERT IGNORE INTO customers (id, name, email, password, phone, license_number) 
VALUES (1, 'Admin User', 'admin@pro.com', 'admin', '+1-555-0199', 'DL-2024-ADMIN');

-- Seed vehicles with DB-linked images
INSERT IGNORE INTO vehicles (type, model, base_rate, status, image_url) VALUES 
('car', 'Tesla Model S', 150.00, 'AVAILABLE', '/images/sedan.png'),
('car', 'BMW M4 Competition', 195.00, 'AVAILABLE', '/images/mercedes.png'),
('truck', 'Rivian R1T', 125.00, 'AVAILABLE', '/images/suv.png'),
('motorcycle', 'Ducati Panigale V4', 90.00, 'AVAILABLE', '/images/mercedes.png');
