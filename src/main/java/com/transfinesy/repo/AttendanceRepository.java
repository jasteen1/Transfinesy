package com.transfinesy.repo;

import com.transfinesy.model.Attendance;
import java.util.List;

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
}

