-- Additional sample data for development and testing
-- This migration adds more dealers, users, and a comprehensive set of cars

-- Create additional users for more dealers and buyers
INSERT INTO users (id, email, password_hash, role, is_active) VALUES
-- Dealers
('650e8400-e29b-41d4-a716-446655440005', 'dealer3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'DEALER', true),
('650e8400-e29b-41d4-a716-446655440006', 'dealer4@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'DEALER', true),
('650e8400-e29b-41d4-a716-446655440007', 'dealer5@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'DEALER', true),
-- Buyers
('650e8400-e29b-41d4-a716-446655440008', 'buyer2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'BUYER', true),
('650e8400-e29b-41d4-a716-446655440009', 'buyer3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp8lQ2W1wQKqO', 'BUYER', true);

-- Create additional dealers
INSERT INTO dealers (id, user_id, name, address, phone, website) VALUES
('650e8400-e29b-41d4-a716-446655440104', '650e8400-e29b-41d4-a716-446655440005', 'Metro Salvage Yard', '789 Industrial Blvd, Tech City, ST 11223', '+1-555-0201', 'https://metrosalvage.com'),
('650e8400-e29b-41d4-a716-446655440105', '650e8400-e29b-41d4-a716-446655440006', 'Luxury Auto Parts', '321 Luxury Ave, Prestige City, ST 44556', '+1-555-0202', 'https://luxuryautoparts.com'),
('650e8400-e29b-41d4-a716-446655440106', '650e8400-e29b-41d4-a716-446655440007', 'Budget Wreckers', '654 Elm Street, Economy Town, ST 77889', '+1-555-0203', 'https://budgetwreckers.com');

-- Insert comprehensive car data with varied conditions and prices
INSERT INTO cars (id, dealer_id, make, model, year, fuel_type, transmission, vehicle_type, condition, price, mileage, description, is_featured, is_active) VALUES

-- City Auto Salvage cars (dealer 102)
('650e8400-e29b-41d4-a716-446655440205', '550e8400-e29b-41d4-a716-446655440102', 'Chevrolet', 'Malibu', 2017, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 7200.00, 52000, 'Left side impact, good engine, transmission intact', false, true),
('650e8400-e29b-41d4-a716-446655440206', '550e8400-e29b-41d4-a716-446655440102', 'Nissan', 'Altima', 2019, 'GAS', 'CVT', 'PASSENGER', 'USED', 13500.00, 28000, 'Minor fender bender, clean interior, drives perfectly', true, true),
('650e8400-e29b-41d4-a716-446655440207', '550e8400-e29b-41d4-a716-446655440102', 'Volkswagen', 'Jetta', 2016, 'DIESEL', 'MANUAL', 'PASSENGER', 'ACCIDENTED', 5800.00, 89000, 'Rear-end collision, extensive frame damage, parts vehicle', false, true),
('650e8400-e29b-41d4-a716-446655440208', '550e8400-e29b-41d4-a716-446655440102', 'Mazda', 'CX-5', 2020, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 18200.00, 15000, 'Front quarter panel damage, low mileage, excellent condition otherwise', true, true),

-- Premium Wreckers cars (dealer 103)
('650e8400-e29b-41d4-a716-446655440209', '550e8400-e29b-41d4-a716-446655440103', 'Mercedes-Benz', 'C-Class', 2018, 'GAS', 'AUTOMATIC', 'PASSENGER', 'USED', 22500.00, 35000, 'Luxury sedan with minor cosmetic damage, pristine interior', true, true),
('650e8400-e29b-41d4-a716-446655440210', '550e8400-e29b-41d4-a716-446655440103', 'Audi', 'A4', 2019, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 19800.00, 22000, 'Driver side damage, premium sound system, navigation included', false, true),
('650e8400-e29b-41d4-a716-446655440211', '550e8400-e29b-41d4-a716-446655440103', 'Lexus', 'RX', 2017, 'HYBRID', 'AUTOMATIC', 'PASSENGER', 'ACCIDENTED', 16500.00, 67000, 'Rollover damage, hybrid system intact, valuable for parts', false, true),

-- Metro Salvage Yard cars (dealer 104)
('650e8400-e29b-41d4-a716-446655440212', '650e8400-e29b-41d4-a716-446655440104', 'Subaru', 'Outback', 2018, 'GAS', 'CVT', 'PASSENGER', 'USED', 14200.00, 48000, 'All-wheel drive wagon, minor rear damage, excellent mechanical condition', true, true),
('650e8400-e29b-41d4-a716-446655440213', '650e8400-e29b-41d4-a716-446655440104', 'Jeep', 'Grand Cherokee', 2016, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 11800.00, 72000, 'Off-road capable SUV, frame damage from accident, still drivable', false, true),
('650e8400-e29b-41d4-a716-446655440214', '650e8400-e29b-41d4-a716-446655440104', 'Kia', 'Sorento', 2020, 'GAS', 'AUTOMATIC', 'PASSENGER', 'USED', 16800.00, 19000, 'Family SUV with third-row seating, minor parking lot damage', true, true),
('650e8400-e29b-41d4-a716-446655440215', '650e8400-e29b-41d4-a716-446655440104', 'Hyundai', 'Tucson', 2019, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 9200.00, 41000, 'Compact SUV, driver side damage, comprehensive safety features', false, true),

-- Luxury Auto Parts cars (dealer 105)
('650e8400-e29b-41d4-a716-446655440216', '650e8400-e29b-41d4-a716-446655440105', 'Tesla', 'Model 3', 2020, 'ELECTRIC', 'AUTOMATIC', 'PASSENGER', 'ACCIDENTED', 28500.00, 18000, 'Electric vehicle with battery damage, autopilot features, high salvage value', true, true),
('650e8400-e29b-41d4-a716-446655440217', '650e8400-e29b-41d4-a716-446655440105', 'Porsche', 'Cayenne', 2018, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 45200.00, 29000, 'Luxury SUV, rear quarter damage, premium interior, sport package', true, true),
('650e8400-e29b-41d4-a716-446655440218', '650e8400-e29b-41d4-a716-446655440105', 'Land Rover', 'Range Rover', 2017, 'DIESEL', 'AUTOMATIC', 'PASSENGER', 'USED', 38500.00, 55000, 'Luxury off-roader, minor cosmetic issues, exceptional build quality', false, true),

-- Budget Wreckers cars (dealer 106)
('650e8400-e29b-41d4-a716-446655440219', '650e8400-e29b-41d4-a716-446655440106', 'Chrysler', '300', 2015, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DERELICT', 3200.00, 125000, 'High mileage sedan, multiple issues, good for parts only', false, true),
('650e8400-e29b-41d4-a716-446655440220', '650e8400-e29b-41d4-a716-446655440106', 'Dodge', 'Charger', 2016, 'GAS', 'AUTOMATIC', 'PASSENGER', 'DAMAGED', 8900.00, 68000, 'Muscle car with front-end damage, HEMI engine intact', false, true),
('650e8400-e29b-41d4-a716-446655440221', '650e8400-e29b-41d4-a716-446655440106', 'Ram', '1500', 2017, 'GAS', 'AUTOMATIC', 'TRUCK', 'USED', 15600.00, 59000, 'Pickup truck, minor bed damage, strong work truck capabilities', true, true),
('650e8400-e29b-41d4-a716-446655440222', '650e8400-e29b-41d4-a716-446655440106', 'GMC', 'Sierra', 2018, 'DIESEL', 'AUTOMATIC', 'TRUCK', 'ACCIDENTED', 18200.00, 45000, 'Heavy-duty truck, cab damage, Cummins diesel engine', false, true);

-- Add car features for all new cars
INSERT INTO car_features (car_id, airbags, abs_brakes, air_conditioning, power_steering, central_locking, electric_windows) VALUES
-- City Auto Salvage cars
('650e8400-e29b-41d4-a716-446655440205', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440206', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440207', true, false, true, true, false, true),
('650e8400-e29b-41d4-a716-446655440208', true, true, true, true, true, true),

-- Premium Wreckers cars
('650e8400-e29b-41d4-a716-446655440209', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440210', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440211', true, true, true, true, true, true),

-- Metro Salvage Yard cars
('650e8400-e29b-41d4-a716-446655440212', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440213', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440214', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440215', true, true, true, true, true, true),

-- Luxury Auto Parts cars
('650e8400-e29b-41d4-a716-446655440216', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440217', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440218', true, true, true, true, true, true),

-- Budget Wreckers cars
('650e8400-e29b-41d4-a716-446655440219', true, false, false, true, false, false),
('650e8400-e29b-41d4-a716-446655440220', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440221', true, true, true, true, true, true),
('650e8400-e29b-41d4-a716-446655440222', true, true, true, true, true, true);

-- Add car images for all new cars (using placeholder URLs)
INSERT INTO car_images (car_id, image_url, alt_text, display_order) VALUES
-- City Auto Salvage cars
('650e8400-e29b-41d4-a716-446655440205', 'https://via.placeholder.com/800x600?text=Chevrolet+Malibu+2017', '2017 Chevrolet Malibu - Front View', 1),
('650e8400-e29b-41d4-a716-446655440206', 'https://via.placeholder.com/800x600?text=Nissan+Altima+2019', '2019 Nissan Altima - Front View', 1),
('650e8400-e29b-41d4-a716-446655440207', 'https://via.placeholder.com/800x600?text=VW+Jetta+2016', '2016 Volkswagen Jetta - Front View', 1),
('650e8400-e29b-41d4-a716-446655440208', 'https://via.placeholder.com/800x600?text=Mazda+CX-5+2020', '2020 Mazda CX-5 - Front View', 1),

-- Premium Wreckers cars
('650e8400-e29b-41d4-a716-446655440209', 'https://via.placeholder.com/800x600?text=Mercedes+C-Class+2018', '2018 Mercedes-Benz C-Class - Front View', 1),
('650e8400-e29b-41d4-a716-446655440210', 'https://via.placeholder.com/800x600?text=Audi+A4+2019', '2019 Audi A4 - Front View', 1),
('650e8400-e29b-41d4-a716-446655440211', 'https://via.placeholder.com/800x600?text=Lexus+RX+2017', '2017 Lexus RX - Front View', 1),

-- Metro Salvage Yard cars
('650e8400-e29b-41d4-a716-446655440212', 'https://via.placeholder.com/800x600?text=Subaru+Outback+2018', '2018 Subaru Outback - Front View', 1),
('650e8400-e29b-41d4-a716-446655440213', 'https://via.placeholder.com/800x600?text=Jeep+Grand+Cherokee+2016', '2016 Jeep Grand Cherokee - Front View', 1),
('650e8400-e29b-41d4-a716-446655440214', 'https://via.placeholder.com/800x600?text=Kia+Sorento+2020', '2020 Kia Sorento - Front View', 1),
('650e8400-e29b-41d4-a716-446655440215', 'https://via.placeholder.com/800x600?text=Hyundai+Tucson+2019', '2019 Hyundai Tucson - Front View', 1),

-- Luxury Auto Parts cars
('650e8400-e29b-41d4-a716-446655440216', 'https://via.placeholder.com/800x600?text=Tesla+Model+3+2020', '2020 Tesla Model 3 - Front View', 1),
('650e8400-e29b-41d4-a716-446655440217', 'https://via.placeholder.com/800x600?text=Porsche+Cayenne+2018', '2018 Porsche Cayenne - Front View', 1),
('650e8400-e29b-41d4-a716-446655440218', 'https://via.placeholder.com/800x600?text=Land+Rover+Range+Rover+2017', '2017 Land Rover Range Rover - Front View', 1),

-- Budget Wreckers cars
('650e8400-e29b-41d4-a716-446655440219', 'https://via.placeholder.com/800x600?text=Chrysler+300+2015', '2015 Chrysler 300 - Front View', 1),
('650e8400-e29b-41d4-a716-446655440220', 'https://via.placeholder.com/800x600?text=Dodge+Charger+2016', '2016 Dodge Charger - Front View', 1),
('650e8400-e29b-41d4-a716-446655440221', 'https://via.placeholder.com/800x600?text=Ram+1500+2017', '2017 Ram 1500 - Front View', 1),
('650e8400-e29b-41d4-a716-446655440222', 'https://via.placeholder.com/800x600?text=GMC+Sierra+2018', '2018 GMC Sierra - Front View', 1);

-- Add some additional favorites for testing
INSERT INTO favorites (user_id, car_id) VALUES
('650e8400-e29b-41d4-a716-446655440008', '650e8400-e29b-41d4-a716-446655440205'),
('650e8400-e29b-41d4-a716-446655440008', '650e8400-e29b-41d4-a716-446655440209'),
('650e8400-e29b-41d4-a716-446655440009', '650e8400-e29b-41d4-a716-446655440212'),
('650e8400-e29b-41d4-a716-446655440009', '650e8400-e29b-41d4-a716-446655440216');