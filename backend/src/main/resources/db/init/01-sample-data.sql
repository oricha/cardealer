-- Sample data for development environment
-- This script populates the database with test data for development and testing

-- Create admin user
INSERT INTO users (id, email, password_hash, role, is_active) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin@crashedcarsales.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'ADMIN', true);

-- Create sample dealers
INSERT INTO users (id, email, password_hash, role, is_active) VALUES
('550e8400-e29b-41d4-a716-446655440002', 'dealer1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'DEALER', true),
('550e8400-e29b-41d4-a716-446655440003', 'dealer2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'DEALER', true),
('550e8400-e29b-41d4-a716-446655440004', 'buyer1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'BUYER', true);

INSERT INTO dealers (id, user_id, name, address, phone, website) VALUES
('550e8400-e29b-41d4-a716-446655440102', '550e8400-e29b-41d4-a716-446655440002', 'City Auto Salvage', '123 Main St, Anytown, ST 12345', '+1-555-0101', 'https://cityautosalvage.com'),
('550e8400-e29b-41d4-a716-446655440103', '550e8400-e29b-41d4-a716-446655440003', 'Premium Wreckers', '456 Oak Ave, Somewhere, ST 67890', '+1-555-0102', 'https://premiumwreckers.com');

-- Create sample cars
INSERT INTO cars (id, dealer_id, make, model, year, fuel_type, transmission, vehicle_type, condition, price, mileage, description, is_featured) VALUES
('550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440102', 'Toyota', 'Camry', 2018, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 8500.00, 45000, 'Front-end collision damage, runs well, good for parts or repair', true),
('550e8400-e29b-41d4-a716-446655440202', '550e8400-e29b-41d4-a716-446655440102', 'Honda', 'Civic', 2019, 'GAS', 'MANUAL', 'PASSENGER', 'USED', 12000.00, 30000, 'Minor rear damage, excellent mechanical condition', false),
('550e8400-e29b-41d4-a716-446655440203', '550e8400-e29b-41d4-a716-446655440103', 'Ford', 'F-150', 2017, 'DIESEL', 'AUTOMATIC', 'TRUCK', 'ACCIDENTED', 15000.00, 80000, 'Frame damage from rollover, strong engine and transmission', true),
('550e8400-e29b-41d4-a716-446655440204', '550e8400-e29b-41d4-a716-446655440103', 'BMW', 'X3', 2020, 'HYBRID', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 25000.00, 25000, 'Side impact damage, luxury SUV with good salvage value', false);

-- Create car features for the sample cars
INSERT INTO car_features (car_id, airbags, abs_brakes, air_conditioning, power_steering, central_locking, electric_windows) VALUES
('550e8400-e29b-41d4-a716-446655440201', true, true, true, true, true, true),
('550e8400-e29b-41d4-a716-446655440202', true, true, true, true, false, true),
('550e8400-e29b-41d4-a716-446655440203', true, true, true, true, true, true),
('550e8400-e29b-41d4-a716-446655440204', true, true, true, true, true, true);

-- Create sample car images (placeholder URLs for development)
INSERT INTO car_images (car_id, image_url, alt_text, display_order) VALUES
('550e8400-e29b-41d4-a716-446655440201', 'https://via.placeholder.com/800x600?text=Toyota+Camry+2018', '2018 Toyota Camry - Front View', 1),
('550e8400-e29b-41d4-a716-446655440201', 'https://via.placeholder.com/800x600?text=Toyota+Camry+Damage', '2018 Toyota Camry - Damage View', 2),
('550e8400-e29b-41d4-a716-446655440202', 'https://via.placeholder.com/800x600?text=Honda+Civic+2019', '2019 Honda Civic - Front View', 1),
('550e8400-e29b-41d4-a716-446655440203', 'https://via.placeholder.com/800x600?text=Ford+F150+2017', '2017 Ford F-150 - Front View', 1),
('550e8400-e29b-41d4-a716-446655440204', 'https://via.placeholder.com/800x600?text=BMW+X3+2020', '2020 BMW X3 - Front View', 1);

-- Create sample favorites
INSERT INTO favorites (user_id, car_id) VALUES
('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440201'),
('550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440203');

-- Create sample sale
INSERT INTO sales (car_id, buyer_id, sale_price, status) VALUES
('550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440004', 8000.00, 'COMPLETED');