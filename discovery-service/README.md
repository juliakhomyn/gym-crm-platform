# Discovery Service (Eureka Server)

This service provides service discovery for the microservices ecosystem using Netflix Eureka.
All other microservices register themselves with this service, enabling dynamic discovery and load balancing.

### Run the service

#### 1. Clone the project

```bash
git clone https://github.com/juliakhomyn/gym-crm-platform.git
cd discovery-service
```

#### 2. Run with Maven

```bash
mvn spring-boot:run
```

Available at: http://localhost:8761