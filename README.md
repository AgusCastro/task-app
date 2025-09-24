# Tasks app

## Description

This is a REST API for a task app that allows users to CRUD tasks. By default tasks are created to a public tenant, use the tenantId field to create tasks for a specific tenant.
Is built with Java 21, Spring Boot, Spring Data JPA, H2 Database and Gradle.

## Requirements

* Java 21 - It is recommended to use [sdkman](https://sdkman.io/) to manage the Java version.

## Setup

### 1. Install Java 21 

### 2. Clone the Repository

```bash
git clone <repository-url>
cd task-app
```

## Running the Application

### Option 1: Using Gradle Wrapper (Recommended)

The project includes a Gradle wrapper, so you don't need to install Gradle separately.

```bash
# Navigate to the project directory
cd task-app

# Make the gradlew script executable (on macOS/Linux)
chmod +x gradlew

# Run the application
./gradlew bootRun
```

### Option 2: Using Gradle (if installed)

```bash
gradle bootRun
```

## Application Details

- **Server Port**: 8080 (default Spring Boot port)
- **Database**: H2 in-memory database
- **Base URL**: `http://localhost:8080/api/v1`

## API Endpoints

The application provides the following REST endpoints:

### Tasks API (`/tasks`)

- Header:
  - `X-Tenant-ID` (string, optional) - Specify the tenant ID for multi-tenancy (default: public)

- **GET** `/tasks` - Get all tasks (paginated)
  - Query parameters:
    - `page` - Page number (default: 0)
    - `size` - Page size (default: 20)
    - `sort` - Sort criteria (default: createdAt)
    - `status` - Filter by task status (optional)

- **GET** `/tasks/{id}` - Get task by ID

- **POST** `/tasks` - Create a new task
  - Body parameters:
    - `title` (string, required, max 100 characters)
    - `description` (string, optional)

- **PUT** `/tasks/{id}` - Update an existing task
  - Body parameters:
    - `title` (string, required, max 100 characters)
    - `description` (string, optional)
    - `status` (enum: PENDING, IN_PROGRESS, COMPLETED)

- **DELETE** `/tasks/{id}` - Delete a task

## Testing

### Run All Tests

```bash
./gradlew test
```
