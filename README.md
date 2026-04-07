# Employee Microservices Application

A Spring Boot-based microservices architecture for managing employees and their addresses, with JWT-based authentication.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Microservices](#microservices)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Service Communication](#service-communication)
- [Project Structure](#project-structure)

---

## 🏗️ Architecture Overview

This project demonstrates a distributed microservices architecture using Spring Boot and Spring Cloud. The system consists of **4 independent microservices** that communicate via REST APIs using OpenFeign, with Netflix Eureka for service discovery and registration.

```
                    ┌─────────────────┐
                    │  EurekaServer   │
                    │   Port: 8761    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
    ┌─────────▼────┐  ┌─────▼──────┐  ┌────▼───────┐
    │   Employee   │  │  Address   │  │    Auth    │
    │  Port: 8081  │  │ Port: 8082 │  │ Port: 8083 │
    └──────────────┘  └────────────┘  └────────────┘
```

---

## 🔧 Microservices

### 1. EurekaServer (`:8761`)
Service registry and discovery server using Netflix Eureka. All microservices register with this server on startup.

**Key Features:**
- Service registration and discovery
- Eureka Dashboard available at `http://localhost:8761`
- Health monitoring of registered services

---

### 2. Employee Service (`:8081`)
Core employee management service providing CRUD operations for employee records.

**Key Features:**
- Employee CRUD operations
- Fetches address data from Address service via OpenFeign
- Composite unique constraint on (empCode, companyName)
- DTO mapping using ModelMapper

**Database:** `microservice_employee` → `employees` table

---

### 3. Address Service (`:8082`)
Manages employee addresses with support for multiple addresses per employee (PERMANENT or TEMPORARY).

**Key Features:**
- Address CRUD operations
- Validates employee existence via Feign call to Employee service
- Supports delta updates (create/update/delete)
- Custom error handling for Feign clients

**Database:** `microservice_employee` → `address` table

---

### 4. Auth Service (`:8083`)
User registration and JWT token-based authentication service.

**Key Features:**
- User registration and login
- JWT token generation and validation
- Spring Security with stateless session management
- BCrypt password encoding
- Role-based access control

**Database:** `microservice_employee` → `users` table

---

## 💻 Technology Stack

| Technology | Version |
|------------|---------|
| Java | 25 |
| Spring Boot | 4.0.3 / 4.0.5 |
| Spring Cloud | 2025.1.1 |
| Spring Data JPA | Latest |
| MySQL | 8.x |
| Netflix Eureka | Service Discovery |
| OpenFeign | Declarative REST Client |
| Spring Security | Authentication & Authorization |
| JWT (JJWT) | 0.13.0 |
| Lombok | Boilerplate Reduction |
| ModelMapper | 3.2.4 |
| Maven | Build Tool |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK) 25** or higher
- **Maven** 3.6+ for building projects
- **MySQL Server** 8.x running on `localhost:3306`
- **Git** (optional, for cloning)

---

## 🚀 Setup & Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Employee-Microservices-App
```

### 2. Database Setup

Ensure MySQL is running and create the database:

```sql
CREATE DATABASE IF NOT EXISTS microservice_employee;
```

Update database credentials in each service's `application.yaml` if your MySQL credentials differ:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/microservice_employee
    username: root
    password: 1234
```

### 3. Build All Services

```bash
# Build EurekaServer
cd EurekaServer
mvn clean install

# Build Employee Service
cd ../Employee
mvn clean install

# Build Address Service
cd ../Address
mvn clean install

# Build Auth Service
cd ../Auth
mvn clean install
```

---

## ▶️ Running the Application

**Important:** Start services in the following order:

### Step 1: Start EurekaServer

```bash
cd EurekaServer
mvn spring-boot:run
```

Wait for EurekaServer to fully start (dashboard available at `http://localhost:8761`).

### Step 2: Start Employee Service

```bash
cd Employee
mvn spring-boot:run
```

### Step 3: Start Address Service

```bash
cd Address
mvn spring-boot:run
```

### Step 4: Start Auth Service

```bash
cd Auth
mvn spring-boot:run
```

### Verify Services

After starting all services, verify they are registered in Eureka Dashboard: `http://localhost:8761`

All services should show as `UP` in the Eureka dashboard.

---

## 📡 API Documentation

### Employee Service (`http://localhost:8081`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/employee/save` | Create a new employee |
| PUT | `/employee/update/{id}` | Update employee by ID |
| DELETE | `/employee/delete/{id}` | Delete employee by ID |
| GET | `/employee/{id}` | Get employee by ID (includes addresses) |
| GET | `/employee/all` | Get all employees (includes addresses) |
| GET | `/employee/get-by-emp-code-and-company-name` | Get employee by empCode and companyName |

**Query Parameters for composite lookup:**
- `empCode` - Employee code
- `companyName` - Company name

---

### Address Service (`http://localhost:8082`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/addresses/save` | Save address(es) for an employee |
| PUT | `/addresses/update` | Update address(es) for an employee |
| GET | `/addresses/{addressId}` | Get address by ID |
| GET | `/addresses/all-address` | Get all addresses |
| DELETE | `/addresses/delete/{addressId}` | Delete address by ID |
| GET | `/addresses/empId/{empId}` | Get all addresses for an employee |

**Address Types:** `PERMANENT`, `TEMPORARY`

---

### Auth Service (`http://localhost:8083`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register-user` | Register a new user | No |
| POST | `/auth/generate-token` | Login and generate JWT token | No |
| GET | `/auth/**` | Other endpoints | Yes |

**Sample Registration Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe",
  "password": "password123",
  "roles": ["USER"]
}
```

**Sample Login Request:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

---

## 🗄️ Database Schema

All services share the same MySQL database: `microservice_employee`

### Tables

**`employees`** - Managed by Employee Service
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `emp_name` (VARCHAR)
- `emp_email` (VARCHAR)
- `emp_code` (VARCHAR)
- `company_name` (VARCHAR)
- Unique constraint: `(emp_code, company_name)`

**`address`** - Managed by Address Service
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `emp_id` (BIGINT, FK → employees.id)
- `street` (VARCHAR)
- `pin_code` (VARCHAR)
- `city` (VARCHAR)
- `country` (VARCHAR)
- `address_type` (ENUM: PERMANENT, TEMPORARY)

**`users`** - Managed by Auth Service
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `email` (VARCHAR)
- `username` (VARCHAR, UNIQUE)
- `password` (VARCHAR, BCrypt encoded)
- `roles` (VARCHAR)

---

## 🔗 Service Communication

### OpenFeign Clients

**Employee → Address Service**
- Uses Eureka service discovery
- Calls `GET /addresses/empId/{empId}` to fetch employee addresses
- Defined in: `AddressFeignClient`

**Address → Employee Service**
- Uses explicit URL configuration (`${employee.service.url}`)
- Calls `GET /{id}` to validate employee existence
- Defined in: `EmployeeFeignClient`
- Includes custom `CustomErrorDecoder` for error handling

### Communication Flow

```
Employee Service ──Feign──► Address Service
     ▲                          │
     │                          │
     └───────Feign──────────────┘
```

---

## 📁 Project Structure

```
Employee-Microservices-App/
├── EurekaServer/                 # Service Registry (Port: 8761)
│   ├── src/main/java/
│   │   └── com.sadcodes.eurekaserver/
│   │       ├── EurekaServerApplication.java
│   │       └── config/
│   ├── src/main/resources/
│   │   └── application.yaml
│   └── pom.xml
│
├── Employee/                     # Employee Service (Port: 8081)
│   ├── src/main/java/
│   │   └── com.sadcodes.employee/
│   │       ├── EmployeeApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── exception/
│   │       └── config/
│   ├── src/main/resources/
│   │   └── application.yaml
│   └── pom.xml
│
├── Address/                      # Address Service (Port: 8082)
│   ├── src/main/java/
│   │   └── com.sadcodes.address/
│   │       ├── AddressApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── feign/
│   │       └── config/
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── Auth/                         # Auth Service (Port: 8083)
│   ├── src/main/java/
│   │   └── com.sadcodes.auth/
│   │       ├── AuthApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── security/
│   │       ├── jwt/
│   │       └── config/
│   ├── src/main/resources/
│   │   └── application.yaml
│   └── pom.xml
│
└── FIXES_SUMMARY.md              # Documentation of previous fixes
```

---

## 🔍 Troubleshooting

**Services not showing in Eureka Dashboard:**
- Ensure EurekaServer starts completely before other services
- Check that all services have `eureka.client.register-with-eureka=true`
- Verify network connectivity between services

**Database Connection Issues:**
- Verify MySQL is running on `localhost:3306`
- Check database credentials in `application.yaml` files
- Ensure database `microservice_employee` exists

**Feign Client Errors:**
- Ensure both Employee and Address services are running
- Check `employee.service.url` configuration in Address service
- Review logs for custom `CustomErrorDecoder` messages

**JWT Authentication Issues:**
- Verify the JWT secret in Auth service configuration
- Ensure BCrypt encoder is properly configured
- Check token expiration times

---

## 📝 Notes

- **Shared Database Pattern:** This project uses a shared database across services for simplicity. In production microservices, each service should ideally have its own dedicated database.
- **Spring Boot 4.x:** This project uses the latest Spring Boot 4.x framework with Java 25.
- **Service Discovery:** Employee service uses Eureka discovery for Address client, while Address service uses explicit URL for Employee client.

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is created for educational purposes.

---

## 👨‍💻 Author

**Sadab Akhtar**

---

For any issues or questions, please open an issue in the repository.
