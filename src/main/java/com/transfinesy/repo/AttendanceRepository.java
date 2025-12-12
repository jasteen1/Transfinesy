package com.transfinesy.repo;

import com.transfinesy.model.Attendance;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for Attendance operations.
 */
public interface AttendanceRepository {
    List<Attendance> findAll();
    Attendance findById(String attendanceID);
    List<Attendance> findByEvent(String eventID);
    List<Attendance> findByStudent(String studID);
    Attendance findByStudentAndEvent(String studID, String eventID);
    void save(Attendance a);
    void update(Attendance a);
    void delete(String attendanceID);
    
    /**
     * Counts unique students by attendance status (optimized SQL query).
     * @param eventId Optional event ID filter. If null, counts across all events.
     * @return Map with status names as keys and counts as values
     */
    Map<String, Long> countUniqueStudentsByStatus(String eventId);
    
    /**
     * Counts unique students by attendance status with course, year level and section filtering.
     * @param eventId Optional event ID filter. If null, counts across all events.
     * @param course Optional course filter. If null or "all", no filter applied.
     * @param yearLevel Optional year level filter. If null or "all", no filter applied.
     * @param section Optional section filter. If null or "all", no filter applied.
     * @return Map with status names as keys and counts as values
     */
    Map<String, Long> countUniqueStudentsByStatusFiltered(String eventId, String course, String yearLevel, String section);
}

