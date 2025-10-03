# Crashed Car Sales App

A comprehensive web application for selling crashed cars to car dealers and scrapyards.

## Project Structure

```
crashed-car-sales-app/
├── frontend/          # Next.js React application
├── backend/           # Spring Boot Java application
├── docker/            # Docker configuration files
├── docs/              # Documentation
└── README.md          # This file
```

## Technology Stack

- **Frontend**: Next.js 14+ with React 18, TypeScript, Tailwind CSS
- **Backend**: Spring Boot 3.x with Java 17+, Spring Security, Spring Data JPA
- **Database**: PostgreSQL 15+ with Flyway migrations
- **Caching**: Redis
- **Storage**: S3/MinIO with CDN
- **Deployment**: Docker with Dokploy

## Getting Started

### Prerequisites

- Node.js 18+
- Java 17+
- Docker and Docker Compose
- PostgreSQL 15+
- Redis

### Development Setup

1. Clone the repository
2. Set up the backend (see `backend/README.md`)
3. Set up the frontend (see `frontend/README.md`)
4. Start the development environment with Docker Compose

### Environment Configuration

Copy the example environment files and configure them for your setup:

```bash
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env.local
```

## Documentation

- [Requirements](docs/requirements.md)
- [Design](docs/design.md)
- [API Documentation](docs/api.md)
- [Deployment Guide](docs/deployment.md)

## License

MIT License