package com.transfinesy.repo;

import com.transfinesy.config.DBConfig;
import com.transfinesy.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of StudentRepository.
 */
public class StudentRepositoryImpl implements StudentRepository {

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public Student findById(String studID) {
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE stud_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(Student s) {
        String sql = "INSERT INTO students (stud_id, first_name, last_name, course, year_level, section, rfid_tag) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getStudID());
            pstmt.setString(2, s.getFirstName());
            pstmt.setString(3, s.getLastName());
            pstmt.setString(4, s.getCourse());
            pstmt.setString(5, s.getYearLevel());
            pstmt.setString(6, s.getSection());
            pstmt.setString(7, s.getRfidTag());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMsg = "Database error: " + e.getMessage();
            if (e.getMessage().contains("Unknown database")) {
                errorMsg = "Database 'transfinesy' does not exist. Please create it first.";
            } else if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")) {
                errorMsg = "Database tables not found. Please run the schema.sql script.";
            } else if (e.getMessage().contains("Access denied")) {
                errorMsg = "Database access denied. Please check your credentials in db.properties.";
            } else if (e.getMessage().contains("Communications link failure") || e.getMessage().contains("Connection refused")) {
                errorMsg = "Cannot connect to database. Please ensure MySQL is running and check your connection settings.";
            }
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void update(Student s) {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, course = ?, year_level = ?, section = ?, rfid_tag = ? WHERE stud_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, s.getFirstName());
            pstmt.setString(2, s.getLastName());
            pstmt.setString(3, s.getCourse());
            pstmt.setString(4, s.getYearLevel());
            pstmt.setString(5, s.getSection());
            pstmt.setString(6, s.getRfidTag());
            pstmt.setString(7, s.getStudID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update student", e);
        }
    }

    @Override
    public void delete(String studID) {
        String sql = "DELETE FROM students WHERE stud_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete student", e);
        }
    }

    @Override
    public List<Student> findByCourse(String course) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE course = ? ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public List<Student> searchByName(String name) {
        List<Student> students = new ArrayList<>();
        // Name search only matches first_name and last_name, not other fields
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students " +
                     "WHERE first_name LIKE ? OR last_name LIKE ? OR CONCAT(first_name, ' ', last_name) LIKE ? " +
                     "ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public List<Student> findByYearLevel(String yearLevel) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE year_level = ? ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, yearLevel);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public List<Student> findBySection(String section) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE section = ? ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, section);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public List<Student> searchByID(String studID) {
        List<Student> students = new ArrayList<>();
        // Student ID search only matches stud_id, not other fields
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE stud_id LIKE ? ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + studID + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public List<Student> search(String query) {
        List<Student> students = new ArrayList<>();
        // Include RFID in "All Fields" search
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students " +
                     "WHERE stud_id LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR course LIKE ? OR year_level LIKE ? OR section LIKE ? OR rfid_tag LIKE ? " +
                     "ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            for (int i = 1; i <= 7; i++) {
                pstmt.setString(i, searchPattern);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    @Override
    public Student findByRFID(String rfidTag) {
        // Exact match for RFID
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE rfid_tag = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rfidTag);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Search students by RFID with partial matching (LIKE %query%).
     * Used for RFID search functionality.
     */
    public List<Student> searchByRFIDPartial(String rfidTag) {
        List<Student> students = new ArrayList<>();
        // RFID search with partial matching - only matches rfid_tag field
        String sql = "SELECT stud_id, first_name, last_name, course, year_level, section, rfid_tag FROM students WHERE rfid_tag LIKE ? ORDER BY last_name, first_name";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + rfidTag + "%";
            pstmt.setString(1, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student(
                    rs.getString("stud_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getString("year_level"),
                    rs.getString("section"),
                    rs.getString("rfid_tag")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
}

