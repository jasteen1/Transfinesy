-- transFINESy Database Schema
-- Run this script to create the database and tables

CREATE DATABASE IF NOT EXISTS transfinesy;
USE transfinesy;

-- Students table
CREATE TABLE IF NOT EXISTS students (
    stud_id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    year_level VARCHAR(20) NOT NULL,
    section VARCHAR(10) NOT NULL,
    rfid_tag VARCHAR(50) UNIQUE
);

-- Events table
CREATE TABLE IF NOT EXISTS events (
    event_id VARCHAR(50) PRIMARY KEY,
    event_name VARCHAR(200) NOT NULL,
    event_date DATE NOT NULL,
    semester INT NOT NULL,
    school_year VARCHAR(9) NOT NULL,
    am_time_in TIME NULL,
    am_time_out TIME NULL,
    pm_time_in TIME NULL,
    pm_time_out TIME NULL
);

-- Attendance table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id VARCHAR(50) PRIMARY KEY,
    stud_id VARCHAR(50) NOT NULL,
    event_id VARCHAR(50) NOT NULL,
    status ENUM('PRESENT', 'LATE', 'ABSENT', 'EXCUSED') NOT NULL,
    minutes_late INT DEFAULT 0,
    check_in_time DATETIME NULL,
    check_out_time DATETIME NULL,
    scan_source VARCHAR(20) DEFAULT 'MANUAL',
    FOREIGN KEY (stud_id) REFERENCES students(stud_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

-- Fines table
CREATE TABLE IF NOT EXISTS fines (
    fine_id VARCHAR(50) PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    stud_id VARCHAR(50) NOT NULL,
    event_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (stud_id) REFERENCES students(stud_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(50) PRIMARY KEY,
    transaction_id VARCHAR(50) UNIQUE NOT NULL,
    stud_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    or_number VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (stud_id) REFERENCES students(stud_id) ON DELETE CASCADE
);

-- Community Service table
CREATE TABLE IF NOT EXISTS community_service (
    service_id VARCHAR(50) PRIMARY KEY,
    stud_id VARCHAR(50) NOT NULL,
    hours_rendered INT NOT NULL,
    credit_amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (stud_id) REFERENCES students(stud_id) ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX idx_attendance_stud ON attendance(stud_id);
CREATE INDEX idx_attendance_event ON attendance(event_id);
CREATE INDEX idx_fines_stud ON fines(stud_id);
CREATE INDEX idx_fines_event ON fines(event_id);
CREATE INDEX idx_payments_stud ON payments(stud_id);
CREATE INDEX idx_community_service_stud ON community_service(stud_id);
CREATE INDEX idx_students_course ON students(course);
CREATE INDEX idx_students_year_section ON students(year_level, section);
