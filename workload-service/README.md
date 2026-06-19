# Workload Service

This service manages trainer workload data and registers itself with the Discovery Server (Eureka).

## Prerequisites

- **Java Development Kit (JDK) 21**
- **Maven**
- **[Discovery Server](../discovery-service/README.md)** must be running

## 1. Clone the project

```bash
git clone https://github.com/juliakhomyn/gym-crm-platform.git
cd workload-service
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

Available at: http://localhost:8081

After startup, service will be available at:

* Base API Path: http://localhost:8081/workload-service/api/v1
* OpenAPI / Swagger UI: http://localhost:8081/workload-service/swagger-ui/index.html
* OpenAPI Spec (JSON): http://localhost:8081/workload-service/v3/api-docs
