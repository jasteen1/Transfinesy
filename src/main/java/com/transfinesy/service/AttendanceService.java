package com.transfinesy.service;

import com.transfinesy.model.Attendance;
import com.transfinesy.model.AttendanceSession;
import com.transfinesy.model.AttendanceStatus;
import com.transfinesy.model.Event;
import com.transfinesy.model.Fine;
import com.transfinesy.model.Student;
import com.transfinesy.repo.AttendanceRepository;
import com.transfinesy.repo.AttendanceRepositoryImpl;
import com.transfinesy.repo.EventRepository;
import com.transfinesy.repo.EventRepositoryImpl;
import com.transfinesy.repo.StudentRepository;
import com.transfinesy.repo.StudentRepositoryImpl;
import com.transfinesy.service.RFIDService;
import com.transfinesy.util.Queue;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Attendance operations.
 * Handles check-in logic with time comparison and status determination.
 */
@Service
public class AttendanceService {
    private AttendanceRepository repository;
    private EventRepository eventRepository;
    private StudentRepository studentRepository;
    private FineService fineService;
    private RFIDService rfidService;
    // Queue for processing attendance scans in order (FIFO)
    private Queue<AttendanceScanRequest> scanQueue;

    public AttendanceService() {
        this.repository = new AttendanceRepositoryImpl();
        this.eventRepository = new EventRepositoryImpl();
        this.studentRepository = new StudentRepositoryImpl();
        this.rfidService = new RFIDService();
        this.scanQueue = new Queue<>();
    }
    
    /**
     * Inner class to represent an attendance scan request in the queue.
     */
    private static class AttendanceScanRequest {
        String rfidTag;
        String eventId;
        String sessionType;
        boolean isTimeIn;
        
        AttendanceScanRequest(String rfidTag, String eventId, String sessionType, boolean isTimeIn) {
            this.rfidTag = rfidTag;
            this.eventId = eventId;
            this.sessionType = sessionType;
            this.isTimeIn = isTimeIn;
        }
    }

    public AttendanceService(FineService fineService) {
        this();
        this.fineService = fineService;
    }

    public AttendanceService(FineService fineService, RFIDService rfidService) {
        this();
        this.fineService = fineService;
        this.rfidService = rfidService;
    }

    /**
     * Searches students by ID, name, course, year level, or section.
     */
    public List<Student> searchStudents(String query) {
        return studentRepository.search(query);
    }

    /**
     * Checks in a student for an event.
     * Determines status (PRESENT/LATE) based on check-in time vs event time-in.
     */
    public Attendance checkInStudent(String studID, String eventID) {
        Event event = eventRepository.findById(eventID);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventID);
        }

        LocalDateTime checkInTime = LocalDateTime.now();
        LocalTime relevantTimeIn = event.getRelevantTimeIn(checkInTime);

        AttendanceStatus status = AttendanceStatus.PRESENT;
        int minutesLate = 0;

        // If there's a relevant time-in, check if student is late
        if (relevantTimeIn != null) {
            LocalTime checkInTimeOnly = checkInTime.toLocalTime();
            if (checkInTimeOnly.isAfter(relevantTimeIn)) {
                status = AttendanceStatus.LATE;
                minutesLate = (int) ChronoUnit.MINUTES.between(relevantTimeIn, checkInTimeOnly);
            }
        }

        // Check if attendance already exists
        Attendance existing = findAttendanceByStudentAndEvent(studID, eventID);
        if (existing != null) {
            existing.setCheckInTime(checkInTime);
            existing.setStatus(status);
            existing.setMinutesLate(minutesLate);
            if (existing.getScanSource() == null) {
                existing.setScanSource("MANUAL");
            }
            repository.update(existing);
            return existing;
        } else {
            // Create new attendance record
            String attendanceID = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Attendance attendance = new Attendance(
                attendanceID,
                studID,
                eventID,
                status,
                minutesLate,
                checkInTime,
                null, // checkOutTime is null initially
                "MANUAL" // scanSource
            );
            repository.save(attendance);
            return attendance;
        }
    }

    /**
     * Checks out a student from an event.
     */
    public void checkOutStudent(String studID, String eventID) {
        Attendance attendance = findAttendanceByStudentAndEvent(studID, eventID);
        if (attendance != null) {
            attendance.setCheckOutTime(LocalDateTime.now());
            repository.update(attendance);
        }
    }

    /**
     * Finds attendance record for a specific student and event.
     */
    public Attendance findAttendanceByStudentAndEvent(String studID, String eventID) {
        List<Attendance> attendances = repository.findByEvent(eventID);
        return attendances.stream()
            .filter(a -> a.getStudID().equals(studID))
            .findFirst()
            .orElse(null);
    }

    /**
     * Scans RFID and automatically checks in student.
     */
    public Attendance scanRFID(String rfidTag, Event event) {
        if (rfidService == null) {
            rfidService = new RFIDService();
        }
        return rfidService.autoCheckIn(event, rfidTag);
    }

    /**
     * Finalizes attendance for an event and generates fines.
     * Marks all students without attendance as ABSENT.
     * Generates fines for LATE and ABSENT students.
     */
    public void finalizeEventAttendance(String eventID) {
        Event event = eventRepository.findById(eventID);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventID);
        }

        // Get all students
        List<Student> allStudents = studentRepository.findAll();
        
        // Get existing attendance records
        List<Attendance> existingAttendance = repository.findByEvent(eventID);
        List<String> studentsWithAttendance = existingAttendance.stream()
            .map(Attendance::getStudID)
            .collect(Collectors.toList());

        // Mark students without attendance as ABSENT
        for (Student student : allStudents) {
            if (!studentsWithAttendance.contains(student.getStudID())) {
                String attendanceID = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Attendance absentAttendance = new Attendance(
                    attendanceID,
                    student.getStudID(),
                    eventID,
                    AttendanceStatus.ABSENT,
                    0,
                    null,
                    null,
                    "MANUAL" // scanSource
                );
                repository.save(absentAttendance);
            }
        }

        // Generate fines for LATE and ABSENT students using event-specific fine amounts
        if (fineService != null) {
            List<Attendance> allAttendance = repository.findByEvent(eventID);
            fineService.generateFinesFromAttendances(allAttendance, eventID, event);
        }
    }

    /**
     * Legacy method name for compatibility.
     */
    public void finalizeAttendanceAndGenerateFines(String eventID) {
        finalizeEventAttendance(eventID);
    }

    public void recordAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        repository.save(attendance);
    }

    public void updateAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        repository.update(attendance);
    }

    public List<Attendance> getAttendanceByEvent(String eventID) {
        return repository.findByEvent(eventID);
    }

    public List<Attendance> getAttendanceByStudent(String studID) {
        return repository.findByStudent(studID);
    }

    public Attendance getAttendanceById(String attendanceID) {
        return repository.findById(attendanceID);
    }

    public void deleteAttendance(String attendanceID) {
        repository.delete(attendanceID);
    }

    /**
     * Gets all attendance records (for dashboard statistics).
     */
    public List<Attendance> getAllAttendance() {
        return repository.findAll();
    }

    /**
     * Saves multiple attendance records for an event.
     */
    public void saveAttendanceBatch(List<Attendance> attendances) {
        for (Attendance attendance : attendances) {
            recordAttendance(attendance);
        }
    }

    // ========== NEW START-STOP ATTENDANCE LOGIC ==========

    /**
     * Checks in a student using the new Start-Stop window logic.
     * @param studID Student ID
     * @param eventID Event ID
     * @param sessionType "AM" or "PM" - determines which window to check
     * @param isTimeIn true for time-in, false for time-out
     * @return Attendance record with status (PRESENT if within window, LATE if after stop time)
     */
    public Attendance checkInStudentWithWindow(String studID, String eventID, String sessionType, boolean isTimeIn) {
        Event event = eventRepository.findById(eventID);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventID);
        }

        // Check if attendance can still be recorded (within 1 day of event date)
        LocalDateTime now = LocalDateTime.now();
        LocalDate eventDate = event.getEventDate();
        LocalDate today = now.toLocalDate();
        long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(eventDate, today);
        
        if (daysDifference > 1) {
            throw new IllegalArgumentException("Attendance cannot be recorded. The event date (" + eventDate + ") was more than 1 day ago.");
        }

        LocalDateTime checkInTime = now;
        LocalTime scannedTime = checkInTime.toLocalTime();
        
        LocalTime startTime = null;
        LocalTime stopTime = null;
        
        // Determine which window to use based on session and type
        if ("AM".equalsIgnoreCase(sessionType)) {
            if (isTimeIn) {
                startTime = event.getTimeInStartAM();
                stopTime = event.getTimeInStopAM();
            } else {
                startTime = event.getTimeOutStartAM();
                stopTime = event.getTimeOutStopAM();
            }
        } else if ("PM".equalsIgnoreCase(sessionType)) {
            if (isTimeIn) {
                startTime = event.getTimeInStartPM();
                stopTime = event.getTimeInStopPM();
            } else {
                startTime = event.getTimeOutStartPM();
                stopTime = event.getTimeOutStopPM();
            }
        }
        
        AttendanceStatus status = AttendanceStatus.PRESENT;
        int minutesLate = 0;
        
        // Check if scanned time is within window
        if (startTime != null && stopTime != null) {
            // NEW LOGIC: Check if scanned time is within the start-stop window
            if (scannedTime.isBefore(startTime)) {
                // Scanned before window opens - automatically LATE
                status = AttendanceStatus.LATE;
                minutesLate = (int) ChronoUnit.MINUTES.between(scannedTime, startTime);
            } else if (scannedTime.isAfter(stopTime)) {
                // Scanned after window closes - automatically LATE
                status = AttendanceStatus.LATE;
                minutesLate = (int) ChronoUnit.MINUTES.between(stopTime, scannedTime);
            } else {
                // Within window - check if on time
                if (scannedTime.equals(startTime) || scannedTime.isAfter(startTime)) {
                    // On time - scanned at or after start time (within window)
                    status = AttendanceStatus.PRESENT;
                    minutesLate = 0;
                } else {
                    // Should not reach here, but just in case
                    status = AttendanceStatus.PRESENT;
                    minutesLate = 0;
                }
            }
        } else {
            // No window defined - use legacy logic
            LocalTime relevantTimeIn = event.getRelevantTimeIn(checkInTime);
            if (relevantTimeIn != null && scannedTime.isAfter(relevantTimeIn)) {
                status = AttendanceStatus.LATE;
                minutesLate = (int) ChronoUnit.MINUTES.between(relevantTimeIn, scannedTime);
            }
        }

        // Check if attendance already exists
        Attendance existing = findAttendanceByStudentAndEvent(studID, eventID);
        if (existing != null) {
            existing.setCheckInTime(checkInTime);
            existing.setStatus(status);
            existing.setMinutesLate(minutesLate);
            if (existing.getScanSource() == null) {
                existing.setScanSource("RFID");
            }
            repository.update(existing);
            
            // Generate fine if student is late using event-specific fine amounts
            if (status == AttendanceStatus.LATE && minutesLate > 0) {
                FineService fineService = this.fineService != null ? this.fineService : new FineService();
                // Check if fine already exists for this attendance
                List<Fine> existingFines = fineService.getFinesByEvent(eventID);
                boolean fineExists = existingFines.stream()
                    .anyMatch(f -> f.getStudID().equals(studID) && f.getEventID().equals(eventID));
                
                if (!fineExists) {
                    Fine fine = fineService.createFineFromAttendance(existing, eventID, event);
                    if (fine != null) {
                        fineService.saveFine(fine);
                    }
                }
            }
            
            return existing;
        } else {
            // Create new attendance record
            String attendanceID = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Attendance attendance = new Attendance(
                attendanceID,
                studID,
                eventID,
                status,
                minutesLate,
                checkInTime,
                null,
                "RFID"
            );
            repository.save(attendance);
            
            // Generate fine if student is late using event-specific fine amounts
            if (status == AttendanceStatus.LATE && minutesLate > 0) {
                FineService fineService = this.fineService != null ? this.fineService : new FineService();
                Fine fine = fineService.createFineFromAttendance(attendance, eventID, event);
                if (fine != null) {
                    fineService.saveFine(fine);
                }
            }
            
            return attendance;
        }
    }

    /**
     * Scans RFID using the new Start-Stop window logic.
     * Uses a Queue to process scans in order (FIFO).
     * @param rfidTag RFID tag
     * @param event Event
     * @param sessionType "AM" or "PM"
     * @param isTimeIn true for time-in, false for time-out
     * @return Attendance record
     */
    public Attendance scanRFIDWithWindow(String rfidTag, Event event, String sessionType, boolean isTimeIn) {
        if (rfidService == null) {
            rfidService = new RFIDService();
        }
        
        // Add scan request to queue for ordered processing (FIFO)
        AttendanceScanRequest request = new AttendanceScanRequest(rfidTag, event.getEventID(), sessionType, isTimeIn);
        scanQueue.enqueue(request);
        
        // Process the scan (queue ensures FIFO order)
        try {
            // Find student by RFID
            Student student = rfidService.getStudentByRFID(rfidTag);
            if (student == null) {
                throw new IllegalArgumentException("Student not found for RFID: " + rfidTag);
            }
            
            Attendance attendance = checkInStudentWithWindow(student.getStudID(), event.getEventID(), sessionType, isTimeIn);
            
            // Remove processed request from queue (FIFO - first in, first out)
            if (!scanQueue.isEmpty()) {
                scanQueue.dequeue();
            }
            
            return attendance;
        } catch (Exception e) {
            // Remove failed request from queue
            if (!scanQueue.isEmpty()) {
                try {
                    scanQueue.dequeue();
                } catch (Exception ex) {
                    // Ignore if queue is empty
                }
            }
            throw e;
        }
    }
    
    /**
     * Gets the current size of the scan queue.
     */
    public int getScanQueueSize() {
        return scanQueue.size();
    }

    /**
     * Marks all students as ABSENT for a specific session who haven't checked in.
     * Only marks absentees if the attendance window has closed.
     * Does not overwrite existing attendance records.
     * @param eventID Event ID
     * @param sessionType "AM" or "PM"
     * @param isTimeIn true for time-in session, false for time-out session
     */
    public void markSessionAbsentees(String eventID, String sessionType, boolean isTimeIn) {
        Event event = eventRepository.findById(eventID);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + eventID);
        }

        // Check if the attendance window has closed
        LocalTime stopTime = null;
        if ("AM".equalsIgnoreCase(sessionType)) {
            stopTime = isTimeIn ? event.getTimeInStopAM() : event.getTimeOutStopAM();
        } else if ("PM".equalsIgnoreCase(sessionType)) {
            stopTime = isTimeIn ? event.getTimeInStopPM() : event.getTimeOutStopPM();
        }
        
        // Only mark absentees if the window has closed
        if (stopTime != null) {
            LocalTime now = LocalTime.now();
            if (now.isBefore(stopTime)) {
                throw new IllegalArgumentException("Cannot mark absentees yet. The attendance window is still open until " + stopTime + ".");
            }
        }

        // Get all students
        List<Student> allStudents = studentRepository.findAll();
        
        // Get existing attendance records for this event
        List<Attendance> existingAttendance = repository.findByEvent(eventID);
        
        // Only mark students who don't have ANY attendance record for this event
        List<String> studentsWithAttendance = existingAttendance.stream()
            .map(Attendance::getStudID)
            .collect(Collectors.toList());

        // Mark students without attendance as ABSENT and generate fines
        FineService fineService = this.fineService != null ? this.fineService : new FineService();
        for (Student student : allStudents) {
            if (!studentsWithAttendance.contains(student.getStudID())) {
                String attendanceID = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Attendance absentAttendance = new Attendance(
                    attendanceID,
                    student.getStudID(),
                    eventID,
                    AttendanceStatus.ABSENT,
                    0,
                    null,
                    null,
                    "MANUAL"
                );
                repository.save(absentAttendance);
                
                // Generate fine for absent student using event-specific fine amounts
                Fine fine = fineService.createFineFromAttendance(absentAttendance, eventID, event);
                if (fine != null) {
                    fineService.saveFine(fine);
                }
            }
        }
    }

    /**
     * Determines if a scanned time is within the attendance window for a session.
     * @param event Event
     * @param sessionType "AM" or "PM"
     * @param isTimeIn true for time-in, false for time-out
     * @param scannedTime The time that was scanned
     * @return true if within window, false otherwise
     */
    public boolean isWithinAttendanceWindow(Event event, String sessionType, boolean isTimeIn, LocalTime scannedTime) {
        LocalTime startTime = null;
        LocalTime stopTime = null;
        
        if ("AM".equalsIgnoreCase(sessionType)) {
            if (isTimeIn) {
                startTime = event.getTimeInStartAM();
                stopTime = event.getTimeInStopAM();
            } else {
                startTime = event.getTimeOutStartAM();
                stopTime = event.getTimeOutStopAM();
            }
        } else if ("PM".equalsIgnoreCase(sessionType)) {
            if (isTimeIn) {
                startTime = event.getTimeInStartPM();
                stopTime = event.getTimeInStopPM();
            } else {
                startTime = event.getTimeOutStartPM();
                stopTime = event.getTimeOutStopPM();
            }
        }
        
        if (startTime == null || stopTime == null) {
            return false;
        }
        
        return !scannedTime.isBefore(startTime) && !scannedTime.isAfter(stopTime);
    }
}
