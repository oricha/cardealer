# Car Dealer Website - Spring Boot Application

A simple and elegant car dealership website built with Spring Boot, Thymeleaf, and Bootstrap. This application allows users to browse car inventory, view detailed car information, and submit contact inquiries.


## Quick Start

### 1. Clone or Download the Project

```bash
cd /Users/zion/dev/project/springboot
```

### 2. Build the Project

```bash
./gradlew clean build
```

### 3. Start PostgreSQL (Docker)

```bash
docker compose up -d db
```

### 4. Run the Application

```bash
./gradlew bootRun
```

## Development

### Running in Development Mode

```bash
docker compose up -d db
./gradlew bootRun
```

The application will automatically reload when you make changes to the code (thanks to Spring Boot DevTools).

### Building for Production

```bash
./gradlew clean build
java -jar build/libs/car-dealer-0.0.1-SNAPSHOT.jar
```

## Testing

### Run All Tests

```bash
./gradlew test
```


### Database Connection Issues

1) Ensure Docker Postgres is running:
```bash
docker compose ps
```
2) Verify env or defaults match `application.properties`:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
3) Recreate DB volume if needed:
```bash
docker compose down -v && docker compose up -d db
```

 
## Flyway Migrations

Migrations run automatically at application startup. To force a clean setup:
```bash
docker compose down -v
docker compose up -d db
./gradlew bootRun
```
- [ ] Add pagination for car listings
- [ ] Implement favorites/wishlist feature
