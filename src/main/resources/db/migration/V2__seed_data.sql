-- Seed data for users, dealers, cars and related tables

-- Users
INSERT INTO users (name, email, password, phone, role, enabled, created_at) VALUES
('Juan Pérez', 'juan.perez@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 600 111 222', 'COMPRADOR', true, CURRENT_TIMESTAMP),
('María García', 'maria.garcia@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 600 222 333', 'COMPRADOR', true, CURRENT_TIMESTAMP),
('Carlos López', 'carlos.lopez@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 600 333 444', 'COMPRADOR', true, CURRENT_TIMESTAMP),
('AutoMax Madrid', 'automax@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 910 111 222', 'VENDEDOR', true, CURRENT_TIMESTAMP),
('Premium Cars Barcelona', 'premiumcars@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 930 222 333', 'VENDEDOR', true, CURRENT_TIMESTAMP),
('Coches Valencia', 'cochesvalencia@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '+34 960 333 444', 'VENDEDOR', true, CURRENT_TIMESTAMP);

-- Dealers
INSERT INTO dealers (name, email, phone, address, city, postal_code, description, logo_url, banner_url, user_id, active, created_at) VALUES
('AutoMax Madrid', 'automax@example.com', '+34 910 111 222', 'Calle Gran Vía 123', 'Madrid', '28013', 'Concesionario especializado en vehículos de alta gama con más de 20 años de experiencia en el sector.', '/img/dealer/01.png', '/img/dealer/01.png', 4, true, CURRENT_TIMESTAMP),
('Premium Cars Barcelona', 'premiumcars@example.com', '+34 930 222 333', 'Paseo de Gracia 456', 'Barcelona', '08008', 'Venta de coches premium y deportivos. Garantía y financiación disponible.', '/img/dealer/02.png', '/img/dealer/02.png', 5, true, CURRENT_TIMESTAMP),
('Coches Valencia', 'cochesvalencia@example.com', '+34 960 333 444', 'Avenida del Puerto 789', 'Valencia', '46023', 'Tu concesionario de confianza en Valencia. Amplio stock de vehículos de ocasión.', '/img/dealer/03.png', '/img/dealer/03.png', 6, true, CURRENT_TIMESTAMP);

-- Cars (single-row inserts to avoid parser issues)
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Mercedes-Benz', 'C 300', 2021, 45000.00, 35000, 'DIESEL', 'AUTOMATICO', 'SEDAN', 'OCASION', 'Negro', 4, '2.0L Turbo', 'Mercedes-Benz C 300 en excelente estado. Revisiones al día, único dueño. Interior de cuero, navegación GPS, cámara trasera.', 125, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('BMW', '320d', 2020, 38500.00, 42000, 'DIESEL', 'AUTOMATICO', 'SEDAN', 'OCASION', 'Blanco', 4, '2.0L', 'BMW Serie 3 320d con paquete deportivo M. Mantenimiento oficial BMW. Garantía extendida disponible.', 98, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Audi', 'A4', 2022, 42000.00, 18000, 'GASOLINA', 'AUTOMATICO', 'SEDAN', 'OCASION', 'Gris', 4, '2.0 TFSI', 'Audi A4 como nuevo, pocos kilómetros. Sistema de sonido Bang & Olufsen, asientos deportivos.', 156, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Tesla', 'Model 3', 2023, 52000.00, 8000, 'ELECTRICO', 'AUTOMATICO', 'SEDAN', 'NUEVO', 'Azul', 4, 'Eléctrico', 'Tesla Model 3 Long Range. Autopilot incluido. Carga rápida. Garantía de batería.', 234, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Porsche', '911 Carrera', 2021, 125000.00, 12000, 'GASOLINA', 'AUTOMATICO', 'COUPE', 'OCASION', 'Rojo', 2, '3.0L Turbo', 'Porsche 911 Carrera en estado impecable. Libro de mantenimiento completo. Opciones deportivas.', 312, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Ferrari', '488 GTB', 2019, 245000.00, 8500, 'GASOLINA', 'AUTOMATICO', 'COUPE', 'OCASION', 'Rojo', 2, '3.9L V8', 'Ferrari 488 GTB, una obra maestra italiana. Mantenimiento oficial Ferrari. Certificado de autenticidad.', 445, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Lamborghini', 'Huracán', 2020, 215000.00, 6000, 'GASOLINA', 'AUTOMATICO', 'COUPE', 'OCASION', 'Amarillo', 2, '5.2L V10', 'Lamborghini Huracán EVO. Experiencia de conducción única. Sistema de escape deportivo.', 523, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Volkswagen', 'Golf', 2019, 18500.00, 55000, 'DIESEL', 'MANUAL', 'HATCHBACK', 'OCASION', 'Blanco', 5, '2.0 TDI', 'Volkswagen Golf 2.0 TDI, muy económico y fiable. Ideal para ciudad y carretera.', 87, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Seat', 'León', 2020, 19800.00, 38000, 'GASOLINA', 'MANUAL', 'HATCHBACK', 'OCASION', 'Gris', 5, '1.5 TSI', 'Seat León 1.5 TSI con acabado FR. Deportivo y eficiente. Garantía de 1 año.', 76, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Toyota', 'Corolla', 2021, 22500.00, 28000, 'HIBRIDO', 'AUTOMATICO', 'SEDAN', 'OCASION', 'Plata', 4, '1.8L Hybrid', 'Toyota Corolla Híbrido, bajo consumo. Perfecto estado. Revisiones oficiales Toyota.', 134, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Nissan', 'Qashqai', 2022, 28000.00, 15000, 'GASOLINA', 'AUTOMATICO', 'SUV', 'OCASION', 'Negro', 5, '1.3 DIG-T', 'Nissan Qashqai con tecnología ProPilot. Espacioso y confortable. Como nuevo.', 167, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO cars (make, model, car_year, price, mileage, fuel_type, transmission, body_type, "condition", color, doors, engine, description, views, dealer_id, active, created_at, updated_at) VALUES
('Renault', 'Clio', 2018, 12500.00, 68000, 'GASOLINA', 'MANUAL', 'HATCHBACK', 'OCASION', 'Rojo', 5, '1.2 TCe', 'Renault Clio económico y fiable. Perfecto primer coche. Mantenimiento al día.', 45, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


