# Gym CRM Core Service

This service is the main backend service for the CRM system, managing trainees, trainers, training sessions, and user authentication.

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 21**
- **Maven**
- **Git**
- **Redis**
- **MySQL Server**

## 1. Clone the project

```bash
git clone https://github.com/juliakhomyn/gym-crm-platform.git
cd gym-crm-platform
```

## 2. Database Setup MySQL
Run the following script to create the database and add a user:

```sql
CREATE DATABASE gym_db;
CREATE USER 'gymuser'@'localhost' IDENTIFIED BY 'gympass';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gymuser'@'localhost';
FLUSH PRIVILEGES;
```

## 3. Redis Setup
Make sure Redis is installed and running on your machine.

* On macOS, you can install Redis using Homebrew:
```bash
brew install redis
brew services start redis
```
* On Ubuntu/Debian:
```bash
sudo apt-get install redis-server
sudo systemctl start redis
```
* Or download from https://redis.io/download

## 4. Environment Variables
Set the following environment variables before running the application:

### Database Configuration
```text
DB_URL=jdbc:mysql://localhost:3306/gym_db
DB_USERNAME=gymuser
DB_PASSWORD=gympass
```

### Redis Configuration
```text
REDIS_HOST=localhost
REDIS_PORT=6379
```

### JWT Configuration
```text
JWT_SECRET=your_jwt_secret_key
```

### CORS Configuration
```text
CORS_ALLOWED_ORIGINS=http://localhost:3000 
```

### Microservice URLs
```text
WORKLOAD_SERVICE_URL=http://workload-service:8081/workload-service/api/v1   # Base URL for the workload microservice
DISCOVERY_SERVICE_URL=http://localhost:8761/eureka/                         # Eureka discovery server URL
```

### Spring Profiles
If you want to use specific environment, you can configure it by adding:

```text
SPRING_PROFILES_ACTIVE=dev
```

## 5. Build the project

```bash
mvn clean compile
```

## 6. Run tests

```bash
mvn test
```

## 7. Run services from console

To work successfully, services have to be run in the following order.

### discovery-service

```bash
cd discovery-service
mvn spring-boot:run
```

Available at: http://localhost:8761

### gym-core-service

```bash
cd gym-core-service
mvn spring-boot:run
```

After startup, service will be available at:

* Base API Path: http://localhost:8082/gym-crm/core/api/v1
* OpenAPI / Swagger UI: http://localhost:8082/gym-crm/core/swagger-ui/index.html
* OpenAPI Spec (JSON): http://localhost:8082/gym-crm/core/v3/api-docs

### workload-service

```bash
cd workload-service
mvn spring-boot:run
```

Available at: http://localhost:8081/gym-crm/workload

## Postman Collection

The project includes a Postman collection for testing the API. You can find it at the following relative path:

```text
docs/postman/gym-crm.postman_collection.json
```

## Actuator and Metrics

Spring Boot Actuator and Micrometer are configured to expose system and custom metrics.

Base local URL: http://localhost:8082/gym-crm/core/actuator

### Health:

* Database Health: http://localhost:8082/gym-crm/core/actuator/health/database
* Disk Space Health: http://localhost:8082/gym-crm/core/actuator/health/diskSpace
* Memory Health: http://localhost:8082/gym-crm/core/actuator/health/memory
* Prometheus metrics: http://localhost:8082/gym-crm/core/actuator/prometheus

### Custom Metrics

* **User Registrations**: http://localhost:8082/gym-crm/core/actuator/metrics/gym.auth.login.attempts
  * Tags: `type` (trainee, trainer), `status` (success, failure)
* **Login Attempts**: http://localhost:8082/gym-crm/core/actuator/metrics/gym.auth.login.attempts
  * Tags: `status` (success, failure)
* **Trainings Created**: http://localhost:8082/gym-crm/core/actuator/metrics/gym.training.creations
  * Tags: `type` (training type name e.g. Yoga, Pilates)
  * *Note: Returns 404 until at least one training has been created.*
* **Active Users**: http://localhost:8082/gym-crm/core/actuator/metrics/gym.users.active
  * Tags: `type` (trainee, trainer)
* **Total Users**: http://localhost:8082/gym-crm/actuator/core/metrics/gym.users.total
  * Tags: `type` (trainee, trainer)