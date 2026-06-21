# Gym CRM Platform

![Build](https://github.com/juliakhomyn/gym-crm-platform/actions/workflows/ci.yml/badge.svg?branch=develop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm-platform&metric=coverage)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm-platform)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=juliakhomyn_gym-crm-platform&metric=alert_status)](https://sonarcloud.io/summary/overall?id=juliakhomyn_gym-crm-platform)

## Platform Services

* **[Discovery Service](discovery-service/README.md)**: Provides service discovery for the microservices ecosystem using Netflix Eureka. All other microservices register themselves with this service, enabling dynamic discovery and load balancing.
* **[Gateway Service](gateway-service/README.md)**: Routes external requests to the appropriate microservice using Spring Cloud Gateway and Eureka service discovery.
* **[Workload Service](workload-service/README.md)**: Manages trainer workload data.
* **[Gym Core Service](gym-core-service/README.md)**: The core service for the CRM system, managing trainees, trainers, training sessions, and user authentication.

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 21**
- **Maven**
- **Git**
- **Redis**
- **MySQL Server**

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

## Postman Collection

The project includes a Postman collection for testing the API. You can find it at the following relative path:

```text
docs/postman/gym-crm.postman_collection.json
```

## Actuator and Metrics

Spring Boot Actuator and Micrometer are configured to expose system and custom metrics.

Base local URL: http://localhost:8080/actuator