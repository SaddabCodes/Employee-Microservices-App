# Employee Microservices App

Spring Boot microservices demo with service discovery, API gateway, JWT-based authentication, and inter-service communication.

## Services Overview

| Service | Port | Purpose | Discovery Name | Spring Boot |
|---|---:|---|---|---|
| EurekaServer | 8761 | Service registry | `EurekaServer` | 4.0.5 |
| ApiGateway | 9090 | Single entry point, JWT validation, circuit breaker | `API-GATEWAY` | 3.5.13 |
| Auth | 8083 | User registration and JWT token generation | `Auth` | 4.0.5 |
| Employee | 8081 | Employee CRUD + fetch employee addresses | `Employee` | 4.0.3 |
| Address | 8082 | Address CRUD by employee | `Address` | 4.0.3 |

## Architecture

### Service Communication Pattern

```
                    ┌─────────────────┐
                    │  EurekaServer   │
                    │   Port: 8761    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┬──────────────┐
              │              │              │              │
    ┌─────────▼────┐  ┌─────▼──────┐  ┌────▼───────┐  ┌───▼────────┐
    │   Employee   │  │  Address   │  │    Auth    │  │ ApiGateway │
    │  Port: 8081  │  │ Port: 8082 │  │ Port: 8083 │  │ Port: 9090 │
    └──────────────┘  └────────────┘  └────────────┘  └────────────┘
```

### Inter-Service Communication (OpenFeign)

**Employee → Address Service:**
- Uses Eureka discovery (`lb://ADDRESS`)
- Calls: `GET /addresses/empId/{empId}`
- Purpose: Enrich employee responses with address data
- Error Handling: Tolerates Address service failures gracefully

**Address → Employee Service:**
- Uses explicit URL configuration
- Calls: `GET /employee/{id}`
- Purpose: Validates employee existence before saving addresses
- Error Handling: Custom `CustomErrorDecoder` for Feign errors

### API Gateway Flow

```
Client Request (port 9090)
    ↓
API Gateway
    ↓
[AuthFilter] → Validates JWT for /employee/** and /addresses/**
    ↓
[CircuitBreaker] → Protects against downstream failures
    ↓
Route to Microservice via Eureka (lb://SERVICE)
```

## Tech Stack

- Java 25
- Spring Boot 4.0.3 (Employee, Address) / 4.0.5 (Auth, EurekaServer) / 3.5.13 (ApiGateway)
- Spring Cloud 2025.1.1 (microservices) / 2025.0.1 (ApiGateway)
- Spring Cloud Netflix Eureka (Service Discovery)
- Spring Cloud OpenFeign (Inter-service Communication)
- Spring Cloud Gateway (API Gateway with WebFlux)
- Spring Security + JJWT 0.13.0
- Resilience4j Circuit Breaker (ApiGateway)
- Spring Boot Actuator (ApiGateway)
- MySQL 8.x
- Maven Wrapper (`mvnw` / `mvnw.cmd`)
- Lombok (Boilerplate Reduction)
- ModelMapper 3.2.4 (DTO-Entity Mapping)

## Project Structure

- `EurekaServer/`
- `ApiGateway/`
- `Auth/`
- `Employee/`
- `Address/`

Each service is an independent Maven project (no root multi-module `pom.xml`).

## Prerequisites

- JDK 25
- MySQL running locally
- Maven is optional (wrapper included)

## Database Setup

All data services use the same database configured in `application.yaml|yml`:

- **Database**: `microservice_employee`
- **URL**: `jdbc:mysql://localhost:3306/microservice_employee`
- **Username**: `root`
- **Password**: `1234`
- **JPA**: `ddl-auto=update`
- **Tables**: `employees`, `address`, `users`

Create database once:

```sql
CREATE DATABASE IF NOT EXISTS microservice_employee;
```

> **Note**: This project uses a shared database pattern for simplicity. In production microservices, each service should have its own database.

## How To Run

Start services in this order:

1. EurekaServer
2. Auth
3. Employee
4. Address
5. ApiGateway

### Windows (PowerShell)

```powershell
cd EurekaServer; .\mvnw.cmd spring-boot:run
cd ..\Auth; .\mvnw.cmd spring-boot:run
cd ..\Employee; .\mvnw.cmd spring-boot:run
cd ..\Address; .\mvnw.cmd spring-boot:run
cd ..\ApiGateway; .\mvnw.cmd spring-boot:run
```

### macOS/Linux

```bash
cd EurekaServer && ./mvnw spring-boot:run
cd ../Auth && ./mvnw spring-boot:run
cd ../Employee && ./mvnw spring-boot:run
cd ../Address && ./mvnw spring-boot:run
cd ../ApiGateway && ./mvnw spring-boot:run
```

## Service Discovery

Eureka dashboard:

- `http://localhost:8761`

All services are configured to register with Eureka (`defaultZone: http://localhost:8761/eureka`).

## API Gateway

Base URL:

- `http://localhost:9090`

### Routes

| Path | Service | Auth Required | Circuit Breaker |
|---|---|---|---|
| `/auth/**` | Auth (lb://AUTH) | No | No |
| `/employee/**` | Employee (lb://EMPLOYEE) | Yes (JWT) | Yes (5s timeout) |
| `/addresses/**` | Address (lb://ADDRESS) | Yes (JWT) | Yes (5s timeout) |

### JWT Authentication

- Header is mandatory for protected routes
- Format must be: `Authorization: Bearer <token>`
- Gateway validates JWT token using configured secret

### Circuit Breaker Configuration (Resilience4j)

**Timeout Settings:**
- Employee Service: 5 seconds
- Address Service: 5 seconds

**Circuit Breaker Settings:**
- Sliding window size: 5 calls
- Minimum calls threshold: 5
- Failure rate threshold: 50%
- Wait duration in open state: 6 seconds
- Auto-transition to half-open: Enabled
- Calls in half-open state: 3

**Fallback Endpoints:**
- `/employeeServiceFallback` - Called when Employee service is unavailable
- `/addressServiceFallback` - Called when Address service is unavailable

### Management Endpoints

The ApiGateway exposes Spring Boot Actuator endpoints:

- `http://localhost:9090/actuator/health` - Service health
- `http://localhost:9090/actuator/health/circuitbreakers` - Circuit breaker status
- `http://localhost:9090/actuator/info` - Service info
- `http://localhost:9090/actuator/metrics` - Service metrics

## Authentication Flow

1. Register user
2. Generate token
3. Call protected endpoints with Bearer token via gateway

### 1) Register

```http
POST /auth/register-user
Host: localhost:9090
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "username": "john",
  "password": "pass123",
  "roles": "USER"
}
```

### 2) Generate JWT

```http
POST /auth/generate-token
Host: localhost:9090
Content-Type: application/json

{
  "username": "john",
  "password": "pass123"
}
```

Response includes:

- `token`
- `type` (Bearer)
- `validUntil`

## Core APIs (via Gateway)

Use:

- `Authorization: Bearer <your_token>`

### Employee APIs

- `POST /employee/save`
- `PUT /employee/update/{id}`
- `DELETE /employee/delete/{id}`
- `GET /employee/{id}`
- `GET /employee/all`
- `GET /employee/get-by-emp-code-and-company-name?empCode=...&companyName=...`

Sample create employee:

```json
{
  "empName": "Alice",
  "empEmail": "alice@company.com",
  "empCode": "EMP-1001",
  "companyName": "ACME"
}
```

### Address APIs

- `POST /addresses/save`
- `PUT /addresses/update`
- `GET /addresses/{addressId}`
- `GET /addresses/all-address`
- `DELETE /addresses/delete/{addressId}`
- `GET /addresses/empId/{empId}`

Sample save/update address payload:

```json
{
  "empId": 1,
  "addressRequestDtoList": [
    {
      "street": "Main Road",
      "pinCode": 123456,
      "city": "Pune",
      "country": "India",
      "addressType": "PERMANENT"
    },
    {
      "street": "Work Street",
      "pinCode": 654321,
      "city": "Mumbai",
      "country": "India",
      "addressType": "TEMPORARY"
    }
  ]
}
```

## Notes

- Employee and Address service methods include intentional `Thread.sleep(6000)` in some GET APIs to help test gateway circuit breaker behavior.
- Address service validates employee existence before saving/updating addresses.
- Employee service tries to enrich employee response with addresses and tolerates address-service failure.

## Running Tests

Run tests per service:

```powershell
cd EurekaServer; .\mvnw.cmd test
cd ..\Auth; .\mvnw.cmd test
cd ..\Employee; .\mvnw.cmd test
cd ..\Address; .\mvnw.cmd test
cd ..\ApiGateway; .\mvnw.cmd test
```

## Troubleshooting

- `401 Unauthorized` from gateway:
  - Missing `Authorization` header
  - Wrong header format (must start with `Bearer `)
  - Invalid/expired JWT
- Service not visible in Eureka:
  - Ensure Eureka server is running first on port `8761`
- MySQL connection errors:
  - Verify DB exists and credentials in each service config
- Fallback response returned:
  - Downstream service is slow/unavailable and circuit breaker opened
