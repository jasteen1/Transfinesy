package com.transfinesy.service;

import com.transfinesy.model.Attendance;
import com.transfinesy.model.AttendanceStatus;
import com.transfinesy.model.Event;
import com.transfinesy.model.Student;
import com.transfinesy.repo.AttendanceRepository;
import com.transfinesy.repo.AttendanceRepositoryImpl;
import com.transfinesy.repo.StudentRepository;
import com.transfinesy.repo.StudentRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service for RFID-based attendance scanning.
 */
@Service
public class RFIDService {
    private StudentRepository studentRepository;
    private AttendanceRepository attendanceRepository;

    public RFIDService() {
        this.studentRepository = new StudentRepositoryImpl();
        this.attendanceRepository = new AttendanceRepositoryImpl();
    }

    /**
     * Placeholder method to detect RFID.
     * In a real system, this would read from RFID hardware.
     * For simulation, returns null (no RFID detected).
     */
    public String detectRFID() {
        // TODO: Implement actual RFID hardware reading
        // For now, return null to indicate no RFID detected
        return null;
    }

    /**
     * Gets a student by RFID tag.
     */
    public Student getStudentByRFID(String rfidTag) {
        if (rfidTag == null || rfidTag.trim().isEmpty()) {
            return null;
        }
        return studentRepository.findByRFID(rfidTag);
    }

    /**
     * Automatically checks in a student using RFID.
     * Creates or updates attendance record with timestamp and status determination.
     */
    public Attendance autoCheckIn(Event event, String rfidTag) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (rfidTag == null || rfidTag.trim().isEmpty()) {
            throw new IllegalArgumentException("RFID tag cannot be null or empty");
        }

        // Find student by RFID
        Student student = getStudentByRFID(rfidTag);
        if (student == null) {
            throw new IllegalArgumentException("Student not found for RFID: " + rfidTag);
        }

        LocalDateTime checkInTime = LocalDateTime.now();
        LocalTime relevantTimeIn = event.getRelevantTimeIn(checkInTime);

        AttendanceStatus status = AttendanceStatus.PRESENT;
        int minutesLate = 0;

        // Determine status based on check-in time vs event time-in
        if (relevantTimeIn != null) {
            LocalTime checkInTimeOnly = checkInTime.toLocalTime();
            if (checkInTimeOnly.isAfter(relevantTimeIn)) {
                status = AttendanceStatus.LATE;
                minutesLate = (int) ChronoUnit.MINUTES.between(relevantTimeIn, checkInTimeOnly);
            }
        }

        // Check if attendance already exists
        Attendance existing = findAttendanceByStudentAndEvent(student.getStudID(), event.getEventID());
        
        if (existing != null) {
            // Update existing attendance
            existing.setCheckInTime(checkInTime);
            existing.setStatus(status);
            existing.setMinutesLate(minutesLate);
            existing.setScanSource("RFID");
            attendanceRepository.update(existing);
            return existing;
        } else {
            // Create new attendance record
            String attendanceID = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Attendance attendance = new Attendance(
                attendanceID,
                student.getStudID(),
                event.getEventID(),
                status,
                minutesLate,
                checkInTime,
                null, // checkOutTime is null initially
                "RFID" // scanSource
            );
            attendanceRepository.save(attendance);
            return attendance;
        }
    }

    /**
     * Finds attendance record for a specific student and event.
     */
    private Attendance findAttendanceByStudentAndEvent(String studID, String eventID) {
        var attendances = attendanceRepository.findByEvent(eventID);
        return attendances.stream()
            .filter(a -> a.getStudID().equals(studID))
            .findFirst()
            .orElse(null);
    }
}
