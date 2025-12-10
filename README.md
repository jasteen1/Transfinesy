# transFINESy - Transforming Fines into Transparency

A Spring Boot web application for managing student fines, payments, attendance, and clearance. Originally built as a Java Swing desktop application, now converted to a modern web interface using Spring Boot + Thymeleaf.

## Requirements

- Java 17 or higher
- Maven 3.6+
- XAMPP (includes MySQL/MariaDB) or standalone MySQL 8.0+

## Setup

### 1. Start XAMPP MySQL

1. Open XAMPP Control Panel
2. Click "Start" next to MySQL
3. Wait until MySQL status shows as "Running"

### 2. Database Setup

**Option A: Using the setup script (Recommended)**
```bash
./setup-xampp-database.sh
```

**Option B: Manual setup**

1. Open phpMyAdmin (http://localhost/phpMyAdmin) or use MySQL command line
2. Create the database:
```sql
CREATE DATABASE transfinesy;
```

3. Run the schema script:
```bash
# Using XAMPP MySQL (macOS)
/Applications/XAMPP/xamppfiles/bin/mysql -u root transfinesy < src/main/resources/schema.sql

# Or using system MySQL client (if XAMPP MySQL is in PATH)
mysql -u root transfinesy < src/main/resources/schema.sql
```

Or execute the SQL commands from `src/main/resources/schema.sql` in phpMyAdmin.

### 3. Database Configuration

The default configuration works with XAMPP's default settings:
- Host: localhost
- Port: 3306
- User: root
- Password: (empty - XAMPP default)

If you've set a password for MySQL root user, edit `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/transfinesy
db.user=root
db.password=your_password_here
```

**Note:** If the properties file is not found, the application will use XAMPP default values (localhost, root, empty password).

### 3. Database Setup (Complete)

Run the complete database setup script to create all tables and columns:
```bash
# Using XAMPP MySQL (macOS)
/Applications/XAMPP/xamppfiles/bin/mysql -u root < COMPLETE_DATABASE_SETUP.sql

# Or using system MySQL client
mysql -u root < COMPLETE_DATABASE_SETUP.sql
```

Or execute the SQL commands from `COMPLETE_DATABASE_SETUP.sql` in phpMyAdmin.

### 4. Build and Run

Build the project:
```bash
mvn clean compile
```

Run the Spring Boot application:
```bash
mvn spring-boot:run
```

The application will be available at: http://localhost:8080

Or package and run:
```bash
mvn clean package
java -jar target/transfinesy-1.0.0.jar
```

## Project Structure

```
src/main/java/com/transfinesy/
├── Main.java                    # Application entry point
├── config/
│   └── DBConfig.java           # Database configuration
├── model/                       # Domain models
│   ├── Student.java
│   ├── Event.java
│   ├── Attendance.java
│   ├── Fine.java
│   ├── Payment.java
│   ├── CommunityService.java
│   ├── Transaction.java
│   ├── Ledger.java
│   └── ...
├── repo/                        # Repository interfaces and JDBC implementations
│   ├── StudentRepository.java
│   ├── StudentRepositoryImpl.java
│   └── ...
├── service/                     # Business logic layer
│   ├── StudentService.java
│   ├── AttendanceService.java
│   ├── FineService.java
│   └── ...
└── web/                         # Spring MVC Controllers
    ├── HomeController.java
    ├── StudentController.java
    ├── EventController.java
    ├── AttendanceController.java
    └── ...
src/main/resources/
├── templates/                   # Thymeleaf HTML templates
│   ├── layout.html
│   ├── index.html
│   ├── students/
│   ├── events/
│   └── ...
└── static/                     # Static resources (CSS, JS, images)
    ├── css/
    ├── js/
    └── images/
```

## Features

- **Student Management**: Add, edit, delete, and search students
- **Event Management**: Create and manage events
- **Attendance Tracking**: Record attendance for events with status (PRESENT, LATE, ABSENT, EXCUSED)
- **Fine Generation**: Automatic fine calculation based on attendance
  - ABSENT: Base fine (₱100)
  - LATE: Per-minute fine (₱2/minute, minimum ₱20)
- **Payment Recording**: Record payments with OR numbers
- **Community Service**: Record service hours (1 hour = ₱50 credit)
- **Ledger View**: View complete transaction history per student
- **Clearance Management**: Check clearance status (CLEARED/WITH BALANCE)
- **Transparency Dashboard**: View collections, fines, and breakdowns by course

## Architecture

The application follows a layered architecture:

- **Model Layer**: Domain entities and business objects
- **Repository Layer**: Data access using JDBC with connection pooling (HikariCP)
- **Service Layer**: Business logic and orchestration
- **Controller Layer**: Spring MVC controllers for web requests
- **View Layer**: Thymeleaf templates with Bootstrap 5 for responsive UI

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: MySQL 8.0+ (via XAMPP)
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Build Tool**: Maven
- **Connection Pooling**: HikariCP

## Notes

- The application uses a modern, minimalistic web UI inspired by Stripe Dashboard and Linear.app
- All database operations use connection pooling for optimal performance
- Error handling is done via custom error pages and flash messages
- Transaction IDs are auto-generated using UUID
- Static resources are cached and compressed for faster page loads

## License

This project is provided as-is for educational and development purposes.

