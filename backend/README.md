# Crashed Car Sales Backend

Spring Boot REST API for the Crashed Car Sales application.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database Access)
- **PostgreSQL** (Primary Database)
- **Redis** (Caching)
- **Flyway** (Database Migrations)
- **AWS S3/MinIO** (File Storage)
- **Maven** (Build Tool)

## Project Structure

```
src/
├── main/
│   ├── java/com/crashedcarsales/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA Entities
│   │   ├── exception/      # Exception handling
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic
│   └── resources/
│       ├── db/migration/   # Flyway migrations
│       └── application.yml # Configuration
└── test/                   # Test classes
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+
- Redis 7+

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd backend
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your local configuration
   ```

3. **Start dependencies with Docker**
   ```bash
   cd ../docker
   docker-compose up postgres redis minio -d
   ```

4. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

5. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080/api`

### API Documentation

Once the application is running, you can access:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn test -Dtest=**/*IntegrationTest

# Run all tests with coverage
mvn clean test jacoco:report
```

### Building for Production

```bash
# Build JAR file
mvn clean package

# Build Docker image
docker build -t crashed-car-sales-backend .
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/crashed_car_sales` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `password` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT signing secret | Required |
| `S3_BUCKET` | S3 bucket name | `crashed-car-sales` |
| `S3_ACCESS_KEY` | S3 access key | Required |
| `S3_SECRET_KEY` | S3 secret key | Required |

### Profiles

- **dev**: Development profile with debug logging
- **prod**: Production profile with optimized settings
- **docker**: Docker environment profile

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### Cars
- `GET /api/cars` - List cars with filtering
- `GET /api/cars/{id}` - Get car details
- `POST /api/cars` - Create new car listing
- `PUT /api/cars/{id}` - Update car listing
- `DELETE /api/cars/{id}` - Delete car listing

### Dealers
- `GET /api/dealers/profile` - Get dealer profile
- `PUT /api/dealers/profile` - Update dealer profile
- `GET /api/dealers/dashboard/stats` - Get dashboard statistics

### Images
- `POST /api/images/upload` - Upload car images
- `DELETE /api/images/{id}` - Delete image

## Database Schema

The application uses PostgreSQL with the following main tables:
- `users` - User accounts (dealers, buyers, admins)
- `dealers` - Dealer-specific information
- `cars` - Car listings
- `car_images` - Car photos
- `car_features` - Car feature specifications
- `sales` - Sales transactions
- `favorites` - User favorites

## Security

- JWT-based authentication
- Role-based authorization (ADMIN, DEALER, BUYER)
- Password encryption with BCrypt
- CORS configuration
- Input validation and sanitization

## Monitoring

- Spring Boot Actuator endpoints
- Health checks at `/api/actuator/health`
- Metrics at `/api/actuator/metrics`
- Prometheus metrics at `/api/actuator/prometheus`