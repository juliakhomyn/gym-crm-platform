# Gateway Service

This service is the API Gateway for the Gym CRM platform. It routes external requests to the appropriate microservice using Spring Cloud Gateway and Eureka service discovery.

## Prerequisites

- **Java Development Kit (JDK) 21**
- **Maven**
- **[Discovery Server](../discovery-service/README.md)** must be running

## 1. Clone the project

```bash
git clone https://github.com/juliakhomyn/gym-crm-platform.git
cd gateway-service
```

## 2. Build the project

```bash
mvn clean compile
```

## 3. Run tests

```bash
mvn test
```

## 4. Run Discovery service (if not ran yet)

In a separate terminal:

```bash
cd ../discovery-service
mvn spring-boot:run
```

Available at: http://localhost:8761

## 4. Run the service from console

```bash
mvn spring-boot:run
```

Available at: http://localhost:8080

## Actuator and Metrics

Spring Boot Actuator and Micrometer are configured to expose system and custom metrics.

Base local URL: http://localhost:8080/actuator

Database Health: http://localhost:8080/actuator/health