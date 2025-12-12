package com.transfinesy.repo;

import com.transfinesy.model.Student;
import java.util.List;

/**
 * Repository interface for Student data access operations.
 * 
 * This interface defines the contract for all student-related database operations.
 * It follows the Repository pattern, separating data access logic from business logic.
 * 
 * Key Operations:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Search operations (by name, ID, RFID, course, year level, section)
 * - Filter operations (by course, year level, section)
 * - Distinct value retrieval (for dropdown filters)
 * 
 * Implementation:
 * - StudentRepositoryImpl provides JDBC-based implementation
 * - Uses connection pooling via DBConfig for performance
 * - All methods handle SQL exceptions internally
 * 
 * @author transFINESy Development Team
 */
public interface StudentRepository {
    List<Student> findAll();
    Student findById(String studID);
    void save(Student s);
    void update(Student s);
    void delete(String studID);
    List<Student> findByCourse(String course);
    List<Student> findByYearLevel(String yearLevel);
    List<Student> findBySection(String section);
    Student findByRFID(String rfidTag);
    List<Student> searchByName(String name);
    List<Student> searchByID(String studID);
    List<Student> search(String query); // Search by ID, name, course, year, section, or RFID
    List<Student> searchByRFIDPartial(String rfidTag); // RFID search with partial matching
    
    /**
     * Gets distinct year levels from students table (optimized SQL query).
     * @return List of distinct year levels, sorted
     */
    List<String> getDistinctYearLevels();
    
    /**
     * Gets distinct sections from students table (optimized SQL query).
     * @return List of distinct sections, sorted
     */
    List<String> getDistinctSections();
    
    /**
     * Gets distinct courses from students table (optimized SQL query).
     * @return List of distinct courses, sorted
     */
    List<String> getDistinctCourses();
}

