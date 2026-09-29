# Banking Microservices

A full-stack banking application built with a microservices architecture using Spring Boot, Apache Kafka, and React. The system simulates core banking operations including account management, financial transactions, user authentication, and real-time SMS notifications.

---

## Architecture Overview

```
                        ┌─────────────────┐
                        │  React Frontend │
                        │   (Port 3000)   │
                        └────────┬────────┘
                                 │
                        ┌────────▼────────┐
                        │   API Gateway   │
                        │   (Port 8080)   │
                        │ Circuit Breaker │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
     ┌────────▼───────┐ ┌────────▼───────┐ ┌───────▼────────┐
     │  Auth Service  │ │Account Service │ │  Transaction   │
     │  (Port 8081)   │ │  (Port 8082)   │ │    Service     │
     └────────┬───────┘ └────────┬───────┘ │  (Port 8083)   │
              │                  │         └───────┬────────┘
              │         ┌────────▼────────┐        │
              │         │  Eureka Server  │        │
              └────────►│  (Port 8761)    │◄───────┘
                        └─────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │      Apache Kafka       │
                    │  transaction-events     │
                    │  auth-events            │
                    │  account-events         │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Notification Service   │
                    │      (Port 8085)        │
                    │   SMS Simulation        │
                    └─────────────────────────┘
```

---

## Services

| Service | Port | Description |
|---|---|---|
| Eureka Server | 8761 | Service registry and discovery |
| API Gateway | 8080 | Single entry point, routing, circuit breaker |
| Auth Service | 8081 | User registration, login, JWT authentication |
| Account Service | 8082 | Account management, balance, freeze/close |
| Transaction Service | 8083 | Deposits, withdrawals, transfers |
| Notification Service | 8085 | Kafka consumer, SMS simulation |
| React Frontend | 3000 | Web UI |

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3.5.14
- Spring Cloud 2025.0.0
- Spring Security + JWT
- Spring Data JPA + Hibernate
- Apache Kafka
- Netflix Eureka (Service Discovery)
- Spring Cloud Gateway
- Resilience4j (Circuit Breaker)
- OpenFeign (Inter-service communication)
- MySQL

**Frontend**
- React
- Tailwind CSS

---

## Prerequisites

- Java 21
- Maven
- MySQL
- Apache Kafka + Zookeeper
- Node.js + npm (for frontend)

---

## Database Setup

Create the following MySQL databases:

```sql
CREATE DATABASE auth_service_db;
CREATE DATABASE account_service_db;
CREATE DATABASE transaction_service_db;
CREATE DATABASE notification_service_db;
```

---

## Environment Variables

Each service uses the following environment variables. You can set them or use the defaults:

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | `mySuperSecretJwtKeyThatIsAtLeast32CharactersLong` | JWT signing key |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |

---

## Running the Application

### 1. Start Zookeeper
```bash
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

### 2. Start Kafka
```bash
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### 3. Start Services (in order)

```bash
# 1. Eureka Server
cd eureka && mvn spring-boot:run

# 2. Auth Service
cd auth && mvn spring-boot:run

# 3. Account Service
cd account && mvn spring-boot:run

# 4. Transaction Service
cd transaction && mvn spring-boot:run

# 5. Notification Service
cd notification && mvn spring-boot:run

# 6. API Gateway (last)
cd apigateway && mvn spring-boot:run
```

### 4. Start Frontend
```bash
cd frontend
npm install
npm start
```

> **Note:** Wait 60-90 seconds after all services start before making requests. This allows Eureka to fully sync service registrations.

---

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`.

### Auth Service `/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register a new customer |
| POST | `/auth/login` | Public | Login and receive JWT |
| GET | `/auth/me` | Customer | Get own profile |
| PUT | `/auth/me/password` | Customer | Change password |
| PUT | `/auth/me/email` | Customer | Change email |
| GET | `/auth/admin/users` | Admin | List all users |
| POST | `/auth/admin/create-user` | Admin | Create admin user |
| DELETE | `/auth/admin/users/{id}` | Admin | Deactivate a user |

### Account Service `/accounts`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/accounts` | Customer | Create a new account |
| GET | `/accounts/my` | Customer | Get own accounts |
| GET | `/accounts/my/{accountNumber}` | Customer | Get own account by account number |
| GET | `/accounts/balance/{accountNumber}` | Customer | Get account balance |
| PUT | `/accounts/{id}/close` | Customer | Close own account |
| GET | `/accounts` | Admin | Get all accounts |
| GET | `/accounts/{id}` | Admin | Get account by ID |
| PUT | `/accounts/admin/{id}/freeze` | Admin | Freeze an account |

### Transaction Service `/transactions`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/transactions/deposit` | Customer | Deposit funds |
| POST | `/transactions/withdraw` | Customer | Withdraw funds |
| POST | `/transactions/transfer` | Customer | Transfer between accounts |
| GET | `/transactions/my` | Customer | Get own transactions |
| GET | `/transactions/my?type=DEPOSIT` | Customer | Filter transactions by type |
| GET | `/transactions/{transactionId}` | Customer/Admin | Get transaction by reference |
| GET | `/transactions` | Admin | Get all transactions |
| GET | `/transactions/account/{accountId}` | Admin | Get transactions by account |

---

## Kafka Topics

| Topic | Producer | Consumer | Events |
|---|---|---|---|
| `transaction-events` | Transaction Service | Notification Service | DEPOSIT, WITHDRAW, TRANSFER |
| `auth-events` | Auth Service | Notification Service | PASSWORD_CHANGED, EMAIL_CHANGED |
| `account-events` | Account Service | Notification Service | ACCOUNT_CLOSED, ACCOUNT_FROZEN |

---

## SMS Notification Simulation

The notification service simulates SMS alerts by writing clean formatted messages to a dedicated log file at `logs/notification-sms.log`.

Example output:
```
25-01-2025 14:32:01 | [SMS] Account ACC-1A2B3C4D: A deposit of £500.00 has been credited to your account. Ref: abc-123
25-01-2025 14:35:10 | [SMS] Hi john, your account password was recently changed. If this wasn't you, contact support immediately.
25-01-2025 14:40:22 | [SMS] Account ACC-1A2B3C4D has been frozen. Please contact support for further assistance.
```

To monitor live:
```bash
# Windows PowerShell
Get-Content logs/notification-sms.log -Wait
```

---

## Account Status

Accounts have three possible statuses:

| Status | Description | Transactions Allowed |
|---|---|---|
| `ACTIVE` | Normal operating account | Yes |
| `FROZEN` | Frozen by admin | No |
| `CLOSED` | Closed by customer | No |

- Customers can see `ACTIVE` and `FROZEN` accounts but not `CLOSED` ones
- Admins can see all accounts with their status
- Any transaction attempted on a `FROZEN` or `CLOSED` account will be rejected with an appropriate error message

---

## Security

- All endpoints are secured with JWT Bearer token authentication
- Tokens are issued on login and must be included in the `Authorization` header
- Role-based access control — `CUSTOMER` and `ADMIN` roles
- Passwords are hashed using BCrypt
- Internal service endpoints are not exposed through the API Gateway

---

## Project Structure

```
banking/
├── account/          # Account microservice
├── apigateway/       # API Gateway
├── auth/             # Authentication microservice
├── eureka/           # Service discovery server
├── frontend/         # React frontend
├── notification/     # Notification microservice (Kafka consumer)
├── transaction/      # Transaction microservice
└── logs/             # Centralised log output (gitignored)
```
