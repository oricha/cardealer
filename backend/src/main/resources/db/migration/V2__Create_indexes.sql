-- Indexes for performance optimization

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Dealers table indexes
CREATE INDEX idx_dealers_user_id ON dealers(user_id);
CREATE INDEX idx_dealers_name ON dealers(name);

-- Cars table indexes - these are the most critical for search performance
CREATE INDEX idx_cars_make_model ON cars(make, model);
CREATE INDEX idx_cars_price ON cars(price);
CREATE INDEX idx_cars_condition ON cars(condition);
CREATE INDEX idx_cars_dealer_id ON cars(dealer_id);
CREATE INDEX idx_cars_created_at ON cars(created_at);
CREATE INDEX idx_cars_is_active ON cars(is_active);
CREATE INDEX idx_cars_is_featured ON cars(is_featured);

-- Composite indexes for common search patterns
CREATE INDEX idx_cars_search_main ON cars(make, model, year, price);
CREATE INDEX idx_cars_filter_condition ON cars(condition, vehicle_type, fuel_type);
CREATE INDEX idx_cars_price_range ON cars(price, condition, is_active);

-- Car images table indexes
CREATE INDEX idx_car_images_car_id ON car_images(car_id);
CREATE INDEX idx_car_images_display_order ON car_images(display_order);

-- Car features table indexes
CREATE INDEX idx_car_features_car_id ON car_features(car_id);

-- Sales table indexes
CREATE INDEX idx_sales_car_id ON sales(car_id);
CREATE INDEX idx_sales_buyer_id ON sales(buyer_id);
CREATE INDEX idx_sales_sale_date ON sales(sale_date);
CREATE INDEX idx_sales_status ON sales(status);

-- Favorites table indexes
CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorites_car_id ON favorites(car_id);
CREATE INDEX idx_favorites_created_at ON favorites(created_at);