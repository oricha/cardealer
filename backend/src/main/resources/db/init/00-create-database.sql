-- Create database if it doesn't exist
-- This script should be run as a PostgreSQL superuser

-- Note: This script is for development setup only
-- In production, the database should be created through proper DBA processes

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'crashed_car_sales') THEN
        CREATE DATABASE crashed_car_sales;
    END IF;
END
$$;

-- Connect to the database and create any additional setup if needed
\c crashed_car_sales;

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Set timezone for consistent behavior
SET timezone = 'UTC';