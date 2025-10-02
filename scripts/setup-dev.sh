#!/bin/bash

# Crashed Car Sales App - Development Setup Script

set -e

echo "🚗 Setting up Crashed Car Sales App development environment..."

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18+ first."
    exit 1
fi

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+ first."
    exit 1
fi

echo "✅ All prerequisites are installed"

# Create environment files
echo "📝 Setting up environment files..."

if [ ! -f "backend/.env" ]; then
    cp backend/.env.example backend/.env
    echo "✅ Created backend/.env from example"
else
    echo "⚠️  backend/.env already exists, skipping..."
fi

if [ ! -f "frontend/.env.local" ]; then
    cp frontend/.env.example frontend/.env.local
    echo "✅ Created frontend/.env.local from example"
else
    echo "⚠️  frontend/.env.local already exists, skipping..."
fi

# Start infrastructure services
echo "🐳 Starting infrastructure services with Docker..."
cd docker
docker-compose up -d postgres redis minio
echo "✅ Infrastructure services started"

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 10

# Setup MinIO bucket
echo "🪣 Setting up MinIO bucket..."
docker-compose exec -T minio mc alias set local http://localhost:9000 minioadmin minioadmin123
docker-compose exec -T minio mc mb local/crashed-car-sales --ignore-existing
echo "✅ MinIO bucket created"

cd ..

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
cd frontend
npm install
echo "✅ Frontend dependencies installed"

cd ..

# Install backend dependencies and run migrations
echo "📦 Installing backend dependencies..."
cd backend
mvn dependency:resolve
echo "✅ Backend dependencies installed"

# Run database migrations
echo "🗄️  Running database migrations..."
mvn flyway:migrate
echo "✅ Database migrations completed"

cd ..

echo ""
echo "🎉 Development environment setup complete!"
echo ""
echo "To start the application:"
echo "  1. Start the backend:  cd backend && mvn spring-boot:run"
echo "  2. Start the frontend: cd frontend && npm run dev"
echo ""
echo "Services will be available at:"
echo "  - Frontend:  http://localhost:3000"
echo "  - Backend:   http://localhost:8080/api"
echo "  - Swagger:   http://localhost:8080/api/swagger-ui.html"
echo "  - MinIO:     http://localhost:9001 (minioadmin/minioadmin123)"
echo ""
echo "Happy coding! 🚗💨"