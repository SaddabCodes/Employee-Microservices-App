# Employee Microservices App - QWEN Context

## Project Overview

This is a **Spring Boot 4.x microservices architecture** for managing employees and their addresses with JWT-based authentication. The project demonstrates a distributed microservices pattern using Spring Cloud, Netflix Eureka for service discovery, OpenFeign for inter-service communication, and Spring Cloud Gateway for API routing.

### Architecture

The system consists of **5 independent services** that communicate via REST APIs:

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

### Technology Stack

- **Java**: 25
- **Spring Boot**: 4.0.3 / 4.0.5
- **Spring Cloud**: 2025.1.1
- **Database**: MySQL 8.x (shared database: `microservice_employee`)
- **Service Discovery**: Netflix Eureka
- **Inter-service Communication**: OpenFeign
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT (JJWT 0.13.0)
- **Build Tool**: Maven
- **Boilerplate Reduction**: Lombok
- **Object Mapping**: ModelMapper 3.2.4

---

## Project Structure

```
Employee-Microservices-App/
├── EurekaServer/                 # Service Registry (Port: 8761)
├── Employee/                     # Employee Service (Port: 8081)
├── Address/                      # Address Service (Port: 8082)
├── Auth/                         # Auth Service (Port: 8083)
├── ApiGateway/                   # API Gateway (Port: 9090)
├── README.md                     # Project documentation
├── FIXES_SUMMARY.md             # Previous fixes and troubleshooting
└── QWEN.md                       # This file - AI assistant context
```

### Service Details

#### 1. **EurekaServer** (`:8761`)
- Service registry and discovery using Netflix Eureka
- All microservices register with this server on startup
- Health monitoring and Eureka Dashboard at `http://localhost:8761`

#### 2. **Employee Service** (`:8081`)
- Core employee management with CRUD operations
- Fetches address data from Address service via OpenFeign
- Composite unique constraint on (empCode, companyName)
- DTO mapping using ModelMapper
- Database: `microservice_employee` → `employees` table

#### 3. **Address Service** (`:8082`)
- Manages employee addresses (PERMANENT or TEMPORARY types)
- Validates employee existence via Feign call to Employee service
- Supports delta updates (create/update/delete)
- Custom error handling with GlobalExceptionHandler
- Database: `microservice_employee` → `address` table

#### 4. **Auth Service** (`:8083`)
- User registration and JWT token-based authentication
- Spring Security with stateless session management
- BCrypt password encoding
- Role-based access control
- Database: `microservice_employee` → `users` table

#### 5. **ApiGateway** (`:9090`)
- Spring Cloud Gateway for routing requests to microservices
- Routes:
  - `/employee/**` → Employee Service (with AuthFilter)
  - `/addresses/**` → Address Service (with AuthFilter)
  - `/auth/**` → Auth Service (no auth filter for login/register)
- Uses Eureka service discovery for load balancing

---

## Building and Running

### Prerequisites
- **JDK 25** or higher
- **Maven 3.6+**
- **MySQL Server 8.x** running on `localhost:3306`
- Database `microservice_employee` created

### Database Setup
```sql
CREATE DATABASE IF NOT EXISTS microservice_employee;
```

Update credentials in `application.yaml` files if needed (default: `root/1234`).

### Build All Services
```bash
cd EurekaServer && mvn clean install
cd ../Employee && mvn clean install
cd ../Address && mvn clean install
cd ../Auth && mvn clean install
cd ../ApiGateway && mvn clean install
```

### Running the Application

**Start services in this exact order:**

1. **EurekaServer**
   ```bash
   cd EurekaServer
   mvn spring-boot:run
   ```

2. **Employee Service**
   ```bash
   cd Employee
   mvn spring-boot:run
   ```

3. **Address Service**
   ```bash
   cd Address
   mvn spring-boot:run
   ```

4. **Auth Service**
   ```bash
   cd Auth
   mvn spring-boot:run
   ```

5. **ApiGateway**
   ```bash
   cd ApiGateway
   mvn spring-boot:run
   ```

### Verify Services
- Eureka Dashboard: `http://localhost:8761`
- All services should show as `UP`

---

## API Endpoints

### Via API Gateway (Port: 9090)

**Employee Service** (requires authentication):
- `POST /employee/save` - Create employee
- `PUT /employee/update/{id}` - Update employee
- `DELETE /employee/delete/{id}` - Delete employee
- `GET /employee/{id}` - Get employee by ID
- `GET /employee/all` - Get all employees
- `GET /employee/get-by-emp-code-and-company-name?empCode=&companyName=` - Composite lookup

**Address Service** (requires authentication):
- `POST /addresses/save` - Save address(es)
- `PUT /addresses/update` - Update address(es)
- `GET /addresses/{addressId}` - Get address by ID
- `GET /addresses/all-address` - Get all addresses
- `DELETE /addresses/delete/{addressId}` - Delete address
- `GET /addresses/empId/{empId}` - Get addresses by employee ID

**Auth Service** (no authentication required):
- `POST /auth/register-user` - Register new user
- `POST /auth/generate-token` - Login and get JWT token

---

## Service Communication Patterns

### OpenFeign Clients
- **Employee → Address**: Uses Eureka discovery (`lb://ADDRESS`)
  - Calls: `GET /addresses/empId/{empId}`
  
- **Address → Employee**: Uses explicit URL configuration
  - Calls: `GET /employee/{id}`
  - Includes custom `CustomErrorDecoder` for error handling

### API Gateway Routing
- All external requests go through ApiGateway (port 9090)
- AuthFilter applied to Employee and Address routes
- Auth routes bypass filter for public access

---

## Development Conventions

### Exception Handling
- Each service has a `GlobalExceptionHandler` with `@RestControllerAdvice`
- Standard error response format:
  ```json
  {
    "message": "Error description",
    "status": 400,
    "timestamp": "2026-04-04T10:30:45.123Z"
  }
  ```

### JWT Authentication
- Token generation via `JwtUtil` class in Auth service
- Uses HMAC-SHA signing (min 256-bit key)
- Default expiration: 24 hours (86400000ms)
- Configurable via `jwt.secret` and `jwt.expiration` properties

### Database Configuration
- Shared database pattern (for simplicity)
- Hibernate DDL-auto: `update`
- All services connect to: `jdbc:mysql://localhost:3306/microservice_employee`

### Code Style
- Package structure: `com.sadcodes.{service}.{layer}`
- Layers: controller, service, repository, model, dto, exception, config
- Lombok annotations for boilerplate reduction
- ModelMapper for DTO-to-entity mapping

---

## Common Troubleshooting

### Services Not Registering with Eureka
- Ensure EurekaServer starts completely before other services
- Check `eureka.client.register-with-eureka=true` in config
- Verify network connectivity between services

### Database Connection Issues
- Verify MySQL is running on `localhost:3306`
- Check credentials in `application.yaml` files
- Ensure database `microservice_employee` exists

### Feign Client Errors
- Ensure both related services are running
- Check URL configuration for explicit Feign clients
- Review logs for `CustomErrorDecoder` messages

### JWT Authentication Issues
- Verify JWT secret is configured (min 256 bits)
- Ensure BCrypt encoder is configured
- Check token expiration settings

### Gateway Routing Issues
- Verify routes in `application.yaml` match service paths
- Check that AuthFilter is properly implemented
- Ensure Eureka discovery is working for load-balanced routes

---

## Important Files to Reference

- `README.md` - Complete project documentation
- `FIXES_SUMMARY.md` - Previous fixes and implementation details
- Each service's `application.yaml` - Configuration and properties
- `pom.xml` files - Dependencies and build configuration

---

## Notes for AI Assistant

- This is a **Spring Boot 4.x** project (latest version)
- Uses **Java 25** features
- **Shared database** pattern (note: production microservices should use database-per-service)
- **5 services total**: EurekaServer, Employee, Address, Auth, ApiGateway
- API Gateway is the **entry point** for all client requests (port 9090)
- **Service startup order matters**: EurekaServer → Employee → Address → Auth → ApiGateway
- Always use **absolute paths** when working with files
- Use `mvn spring-boot:run` for development, `mvn clean install` for building
- Test endpoints via API Gateway (9090) or directly to services (8081-8083)
