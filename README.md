# Personal Finance Manager

A comprehensive personal finance management system built with Spring Boot, enabling users to track income, expenses, and savings goals.

## Architecture & Technology Stack
- **Architecture**: Layered Monolith (Controller -> Service -> Repository)
- **Java**: Java 17+
- **Framework**: Spring Boot 3.3.0
- **Database**: H2 (In-Memory Database)
- **Security**: Spring Security (Session-based authentication)
- **Validation**: Jakarta Validation API
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven

## Design Decisions
1. **Soft Deletion vs Hard Deletion**: Used hard deletion for transactions since deleted transactions do not need to appear in reports or goals, and they aren't referenced by foreign keys. Custom categories with associated transactions cannot be deleted.
2. **Data Isolation**: A `SecurityUtils` component enforces multi-tenancy. Every request extracts the authenticated user from `SecurityContext` and all database queries are explicitly filtered by `userId` to ensure data isolation.
3. **Session Based Auth**: Session context is maintained through HTTP cookies, adhering to traditional web app security patterns over JWT.
4. **Goals Tracking**: Goal progress isn't statically saved in the DB but is dynamically derived from actual transactions to prevent data staleness.

## Setup Instructions

1. **Prerequisites**
   - Java 17 or higher installed (`JAVA_HOME` configured)
   - Maven (optional, wrapper is included)

2. **How to run**
   Use the Maven wrapper to build and start the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

3. **Running Tests & Coverage**
   Run the tests and generate JaCoCo coverage report (minimum 80% coverage):
   ```bash
   ./mvnw clean verify
   ```
   *Coverage reports will be located at `target/site/jacoco/index.html`.*

## Database Structure
The application uses H2 In-Memory Database.
- Users table (`users`): Manages authentication details.
- Categories table (`categories`): Stores custom and predefined categories.
- Transactions table (`transactions`): Stores user incomes and expenses.
- Goals table (`goals`): Stores financial goals.

Predefined categories are seeded on startup automatically and protected from modification/deletion.

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate user & start session
- `POST /api/auth/logout` - Terminate user session

### Categories
- `GET /api/categories` - View accessible categories
- `POST /api/categories` - Create a custom category
- `DELETE /api/categories/{name}` - Delete a custom category

### Transactions
- `GET /api/transactions` - Filter & list transactions
- `POST /api/transactions` - Create transaction
- `PUT /api/transactions/{id}` - Update a transaction (except date)
- `DELETE /api/transactions/{id}` - Delete a transaction

### Goals
- `GET /api/goals` - List all goals
- `GET /api/goals/{id}` - View specific goal progress
- `POST /api/goals` - Create savings goal
- `PUT /api/goals/{id}` - Update target amount/date
- `DELETE /api/goals/{id}` - Remove a goal

### Reports
- `GET /api/reports/monthly/{year}/{month}` - Get monthly financial report
- `GET /api/reports/yearly/{year}` - Get yearly financial report

## Deployment Instructions (Render)

This project is fully ready for Render deployment.

1. **Create Web Service** on Render.
2. Link your GitHub repository.
3. Configure settings:
   - **Environment**: Java
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/personal-finance-manager-0.0.1-SNAPSHOT.jar`
4. The application binds automatically to Render's `PORT` environment variable via `server.port=${PORT:8080}` in `application.properties`.

*No deployment tools/credentials were provided in the environment, so deployment is ready to be handled manually via Render dashboard.*
