# OrderPulse - Event-Driven Order Management System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.6-black)
![License](https://img.shields.io/badge/License-MIT-yellow)

A production-ready, event-driven order management system built with Spring Boot, Apache Kafka, and modern microservices architecture.

## 🚀 Features

- ✅ RESTful API with comprehensive CRUD operations
- ✅ Event-driven architecture using Apache Kafka
- ✅ Real-time email notifications (SendGrid)
- ✅ JWT-based authentication & authorization
- ✅ Redis caching for performance optimization
- ✅ PostgreSQL database with JPA/Hibernate
- ✅ Comprehensive API documentation (Swagger/OpenAPI)
- ✅ Unit & integration testing
- ✅ Docker containerization

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.x
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **Caching:** Redis
- **Message Broker:** Apache Kafka
- **Security:** Spring Security + JWT
- **Email:** SendGrid API
- **Documentation:** SpringDoc OpenAPI (Swagger)
- **Testing:** JUnit 5, Mockito, AssertJ

### DevOps
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Maven
- **CI/CD:** GitHub Actions (planned)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15 (or use Docker)
- Redis (or use Docker)
- Apache Kafka (or use Docker)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/OrderPulse.git
cd OrderPulse
```

### 2. Start Infrastructure (Docker)

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- Redis on port 6379
- Kafka on port 9092
- Zookeeper on port 2181

### 3. Configure Environment Variables

Create `.env` file in project root:

```env
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/orderpulse
DATABASE_USERNAME=orderpulse_user
DATABASE_PASSWORD=your_password

# SendGrid
SENDGRID_API_KEY=your_sendgrid_api_key
SENDGRID_FROM_EMAIL=noreply@orderpulse.com

# JWT
JWT_SECRET=your-256-bit-secret-key

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 4. Build & Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

### 5. Access API Documentation

Open browser: `http://localhost:8080/swagger-ui.html`

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Orders
- `GET /api/orders` - Get all orders (paginated)
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}/status` - Update order status
- `DELETE /api/orders/{id}` - Delete order
- `GET /api/orders/customer/{name}` - Get orders by customer
- `GET /api/orders/status/{status}` - Get orders by status

## 🏗️ Architecture

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│   Client    │─────▶│  REST API    │─────▶│  Database   │
│ (Frontend)  │      │ (Spring Boot)│      │ (PostgreSQL)│
└─────────────┘      └──────────────┘      └─────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │    Kafka     │
                     │  (Events)    │
                     └──────────────┘
                            │
                            ▼
                     ┌──────────────┐      ┌─────────────┐
                     │   Consumer   │─────▶│   SendGrid  │
                     │   Service    │      │   (Email)   │
                     └──────────────┘      └─────────────┘
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## 📦 Docker Deployment

```bash
# Build Docker image
docker build -t orderpulse-backend:latest ./backend

# Run with Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

## 🔒 Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control (RBAC)
- Input validation
- SQL injection prevention
- XSS protection

## 📈 Performance

- Redis caching for frequently accessed data
- Database connection pooling (HikariCP)
- Async event processing with Kafka
- Optimized database queries with JPA
- Response time: < 200ms (avg)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👤 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

## 🙏 Acknowledgments

- Spring Boot Team
- Apache Kafka Community
- SendGrid

---

⭐ Star this repo if you find it helpful!
