# Gym CRM Automation Testing

This module contains the end-to-end integration and behavior-driven development (BDD) tests for the Gym CRM Platform. The test suite uses Cucumber, JUnit 5, RestAssured, and Testcontainers to orchestrate, run, and verify the multi-service system in an isolated Docker environment.

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 21**
- **Maven**
- **Git**
- **Docker Engine / Docker Desktop (running)**

## 1. Package the Java Applications

From the root directory (gym-crm-platform), run the package phase to compile code and generate the .jar files in each module's target/ directory:

```bash
mvn clean package -DskipTests
```

## 2. Build the Docker Images Locally
Once the JARs are ready, build the images using the custom Dockerfiles

### Build core service image

```bash
cd gym-core-service
docker build -t gym-core-service:local .
```

### Build workload service image

```bash
cd ../workload-service
docker build -t workload-service:local .
```

Return to the root directory and verify the images exist:

```bash
cd ..
docker images
```

## 3. Run the tests

Run the integration tests using the Maven Failsafe plugin from your project root:

```bash
mvn clean verify -pl :gym-automation -am
```

### Run Specific Feature Groups (By Target Endpoint / Component)

```bash
# Authentication tests only
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@auth"

# Core tests only
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@core"

# Workload tests only
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@workload"

# Integration tests only (Core-to-Workload communication)
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@integration"
```

### Run Specific Scenarios (By Test Type)

```bash
# Happy path tests only (Status 200 checks)
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@positive"

# Error path tests only (Status 400/401/404 checks)
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@negative"

# Input validation/empty payload/edge cases only
mvn clean verify -pl :gym-automation -am -Dcucumber.filter.tags="@validation or @edge"
```