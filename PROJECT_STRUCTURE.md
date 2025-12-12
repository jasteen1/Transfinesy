# transFINESy Project Structure Documentation

## Overview
transFINESy is a Spring Boot web application for managing student fines, attendance, payments, and community service records. This document explains the purpose of each folder and file in the project.

---

## 📁 Root Directory Structure

### Configuration Files
- **`pom.xml`** - Maven project configuration file. Defines project dependencies (Spring Boot, Thymeleaf, MySQL Connector, etc.), build settings, and project metadata.
- **`schema.sql`** - Complete database schema file. Contains all table definitions, indexes, constraints, and sample data for the MySQL database.
- **`README.md`** - Project overview and setup instructions.
- **`TECNICALDOC.md`** - Technical documentation detailing system architecture, features, and implementation details.

### Documentation Files
- **`EVENT_ATTENDANCE_FIXES.md`** - Documentation of fixes applied to event attendance functionality.
- **`IMPLEMENTATION_COMPLETE.md`** - Summary of completed implementation tasks.
- **`GITHUB_UPLOAD_INSTRUCTIONS.md`** - Instructions for uploading files to GitHub.

### Resources
- **`logo.png`** - Application logo image file.

---

## 📁 `src/main/java/com/transfinesy/` - Main Java Source Code

### **`TransfinesyApplication.java`**
- **Purpose**: Spring Boot application entry point. Contains the `main()` method that starts the application.
- **Function**: Initializes Spring Boot framework and starts the embedded web server.

---

## 📁 `config/` - Configuration Classes

### **`DBConfig.java`**
- **Purpose**: Database configuration and connection management.
- **Function**: 
  - Manages database connection pooling for performance optimization
  - Provides static method `getConnection()` for backward compatibility
  - Uses Spring's DataSource for connection reuse instead of creating new connections each time

### **`PerformanceConfig.java`**
- **Purpose**: Performance optimization configuration.
- **Function**: Configures static resource caching (CSS, JS, images) for 1 year to improve page load times.

---

## 📁 `model/` - Data Model Classes (Domain Objects)

These classes represent the core business entities in the system.

### **`Student.java`**
- **Purpose**: Represents a student in the system.
- **Fields**: Student ID, first name, last name, course, year level, section, RFID tag
- **Function**: Data model for student information with helper method `getFullName()`

### **`Event.java`**
- **Purpose**: Represents a school event where attendance is checked.
- **Fields**: Event ID, name, date, semester, school year, AM/PM time windows, fine amounts
- **Function**: Manages event details including time-in/time-out windows for morning and afternoon sessions

### **`Attendance.java`**
- **Purpose**: Represents an attendance record for a student at an event.
- **Fields**: Attendance ID, student ID, event ID, status (PRESENT/LATE/ABSENT/EXCUSED), check-in/out times, session (AM/PM), record type (TIME_IN/TIME_OUT)
- **Function**: Tracks individual attendance records with support for AM/PM sessions and separate time-in/time-out records

### **`Fine.java`**
- **Purpose**: Represents a fine transaction (extends Transaction).
- **Fields**: Fine ID, transaction ID, student ID, event ID, fine amount, date
- **Function**: Models fines issued to students, with positive signed amount for ledger calculations

### **`Payment.java`**
- **Purpose**: Represents a payment transaction (extends Transaction).
- **Fields**: Payment ID, transaction ID, student ID, amount, OR number, date
- **Function**: Models payments made by students, with negative signed amount for ledger calculations

### **`CommunityService.java`**
- **Purpose**: Represents a community service record that can be converted to credit.
- **Fields**: Service ID, student ID, hours rendered, credit amount, date, description
- **Function**: Tracks community service hours and their monetary credit value

### **`Ledger.java`**
- **Purpose**: Represents the financial ledger for a single student.
- **Fields**: Student ID, list of transactions, opening/closing balance, totals (fines, payments, credits)
- **Function**: Aggregates all financial transactions and calculates student balance

### **`Transaction.java`**
- **Purpose**: Abstract base class for all financial transactions.
- **Fields**: Transaction ID, student ID, amount, date
- **Function**: Base class for Fine and Payment, provides common transaction structure

### **`AttendanceStatus.java`**
- **Purpose**: Enumeration for attendance status values.
- **Values**: PRESENT, LATE, ABSENT, EXCUSED
- **Function**: Type-safe enumeration for attendance status

### **`AttendanceSession.java`**
- **Purpose**: Enum representing attendance session type for events.
- **Values**: MORNING_ONLY, AFTERNOON_ONLY, BOTH
- **Function**: Defines which sessions an event supports

### **`ClearanceService.java`**
- **Purpose**: Model class for checking clearance eligibility.
- **Function**: Provides methods to check if a student is eligible for clearance based on ledger balance

### **`StudentRegistry.java`**
- **Purpose**: Registry that maintains a list of all students.
- **Function**: Manages collection of students for dashboards and clearance operations

---

## 📁 `repo/` - Repository Layer (Data Access)

Repository pattern implementation for database operations. Each entity has an interface and JDBC implementation.

### **`StudentRepository.java`** & **`StudentRepositoryImpl.java`**
- **Purpose**: Data access layer for Student entities.
- **Functions**: 
  - CRUD operations (create, read, update, delete students)
  - Search by name (supports full name search)
  - Get distinct courses, year levels, sections for filtering
  - Find by RFID tag

### **`EventRepository.java`** & **`EventRepositoryImpl.java`**
- **Purpose**: Data access layer for Event entities.
- **Functions**: 
  - CRUD operations for events
  - Find events by date range, semester, school year
  - Get all events

### **`AttendanceRepository.java`** & **`AttendanceRepositoryImpl.java`**
- **Purpose**: Data access layer for Attendance entities.
- **Functions**: 
  - Save/update attendance records
  - Find attendance by student, event, session, record type
  - Count attendance by status (with filtering)
  - Get attendance records for an event

### **`FineRepository.java`** & **`FineRepositoryImpl.java`**
- **Purpose**: Data access layer for Fine entities.
- **Functions**: 
  - CRUD operations for fines
  - Get fines by student, event, date range
  - Calculate total fines (with filtering)

### **`PaymentRepository.java`** & **`PaymentRepositoryImpl.java`**
- **Purpose**: Data access layer for Payment entities.
- **Functions**: 
  - CRUD operations for payments
  - Get payments by student, date range
  - Calculate total payments (with filtering)

### **`CommunityServiceRepository.java`** & **`CommunityServiceRepositoryImpl.java`**
- **Purpose**: Data access layer for CommunityService entities.
- **Functions**: 
  - CRUD operations for community service records
  - Get service records by student, date range
  - Calculate total hours and credits (with filtering)

---

## 📁 `service/` - Service Layer (Business Logic)

Service classes contain business logic and coordinate between controllers and repositories.

### **`StudentService.java`**
- **Purpose**: Business logic for student management.
- **Functions**: 
  - Student CRUD operations
  - Search students by various criteria
  - Get distinct values for filtering (courses, year levels, sections)

### **`EventService.java`**
- **Purpose**: Business logic for event management.
- **Functions**: 
  - Event CRUD operations
  - Event validation (date validation, time window validation)
  - Get events by various criteria

### **`AttendanceService.java`**
- **Purpose**: Business logic for attendance management.
- **Functions**: 
  - Check-in/check-out students (with time window validation)
  - Handle AM/PM session separation
  - Count attendance by status (with filtering)
  - Manage attendance records for events

### **`FineService.java`**
- **Purpose**: Business logic for fine management.
- **Functions**: 
  - Create fines for absent/late students
  - Calculate fine amounts based on event settings
  - Get fines by various criteria

### **`PaymentService.java`**
- **Purpose**: Business logic for payment management.
- **Functions**: 
  - Process payments
  - Validate payment data
  - Get payments by various criteria

### **`CommunityServiceService.java`**
- **Purpose**: Business logic for community service management.
- **Functions**: 
  - Record community service hours
  - Convert hours to credit amount (e.g., 1 hour = ₱50)
  - Get service records by various criteria

### **`LedgerService.java`**
- **Purpose**: Business logic for student ledger/financial records.
- **Functions**: 
  - Build complete ledger for a student (fines, payments, credits)
  - Calculate student balance
  - Get transaction history

### **`ReportService.java`**
- **Purpose**: Service for generating reports and dashboard statistics.
- **Functions**: 
  - Calculate totals (fines, payments, service credits)
  - Get monthly/semester collections
  - Calculate outstanding balance
  - Generate filtered statistics for dashboard

### **`ClearanceService.java`**
- **Purpose**: Business logic for student clearance operations.
- **Functions**: 
  - Check clearance eligibility
  - Generate clearance reports
  - Determine clearance status

### **`RFIDService.java`**
- **Purpose**: Business logic for RFID tag management.
- **Functions**: 
  - Handle RFID scanning
  - Map RFID tags to students
  - Process RFID-based check-ins

---

## 📁 `web/` - Web Layer (Controllers)

Spring MVC controllers that handle HTTP requests and prepare data for views.

### **`HomeController.java`**
- **Purpose**: Handles home page and root URL requests.
- **Routes**: `/`, `/home`
- **Function**: Displays the main landing page

### **`DashboardController.java`**
- **Purpose**: Handles dashboard page requests.
- **Routes**: `/dashboard`
- **Function**: 
  - Displays financial summary (total fines, payments, credits, outstanding balance)
  - Displays attendance summary (present, late, absent counts)
  - Displays community service summary (total hours, credits, student count)
  - Handles independent filters for each section

### **`StudentController.java`**
- **Purpose**: Handles student management requests.
- **Routes**: `/students/*`
- **Functions**: 
  - List all students
  - Create/edit student forms
  - Save/update students
  - Search students
  - Delete students

### **`EventController.java`**
- **Purpose**: Handles event management requests.
- **Routes**: `/events/*`
- **Functions**: 
  - List all events
  - Create/edit event forms
  - Save/update events (with date validation)
  - Delete events

### **`AttendanceController.java`**
- **Purpose**: Handles attendance recording and display.
- **Routes**: `/attendance/*`
- **Functions**: 
  - Display event attendance list
  - Check-in/check-out students
  - Handle RFID scanning
  - Show AM/PM attendance separately

### **`PaymentController.java`**
- **Purpose**: Handles payment management requests.
- **Routes**: `/payments/*`
- **Functions**: 
  - List all payments
  - Create new payments
  - Process payment transactions

### **`LedgerController.java`**
- **Purpose**: Handles student ledger view requests.
- **Routes**: `/ledger/*`
- **Functions**: 
  - Display student ledger (all transactions)
  - Show balance calculations
  - Filter transactions by date

### **`CommunityServiceController.java`**
- **Purpose**: Handles community service management requests.
- **Routes**: `/community-service/*`
- **Functions**: 
  - List community service records
  - Create new service records
  - Convert hours to credits

### **`ClearanceController.java`**
- **Purpose**: Handles clearance view requests.
- **Routes**: `/clearance/*`
- **Functions**: 
  - Display clearance status for students
  - Show eligible/not eligible students
  - Generate clearance reports

### **`TransparencyReportController.java`**
- **Purpose**: Handles transparency report requests.
- **Routes**: `/reports/*`
- **Functions**: 
  - Generate financial transparency reports
  - Display collection summaries
  - Export report data

### **`CustomErrorController.java`**
- **Purpose**: Handles error pages.
- **Routes**: `/error`
- **Function**: Displays custom error pages for various HTTP error codes

---

## 📁 `util/` - Utility Classes

Helper classes for common operations.

### **`CSVImporter.java`**
- **Purpose**: Utility for importing student data from CSV files.
- **Function**: Parses CSV files and creates student records

### **`BulkImport.java`**
- **Purpose**: Utility for bulk importing data.
- **Function**: Handles batch operations for importing multiple records

### **`Queue.java`**
- **Purpose**: Custom queue data structure implementation.
- **Function**: FIFO (First In First Out) queue for processing operations

### **`Stack.java`**
- **Purpose**: Custom stack data structure implementation.
- **Function**: LIFO (Last In First Out) stack for processing operations

---

## 📁 `src/main/resources/` - Resources Directory

### **Configuration Files**
- **`application.properties`** - Spring Boot application configuration (server port, database connection, Thymeleaf settings)
- **`db.properties`** - Database connection properties (legacy, may be unused if using application.properties)

### **Static Resources** (`static/`)
- **`css/design-system.css`** - Design system CSS with color variables, typography, spacing
- **`css/main.css`** - Main application styles
- **`js/main.js`** - Main JavaScript file for client-side functionality
- **`images/logo.png`** - Application logo

### **Templates** (`templates/`) - Thymeleaf HTML Templates
- **`layout.html`** - Base layout template with navigation, header, footer
- **`index.html`** - Home page template
- **`error.html`** - Error page template
- **`dashboard/index.html`** - Dashboard page with financial, attendance, and service summaries
- **`students/list.html`** - Student list page
- **`students/form.html`** - Student create/edit form
- **`events/list.html`** - Event list page
- **`events/form.html`** - Event create/edit form
- **`attendance/event.html`** - Event attendance page with AM/PM sections
- **`payments/list.html`** - Payment list page
- **`ledger/view.html`** - Student ledger view
- **`community-service/list.html`** - Community service list page
- **`clearance/view.html`** - Clearance view page
- **`reports/view.html`** - Transparency report view

---

## 📁 `target/` - Build Output Directory

- **Purpose**: Contains compiled classes, resources, and build artifacts generated by Maven.
- **Note**: This directory is typically excluded from version control (via `.gitignore`)

---

## Architecture Overview

### **Layered Architecture**
1. **Web Layer (Controllers)**: Handle HTTP requests, prepare model data, return view names
2. **Service Layer**: Contains business logic, coordinates between controllers and repositories
3. **Repository Layer**: Data access operations, SQL queries, database interactions
4. **Model Layer**: Domain objects representing business entities

### **Design Patterns Used**
- **Repository Pattern**: Separates data access logic from business logic
- **Service Layer Pattern**: Encapsulates business logic
- **MVC Pattern**: Model-View-Controller for web layer
- **Dependency Injection**: Spring's IoC container manages dependencies

### **Database**
- **Database**: MySQL/MariaDB
- **Connection**: JDBC with connection pooling (via Spring DataSource)
- **Schema**: Defined in `schema.sql`

---

## Key Features

1. **Student Management**: CRUD operations for student records
2. **Event Management**: Create and manage school events
3. **Attendance Tracking**: Record attendance with AM/PM session support
4. **Fine Management**: Automatically generate fines for absences/lateness
5. **Payment Processing**: Record and track student payments
6. **Community Service**: Track service hours and convert to credits
7. **Ledger System**: Complete financial ledger for each student
8. **Dashboard**: Real-time statistics and summaries
9. **Clearance System**: Check student eligibility for clearance
10. **Transparency Reports**: Financial transparency and reporting

---

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Template Engine**: Thymeleaf
- **Database**: MySQL/MariaDB
- **Build Tool**: Maven
- **Java Version**: Java 17+
- **Web Server**: Embedded Tomcat (via Spring Boot)

---

*Last Updated: 2025*

