# Gym CRM Platform

![Build](https://github.com/juliakhomyn/gym-crm-platform/actions/workflows/ci.yml/badge.svg?branch=develop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm-platform&metric=coverage)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm-platform)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm-platform&metric=alert_status)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm-platform)

## Platform Services

* **[Discovery Service](discovery-service/README.md)**: Provides service discovery for the microservices ecosystem using Netflix Eureka. All other microservices register themselves with this service, enabling dynamic discovery and load balancing.
* **[Gateway Service](gateway-service/README.md)**: Routes external requests to the appropriate microservice using Spring Cloud Gateway and Eureka service discovery.
* **[Workload Service](workload-service/README.md)**: Manages trainer workload data.
* **[Gym Core Service](gym-core-service/README.md)**: The core service for the CRM system, managing trainees, trainers, training sessions, and user authentication.
* **[Gym Automation Service (Testing)](gym-automation/README.md)**: Provides end-to-end BDD integration tests using Cucumber, RestAssured, and Testcontainers.

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 21**
- **Maven**
- **Git**
- **Docker Compose / Docker Desktop** (Required for running full workflow and integration test suite via Testcontainers)
- **Redis**
- **MySQL Server**
- **ActiveMQ**

## Platform Setup

### 1. Clone the project

```bash
git clone https://github.com/juliakhomyn/gym-crm-platform.git
cd gym-crm-platform
```

### 2. Build the project

```bash
mvn clean compile
```

### 3. Run tests

```bash
mvn test
```

### 4. Run services

To work successfully, services have to be run in the following order.

1. **[Discovery Service](discovery-service/README.md)** (Eureka Server, port `8761`)
2. **[Gateway Service](gateway-service/README.md)** (Port `8080`)
3. **[Workload Service](workload-service/README.md)** (Port `8081`)
4. **[Gym Core Service](gym-core-service/README.md)** (Port `8082`)

Please refer to individual service documentation for specific setup and configuration requirements.

## Run with Docker Compose

The application can be started locally using Docker Compose. The infrastructure consists of:
- **Discovery Service** — Eureka service registry
- **API Gateway** — entry point for client requests
- **Gym Core Service** — main application service
- **Workload Service** — workload processing service
- **MySQL** — relational database
- **MongoDB** — workload database
- **Redis** — caching
- **ActiveMQ** — message broker

### 1. Environment variables

Docker uses '.env' file to read local environment variables. Create a '.env' file in the project root and fill it with values from '.env.example' file.

### 2. Start the application

Build all application images:

```bash
docker compose build
```

Start all services in detached mode:

```bash
docker compose up -d
```

Check the status:

```bash
docker compose ps
```

Wait until the required services become healthy before sending requests.

#### Logs

View logs for a particular service:

```bash
docker compose logs <service-name> --tail <tail-value>
```

#### Stop the application

Stop containers without removing volumes:

```bash
docker compose down
```

Database and other persistent data are stored in Docker volumes, so restarting the containers does not remove the data.

To stop and remove containers, networks and volumes:

```bash
docker compose down -v
```

## Postman Collection

The project includes a Postman collection for testing the API. You can find it at the following relative path:

```text
docs/postman/gym-crm.postman_collection.json
```

## Actuator and Metrics

Spring Boot Actuator and Micrometer are configured to expose system and custom metrics.

Base local URL: http://localhost:8080/actuator