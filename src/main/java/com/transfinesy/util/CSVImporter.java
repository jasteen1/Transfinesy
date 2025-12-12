package com.transfinesy.util;

import com.transfinesy.model.Student;
import com.transfinesy.service.StudentService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for importing students from CSV files.
 * 
 * This class provides functionality to bulk import student records from CSV files.
 * It parses CSV data, validates student information, and saves records to the database.
 * 
 * CSV Format:
 * Expected format: rfidTag,studID,firstName,lastName,course,yearLevel,section
 * - First line is treated as header and skipped
 * - Empty lines are ignored
 * - Each line represents one student record
 * 
 * Key Features:
 * - Parses CSV files with comma-separated values
 * - Validates student data before import
 * - Skips invalid records and continues processing
 * - Returns count of successfully imported records
 * - Handles file I/O errors gracefully
 * 
 * Validation:
 * - Student ID must not be empty
 * - Duplicate student IDs are skipped
 * - Uses StudentService validation rules
 * 
 * Error Handling:
 * - Catches IOException for file reading errors
 * - Skips invalid records without stopping import
 * - Returns import statistics (imported count, skipped count)
 * 
 * Usage:
 * - Call importStudents(csvFilePath, studentService)
 * - Returns number of successfully imported students
 * 
 * @author transFINESy Development Team
 */
public class CSVImporter {
    
    /**
     * Imports students from CSV file.
     * Expected format: rfidTag,studID,firstName,lastName,course,yearLevel,section
     */
    public static int importStudents(String csvFilePath, StudentService studentService) {
        List<Student> students = new ArrayList<>();
        int imported = 0;
        int skipped = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 6) {
                    String rfidTag = values[0].trim();
                    String studID = values[1].trim();
                    String firstName = values[2].trim();
                    String lastName = values[3].trim();
                    String course = values[4].trim();
                    String yearLevel = values[5].trim();
                    String section = values.length > 6 ? values[6].trim() : "";
                    
                    // Skip if studID is empty
                    if (studID.isEmpty()) {
                        skipped++;
                        continue;
                    }
                    
                    // Use empty string if RFID is empty
                    if (rfidTag.isEmpty()) {
                        rfidTag = null;
                    }
                    
                    Student student = new Student(studID, firstName, lastName, course, yearLevel, section, rfidTag);
                    students.add(student);
                } else {
                    skipped++;
                }
            }
            
            // Import all students
            for (Student student : students) {
                try {
                    // Check if student already exists
                    if (studentService.getStudentById(student.getStudID()) == null) {
                        studentService.addStudent(student);
                        imported++;
                    } else {
                        // Update existing student with RFID if not set
                        Student existing = studentService.getStudentById(student.getStudID());
                        if (existing.getRfidTag() == null || existing.getRfidTag().isEmpty()) {
                            existing.setRfidTag(student.getRfidTag());
                            studentService.updateStudent(existing);
                            imported++;
                        } else {
                            skipped++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error importing student " + student.getStudID() + ": " + e.getMessage());
                    skipped++;
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        }
        
        System.out.println("Import completed: " + imported + " imported, " + skipped + " skipped");
        return imported;
    }
}
