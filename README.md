# Personal Finance Manager

A Personal Finance Manager REST API built for the Syfe Backend Intern assignment. This application provides secure transaction tracking, budget categorization, and savings goal management.

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

## 🏗️ Architecture

The application uses a simple layered structure. Each layer has one main responsibility.

```text
┌──────────────────────────────┐
│        Client / Postman      │
└──────────────┬───────────────┘
               │ HTTP Request
               ▼
┌──────────────────────────────┐
│         Controller           │
│ Receives requests and sends  │
│ HTTP responses               │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           Service            │
│ Contains business logic and  │
│ validation rules             │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Repository           │
│ Performs database operations │
│ using Spring Data JPA        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│        H2 Database           │
│        In-memory DB          │
└──────────────────────────────┘
```

## 🛠️ Technology Stack

| Technology | Why it is used |
|---|---|
| Java 17 | Main programming language |
| Spring Boot | Builds the REST API and runs the application |
| Spring Security | Handles login, sessions, and protected endpoints |
| Spring Data JPA | Simplifies database operations |
| Hibernate | Maps Java entities to database tables |
| H2 | In-memory database for storing application data |
| Maven | Builds the project and manages dependencies |
| JUnit 5 | Automated testing |
| Mockito | Mocking dependencies in unit tests |
| MockMvc | Testing REST API endpoints |
| JaCoCo | Measures test coverage |
| Docker | Packages the application for deployment |
| Render | Hosts the deployed application |

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

## 🧪 Testing and Coverage

The project uses automated tests to verify application behavior without manual checks.

**What is tested:**
- User authentication and data isolation
- Input validation and exception handling
- Transaction and category management
- Savings goal calculations
- Monthly and yearly report generation

**Run local unit tests:**
```bash
./mvnw clean verify
```

**Run end-to-end evaluator script:**
```bash
bash financial_manager_tests.sh https://personal-finance-manager-nw2c.onrender.com/api
```

## Deployment

The application is deployed on Render.

**Live API:**
https://personal-finance-manager-nw2c.onrender.com

