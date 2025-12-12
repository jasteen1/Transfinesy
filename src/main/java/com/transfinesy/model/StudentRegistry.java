package com.transfinesy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry that maintains a list of all students.
 * 
 * This class provides a centralized collection of all students in the system.
 * It is used for dashboards, clearance operations, and bulk operations.
 * 
 * Key Features:
 * - Maintains a list of all students
 * - Provides methods to filter students (by clearance eligibility, balance)
 * - Supports adding and removing students
 * 
 * Usage:
 * - Used in dashboard to get student counts
 * - Used in clearance operations to list students
 * - Note: Actual implementation may delegate to StudentService
 * 
 * @author transFINESy Development Team
 */
public class StudentRegistry {
    private List<Student> students;

    public StudentRegistry() {
        this.students = new ArrayList<>();
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public List<Student> getStudentsEligibleForClearance(ClearanceService clearanceService) {
        // This will be implemented using LedgerService in the service layer
        // For now, return empty list - actual implementation needs service reference
        return new ArrayList<>();
    }

    public List<Student> getStudentsWithBalance(ClearanceService clearanceService) {
        // This will be implemented using LedgerService in the service layer
        // For now, return empty list - actual implementation needs service reference
        return new ArrayList<>();
    }

    public void addStudent(Student s) {
        if (s != null && !students.contains(s)) {
            students.add(s);
        }
    }

    public void removeStudent(String studID) {
        students.removeIf(s -> s.getStudID().equals(studID));
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
    }
}

