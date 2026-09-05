# Personal Finance Manager

A Personal Finance Manager REST API built for the Syfe Backend Intern assignment. This application provides secure transaction tracking, budget categorization, and savings goal management.

## Live API

**Live Application:**
https://personal-finance-manager-nw2c.onrender.com

**API Base URL:**
https://personal-finance-manager-nw2c.onrender.com/api

## Features

- User registration
- Login/logout
- Session-based authentication
- Transaction creation, retrieval, update and deletion
- Transaction filtering
- Default and custom categories
- Savings goals and progress calculation
- Monthly and yearly reports
- Input validation
- Global exception handling
- User-data isolation

## Architecture

The application is built as a simple layered monolith.

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
H2 Database
```

- **Controller** → handles HTTP requests/responses
- **Service** → contains business logic
- **Repository** → handles database access
- **Entity** → represents database data
- **DTO** → represents API request/response data
- **Security** → handles authentication/session
- **Exception** → handles API errors

## Technology Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- H2
- Maven
- JUnit 5
- Mockito
- MockMvc
- JaCoCo
- Docker
- Render

## Authentication

- The application uses session-based authentication.
- Successful login creates an HTTP session.
- The client uses the `JSESSIONID` cookie for subsequent authenticated requests.
- Protected endpoints require an authenticated session.
- Logout invalidates the session.

## User Data Isolation

Authenticated users can access only their own transactions, goals and custom categories.

## Important Business Rules

- Transaction amount must be valid.
- Transaction date cannot be in the future.
- Transaction date cannot be changed through update.
- Duplicate categories are prevented.
- Default categories cannot be deleted.
- Categories in use cannot be deleted.
- Goal target date must satisfy validation rules.
- Goal progress is calculated from relevant income and expenses.
- Reports calculate income, expenses and net savings.
- Users cannot access another user's data.

## API Reference

| Category | Method | Endpoint |
|---|---|---|
| **Authentication** | POST | `/api/auth/register` |
| | POST | `/api/auth/login` |
| | POST | `/api/auth/logout` |
| **Categories** | GET | `/api/categories` |
| | POST | `/api/categories` |
| | DELETE | `/api/categories/{name}` |
| **Transactions** | GET | `/api/transactions` |
| | POST | `/api/transactions` |
| | PUT | `/api/transactions/{id}` |
| | DELETE | `/api/transactions/{id}` |
| **Goals** | GET | `/api/goals` |
| | GET | `/api/goals/{id}` |
| | POST | `/api/goals` |
| | PUT | `/api/goals/{id}` |
| | DELETE | `/api/goals/{id}` |
| **Reports** | GET | `/api/reports/monthly/{year}/{month}` |
| | GET | `/api/reports/yearly/{year}` |

## Project Structure

```text
src/main/java/com/syfe/pfm/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── security/
└── service/
```

## Database

The application uses H2 as an in-memory database. H2 is permitted by the assignment and keeps the project self-contained for evaluation. Data is not expected to persist after application restart.

## Run Locally

```bash
./mvnw clean verify
```

```bash
./mvnw spring-boot:run
```

*(On Windows, use `.\mvnw` instead of `./mvnw`)*

## Testing

The project contains automated tests covering:
- authentication
- validation
- transactions
- categories
- savings goals
- reports
- exception handling
- user-data isolation

```bash
./mvnw clean verify
```

The project is also validated against the provided end-to-end evaluator script:
```bash
bash financial_manager_tests.sh https://personal-finance-manager-nw2c.onrender.com/api
```

## Deployment

The application is deployed on Render.

**Live API:**
https://personal-finance-manager-nw2c.onrender.com

## Assignment Links

**GitHub Repository:**
https://github.com/PMritunjay1/personal-finance-manager

**Live API:**
https://personal-finance-manager-nw2c.onrender.com

**E2E Test Script:**
financial_manager_tests.sh
