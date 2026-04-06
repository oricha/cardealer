# Car Dealer Website - Spring Boot Application

A simple and elegant car dealership website built with Spring Boot, Thymeleaf, and Bootstrap. This application allows users to browse car inventory, view detailed car information, and submit contact inquiries.

## Features

- 🚗 **Car Inventory Display**: Browse all available cars with images, prices, and specifications
- 🔍 **Car Details**: View detailed information about each vehicle
- 📧 **Contact Form**: Submit inquiries with form validation
- 📱 **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- 💾 **PostgreSQL Database**: Persistent database running in Docker
- 🎨 **Modern UI**: Clean and professional design using Bootstrap 5

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Frontend**: Thymeleaf + Bootstrap 5
- **Database**: PostgreSQL
- **Build Tool**: Gradle
- **Java Version**: 21

## Prerequisites

Before running this application, ensure you have:

- Java 21 or higher installed
- Gradle 8.5+ installed (or use the included Gradle Wrapper)
- Docker Desktop
- Your favorite IDE (IntelliJ IDEA, Eclipse, or VS Code)

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


## Sample Data

The application comes with pre-populated sample data including:
- 10+ sample cars with various makes and models
- Different price ranges and specifications
- Placeholder images (can be replaced with actual car images)

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

### Manual Testing Checklist

- [ ] Home page loads correctly
- [ ] Car inventory displays all cars
- [ ] Car details page shows complete information
- [ ] Contact form validates input
- [ ] Contact form submits successfully
- [ ] Navigation works between all pages
- [ ] Responsive design works on mobile
- [ ] H2 console is accessible

## Troubleshooting

### Port Already in Use

If port 8080 is already in use, change it in `application.properties`:
```properties
server.port=8081
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

### Template Not Found

Check that Thymeleaf templates are in `src/main/resources/templates/` directory.

## Future Enhancements

- [ ] Add search and filter functionality
- [ ] Implement admin panel for car management
- [ ] Add user authentication
- [ ] Email notifications for contact form
- [ ] Image upload functionality
- [ ] Appointment scheduling
 
## Flyway Migrations

Migrations run automatically at application startup. To force a clean setup:
```bash
docker compose down -v
docker compose up -d db
./gradlew bootRun
```
- [ ] Add pagination for car listings
- [ ] Implement favorites/wishlist feature

## Contributing

This is a learning/demonstration project. Feel free to fork and modify as needed.

## License

This project is open source and available for educational purposes.

## Support

For questions or issues, please refer to the ARCHITECTURE.md file for detailed technical documentation.

## Author

Created as a Spring Boot demonstration project for a car dealership website.

---

**Happy Coding! 🚀**