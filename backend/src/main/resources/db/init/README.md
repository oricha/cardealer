# Database Initialization Scripts

This directory contains scripts for setting up and initializing the database for development and testing purposes.

## Files

- **00-create-database.sql**: Creates the database if it doesn't exist and sets up basic configuration
- **01-sample-data.sql**: Populates the database with sample data for development and testing

## Usage

### Prerequisites

1. Ensure PostgreSQL is installed and running
2. Create a superuser role if needed (for database creation)

### Setup Steps

1. **Create the database:**
   ```bash
   psql -U postgres -f backend/src/main/resources/db/init/00-create-database.sql
   ```

2. **Run Flyway migrations:**
   ```bash
   ./mvnw flyway:migrate
   ```

3. **Load sample data (optional):**
   ```bash
   psql -U postgres -d crashed_car_sales -f backend/src/main/resources/db/init/01-sample-data.sql
   ```

### Sample Data

The sample data includes:

- **Admin User:**
  - Email: admin@crashedcarsales.com
  - Password: password (BCrypt hashed)

- **Dealers:**
  - dealer1@example.com / password (City Auto Salvage)
  - dealer2@example.com / password (Premium Wreckers)

- **Buyer:**
  - buyer1@example.com / password

- **Sample Cars:**
  - 2018 Toyota Camry (Damaged) - $8,500
  - 2019 Honda Civic (Used) - $12,000
  - 2017 Ford F-150 (Accidented) - $15,000
  - 2020 BMW X3 (Damaged) - $25,000

### Environment Variables

Make sure to set these environment variables for the application:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/crashed_car_sales
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=password
export SPRING_PROFILES_ACTIVE=dev
```

### Docker Development Setup

If using Docker Compose, the database initialization is handled automatically through the `docker-compose.yml` file and the initialization scripts in the Docker setup.