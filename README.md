# Sweet Management System

A comprehensive Spring Boot REST API for managing a sweet shop inventory system. This application provides authentication, authorization, and full CRUD operations for managing sweets, categories, and inventory.


## 🎯 Overview

Sweet Management System is a backend API built with Spring Boot that enables:
- User registration and authentication using JWT tokens
- Role-based access control (Admin and User roles)
- Complete CRUD operations for sweets
- Inventory management (purchase and restock)
- Category management
- Search and filtering capabilities
- Pagination support

## ✨ Features

### Authentication & Authorization
- User registration with email validation
- JWT-based authentication
- Role-based access control (ADMIN, USER)
- Secure password hashing using BCrypt

### Sweet Management
- Create, read, update, and delete sweets (Admin only)
- List all sweets with pagination
- Search sweets by name, category, or price range
- View sweet categories

### Inventory Management
- Purchase sweets (decreases quantity) - Available to authenticated users
- Restock sweets (increases quantity) - Admin only
- Automatic quantity validation

### Additional Features
- OpenAPI/Swagger documentation
- Global exception handling
- Standardized API responses
- CORS configuration for frontend integration
- Data initialization (admin user and categories)

## 🛠 Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.8
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: Database operations
- **PostgreSQL**: Database
- **Lombok**: Reducing boilerplate code
- **SpringDoc OpenAPI**: API documentation
- **Gradle**: Build tool
- **JUnit**: Testing framework

## 📁 Project Structure

```
src/main/java/com/api/mithai/
├── auth/        # Authentication (login, register, users, roles)
├── sweet/       # Sweet management (CRUD, categories, inventory, search)
├── base/        # Common configs, constants, exception handling, responses
└── security/    # JWT security, filters, and Spring Security configuration
```

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

1. **Java Development Kit (JDK) 21** or higher
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version`

2. **PostgreSQL Database** (version 12 or higher)
   - Download from [PostgreSQL Official Site](https://www.postgresql.org/download/)
   - Create a database for the application

3. **Gradle** (optional, wrapper included)
   - The project includes Gradle wrapper (`gradlew`), so you don't need to install Gradle separately
   - If you prefer, install Gradle from [Gradle Official Site](https://gradle.org/install/)

4. **IDE** (recommended)
   - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## 🚀 Setup Instructions

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd sweet-managment
```

### Step 2: Set Up PostgreSQL Database

1. **Create a PostgreSQL database:**
   ```sql
   CREATE DATABASE sweet_management;
   ```

2. **Note your database credentials:**
   - Database URL
   - Username
   - Password

### Step 3: Configure Environment Variables

Create a `.env` file in the root directory (or set environment variables):

```env
DATASOURCE_URL=jdbc:postgresql://localhost:5432/sweet_management
DATASOURCE_USERNAME=your_username
DATASOURCE_PASSWORD=your_password
```

**Note:** The application uses environment variables for database configuration. You can also modify `application.properties` directly, but using environment variables is recommended for security.

### Step 4: Build the Project

Using Gradle Wrapper (recommended):

**Windows:**
```bash
gradlew.bat build
```

**Linux/Mac:**
```bash
./gradlew build
```

Or using installed Gradle:
```bash
gradle build
```

### Step 5: Run the Application

**Using Gradle Wrapper:**

**Windows:**
```bash
gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

**Or using installed Gradle:**
```bash
gradle bootRun
```

**Or run directly from IDE:**
- Open the project in your IDE
- Run `SweetManagementApplication.java`

### Step 6: Verify the Application

1. The application will start on port **8081** (as configured in `application.properties`)
2. Access Swagger UI at: `http://localhost:8081/swagger-ui.html`
3. The database tables will be created automatically (Hibernate DDL auto-update)

## ⚙️ Configuration

### Application Properties

The main configuration file is located at `src/main/resources/application.properties`:

```

### Environment Variables

Set these environment variables before running:

- `DATASOURCE_URL`: PostgreSQL connection URL
- `DATASOURCE_USERNAME`: Database username
- `DATASOURCE_PASSWORD`: Database password

**Windows (PowerShell):**
```powershell
$env:DATASOURCE_URL="jdbc:postgresql://localhost:5432/sweet_management"
$env:DATASOURCE_USERNAME="your_username"
$env:DATASOURCE_PASSWORD="your_password"
```

**Linux/Mac:**
```bash
export DATASOURCE_URL="jdbc:postgresql://localhost:5432/sweet_management"
export DATASOURCE_USERNAME="your_username"
export DATASOURCE_PASSWORD="your_password"
```

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

### API Endpoints Overview

#### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

#### Sweets Management (`/api/sweets`)
- `POST /api/sweets` - Create a new sweet (Admin only)
- `GET /api/sweets` - List all sweets with pagination (Authenticated)
- `GET /api/sweets/search` - Search sweets (Authenticated)
- `PUT /api/sweets/{id}` - Update a sweet (Admin only)
- `DELETE /api/sweets/{id}` - Delete a sweet (Admin only)

#### Inventory Management (`/api/sweets`)
- `POST /api/sweets/{id}/purchase` - Purchase a sweet (Authenticated)
- `POST /api/sweets/{id}/restock` - Restock a sweet (Admin only)

#### Categories (`/api/sweets/category`)
- `GET /api/sweets/category` - Get all categories (Authenticated)

## 🔐 Authentication & Authorization

### User Roles

1. **ADMIN**: Full access to all endpoints
   - Create, update, delete sweets
   - Restock inventory
   - View all data

2. **USER**: Limited access
   - View sweets and categories
   - Purchase sweets
   - Cannot modify inventory

### Authentication Flow

1. **Register**: Create a new account
2. **Login**: Get JWT token
   Response includes `accessToken` in the response body.
3. **Use Token**: Include in Authorization header
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### Default Admin User

On first startup, the application automatically creates an admin user:
- **Email**: `himanitrivedi1874@gmail.com`
- **Password**: `Admin@123`
- **Role**: ADMIN

**⚠️ Important**: From register endpoint will be adding new users only not admin user

## 🗄️ Database Schema

### Users Table
- `id` (Long, Primary Key)
- `username` (String, Not Null)
- `emailId` (String, Unique, Not Null)
- `password` (String, Hashed, Not Null)
- `roleName` (Enum: USER, ADMIN)

### Sweets Table
- `id` (Long, Primary Key)
- `name` (String, Not Null)
- `category_id` (Foreign Key to SweetCategory)
- `price` (Double, Not Null)
- `quantity` (Integer, Not Null)

### Sweet Categories Table
- `id` (Long, Primary Key)
- `name` (String, Unique, Not Null)

### Initial Data

On startup, the application initializes:
- 1 Admin user (if not exists)
- 10 Sweet categories:
  - Milk Sweets
  - Dry Fruits Sweets
  - Traditional Sweets
  - Modern Sweets
  - Sugar-Free Sweets
  - Festival Special
  - Bengali Sweets
  - Gujarati Sweets
  - Rajasthani Sweets
  - South Indian Sweets

### Test Structure

Tests are located in `src/test/java/com/api/mithai/`:

- **Controller Tests**: Integration tests for API endpoints
- **Service Tests**: Unit tests for business logic
- **Domain Tests**: Entity validation tests


## 🌐 Frontend Integration

This backend API is designed to work with a React frontend. The frontend repository can be found at:
- **Frontend Repo**: [sweet-application](https://github.com/Himani-Trivedi/sweet-application/tree/master/mithai-application)

### CORS Configuration

The application is configured to allow requests from `http://localhost:5173` (default Vite port). To change this, update `app.frontend.allowed.origin` in `application.properties`.

## 📝 Additional Notes

### Password Requirements

Passwords are validated with the following rules:
- Minimum length requirements
- Must contain uppercase, lowercase, numbers, and special characters

### JWT Token Expiration

Default JWT token expiration is set to 4 hours (14400000 ms). This can be configured in `application.properties`.

### Logging

The application uses SLF4J for logging. Logs are output to the console by default.

## 👤 Author

**Himani Trivedi**
- Email: himanitrivedi1874@gmail.com

---

