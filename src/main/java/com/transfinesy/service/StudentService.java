package com.transfinesy.service;

import com.transfinesy.model.Student;
import com.transfinesy.repo.StudentRepository;
import com.transfinesy.repo.StudentRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Student business logic operations.
 * 
 * This class encapsulates all business logic related to student management.
 * It acts as an intermediary between controllers and repositories, providing
 * validation, data transformation, and business rule enforcement.
 * 
 * Key Responsibilities:
 * - Student CRUD operations with validation
 * - Student search with multiple criteria
 * - Data validation (ID format, year level, course, name format)
 * - Sorting and filtering operations
 * - Retrieving distinct values for filters
 * 
 * Validation Rules:
 * - Student ID: Must match pattern YYYYMXXXX (e.g., 2024M0001)
 * - Year Level: Must be 1, 2, 3, or 4
 * - Course: Letters only
 * - First/Last Name: Letters and spaces only
 * - Section: Single letter (A-Z)
 * 
 * Search Features:
 * - Search by ID, name, course, year level, section, RFID
 * - Full name search (handles "firstname lastname" and "lastname firstname")
 * - Partial matching for RFID tags
 * 
 * @author transFINESy Development Team
 */
@Service
public class StudentService {
    private StudentRepository repository;

    public StudentService() {
        this.repository = new StudentRepositoryImpl();
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Student getStudentById(String studID) {
        return repository.findById(studID);
    }

    public void addStudent(Student student) {
        validateStudent(student);
        repository.save(student);
    }
    
    public void updateStudent(Student student) {
        if (student == null || student.getStudID() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        validateStudent(student);
        repository.update(student);
    }
    
    /**
     * Validates student data according to business rules.
     */
    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        // Student ID validation: YYYYMXXXX pattern (e.g., 2024M0001)
        String studID = student.getStudID();
        if (studID == null || studID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (!studID.matches("^\\d{4}M\\d{4}$")) {
            throw new IllegalArgumentException("Student ID must follow pattern: YYYYMXXXX (e.g., 2024M0001)");
        }
        
        // Year level validation: must be 1, 2, 3, or 4
        String yearLevel = student.getYearLevel();
        if (yearLevel == null || yearLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Year level is required");
        }
        if (!yearLevel.matches("^[1-4]$")) {
            throw new IllegalArgumentException("Year level must be 1, 2, 3, or 4");
        }
        
        // Course validation: letters only
        String course = student.getCourse();
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course is required");
        }
        if (!course.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Course must contain letters only");
        }
        
        // First name validation: letters only (allows spaces for compound names)
        String firstName = student.getFirstName();
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (!firstName.matches("^[A-Za-z\\s]+$")) {
            throw new IllegalArgumentException("First name must contain letters only (no special characters)");
        }
        
        // Last name validation: letters only (allows spaces for compound names)
        String lastName = student.getLastName();
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (!lastName.matches("^[A-Za-z\\s]+$")) {
            throw new IllegalArgumentException("Last name must contain letters only (no special characters)");
        }
        
        // Section validation: letters only
        String section = student.getSection();
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Section is required");
        }
        if (!section.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Section must contain letters only");
        }
        
        // RFID validation: numeric only (optional field)
        String rfidTag = student.getRfidTag();
        if (rfidTag != null && !rfidTag.trim().isEmpty()) {
            if (!rfidTag.matches("^\\d+$")) {
                throw new IllegalArgumentException("RFID tag must contain digits only");
            }
        }
    }


    public void deleteStudent(String studID) {
        repository.delete(studID);
    }

    public List<Student> getStudentsByCourse(String course) {
        return repository.findByCourse(course);
    }

    public List<Student> searchStudentsByName(String name) {
        return repository.searchByName(name);
    }

    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        return repository.search(query);
    }

    public List<Student> searchByStudentID(String studID) {
        return repository.searchByID(studID);
    }

    public List<Student> searchByRFID(String rfidTag) {
        // Use partial matching for RFID search (supports searching "2", "26", "809", etc.)
        return repository.searchByRFIDPartial(rfidTag);
    }
    
    public List<Student> searchByNameOnly(String name) {
        // Name search only - restricts to first_name and last_name fields
        return repository.searchByName(name);
    }

    public List<Student> searchByCourse(String course) {
        return repository.findByCourse(course);
    }

    public List<Student> searchByYearLevel(String yearLevel) {
        return repository.findByYearLevel(yearLevel);
    }

    public List<Student> searchBySection(String section) {
        return repository.findBySection(section);
    }

    /**
     * Sorts a list of students by the specified field and order.
     */
    public List<Student> sortStudents(List<Student> students, String sortBy, String sortOrder) {
        if (students == null || students.isEmpty()) {
            return students;
        }

        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        
        students.sort((s1, s2) -> {
            int comparison = 0;
            
            switch (sortBy.toLowerCase()) {
                case "studid":
                case "studentid":
                    comparison = (s1.getStudID() != null && s2.getStudID() != null) 
                        ? s1.getStudID().compareToIgnoreCase(s2.getStudID()) : 0;
                    break;
                case "firstname":
                    comparison = (s1.getFirstName() != null && s2.getFirstName() != null) 
                        ? s1.getFirstName().compareToIgnoreCase(s2.getFirstName()) : 0;
                    break;
                case "lastname":
                    comparison = (s1.getLastName() != null && s2.getLastName() != null) 
                        ? s1.getLastName().compareToIgnoreCase(s2.getLastName()) : 0;
                    break;
                case "course":
                    comparison = (s1.getCourse() != null && s2.getCourse() != null) 
                        ? s1.getCourse().compareToIgnoreCase(s2.getCourse()) : 0;
                    break;
                case "yearlevel":
                case "year":
                    comparison = (s1.getYearLevel() != null && s2.getYearLevel() != null) 
                        ? s1.getYearLevel().compareToIgnoreCase(s2.getYearLevel()) : 0;
                    break;
                case "section":
                    comparison = (s1.getSection() != null && s2.getSection() != null) 
                        ? s1.getSection().compareToIgnoreCase(s2.getSection()) : 0;
                    break;
                case "rfid":
                case "rfidtag":
                    String rfid1 = s1.getRfidTag() != null ? s1.getRfidTag() : "";
                    String rfid2 = s2.getRfidTag() != null ? s2.getRfidTag() : "";
                    comparison = rfid1.compareToIgnoreCase(rfid2);
                    break;
                default:
                    // Default to last name
                    comparison = (s1.getLastName() != null && s2.getLastName() != null) 
                        ? s1.getLastName().compareToIgnoreCase(s2.getLastName()) : 0;
                    break;
            }
            
            return ascending ? comparison : -comparison;
        });
        
        return students;
    }

    /**
     * Gets distinct year levels (optimized - uses SQL DISTINCT).
     * @return List of distinct year levels, sorted
     */
    public List<String> getDistinctYearLevels() {
        return repository.getDistinctYearLevels();
    }

    /**
     * Gets distinct sections (optimized - uses SQL DISTINCT).
     * @return List of distinct sections, sorted
     */
    public List<String> getDistinctSections() {
        return repository.getDistinctSections();
    }

    /**
     * Gets distinct courses (optimized - uses SQL DISTINCT).
     * @return List of distinct courses, sorted
     */
    public List<String> getDistinctCourses() {
        return repository.getDistinctCourses();
    }
}



