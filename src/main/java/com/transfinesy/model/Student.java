package com.transfinesy.model;

/**
 * Represents a student in the transFINESy system.
 * 
 * This class models a student entity with all relevant information including
 * personal details (ID, name), academic information (course, year level, section),
 * and RFID tag for automated attendance tracking.
 * 
 * Key Features:
 * - Unique student ID (studID) serves as primary identifier
 * - Full name is composed of firstName and lastName
 * - Academic classification via course, yearLevel, and section
 * - Optional RFID tag for automated check-in/check-out
 * 
 * Usage:
 * - Used throughout the system for student management
 * - Referenced in attendance, fines, payments, and ledger records
 * - Supports filtering and searching by various criteria
 * 
 * @author transFINESy Development Team
 */
public class Student {
    private String studID;
    private String firstName;
    private String lastName;
    private String course;           // e.g., BSIT, BSED
    private String yearLevel;         // e.g., 1st, 2nd, 3rd, 4th
    private String section;          // e.g., A, B
    private String rfidTag;          // unique RFID assigned to the student

    public Student() {
    }

    public Student(String studID, String firstName, String lastName, String course, String yearLevel, String section) {
        this.studID = studID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
    }

    public Student(String studID, String firstName, String lastName, String course, String yearLevel, String section, String rfidTag) {
        this.studID = studID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
        this.rfidTag = rfidTag;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String rfidTag) {
        this.rfidTag = rfidTag;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studID='" + studID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", course='" + course + '\'' +
                ", yearLevel='" + yearLevel + '\'' +
                ", section='" + section + '\'' +
                ", rfidTag='" + rfidTag + '\'' +
                '}';
    }
}








