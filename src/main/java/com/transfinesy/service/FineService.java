package com.transfinesy.service;

import com.transfinesy.model.Attendance;
import com.transfinesy.model.AttendanceStatus;
import com.transfinesy.model.Event;
import com.transfinesy.model.Fine;
import com.transfinesy.repo.FineRepository;
import com.transfinesy.repo.FineRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for Fine operations.
 * Contains business rules for calculating fine amounts.
 * Fine amounts are now configurable per event.
 */
@Service
public class FineService {
    private FineRepository repository;
    private LedgerService ledgerService;

    // Default fine calculation constants (used as fallback if event doesn't specify)
    private static final double DEFAULT_FINE_ABSENT = 100.0;
    private static final double DEFAULT_FINE_PER_MINUTE_LATE = 2.0;
    private static final double DEFAULT_MIN_FINE_LATE = 20.0;

    public FineService() {
        this.repository = new FineRepositoryImpl();
        this.ledgerService = new LedgerService();
    }

    public FineService(LedgerService ledgerService) {
        this.repository = new FineRepositoryImpl();
        this.ledgerService = ledgerService;
    }

    /**
     * Calculates fine amount based on attendance status and minutes late.
     * Uses default fine amounts if event is not provided.
     */
    public double calculateFineAmount(AttendanceStatus status, int minutesLate) {
        return calculateFineAmount(status, minutesLate, null);
    }

    /**
     * Calculates fine amount based on attendance status, minutes late, and event-specific fine amounts.
     * If event is provided, uses event's fine amounts. Otherwise, uses default constants.
     */
    public double calculateFineAmount(AttendanceStatus status, int minutesLate, Event event) {
        switch (status) {
            case ABSENT:
                // Use event's absent fine amount if available, otherwise use default
                if (event != null && event.getFineAmountAbsent() != null) {
                    return event.getFineAmountAbsent();
                }
                return DEFAULT_FINE_ABSENT;
            case LATE:
                // Use event's late fine amount (per minute) if available, otherwise use default
                double finePerMinute;
                if (event != null && event.getFineAmountLate() != null) {
                    finePerMinute = event.getFineAmountLate();
                } else {
                    finePerMinute = DEFAULT_FINE_PER_MINUTE_LATE;
                }
                double lateFine = minutesLate * finePerMinute;
                // Apply minimum fine (use default minimum if event doesn't specify)
                return Math.max(lateFine, DEFAULT_MIN_FINE_LATE);
            case PRESENT:
            case EXCUSED:
            default:
                return 0.0;
        }
    }

    /**
     * Creates a fine based on attendance record.
     * Uses default fine amounts if event is not provided.
     */
    public Fine createFineFromAttendance(Attendance attendance, String eventID) {
        return createFineFromAttendance(attendance, eventID, null);
    }

    /**
     * Creates a fine based on attendance record and event-specific fine amounts.
     * If event is provided, uses event's fine amounts. Otherwise, uses default constants.
     */
    public Fine createFineFromAttendance(Attendance attendance, String eventID, Event event) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }

        // Calculate fine amount using event-specific fine amounts if available
        double fineAmount = calculateFineAmount(attendance.getStatus(), attendance.getMinutesLate(), event);

        // Only create fine if amount > 0
        if (fineAmount <= 0) {
            return null;
        }

        String fineID = "FINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String transactionID = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Fine fine = new Fine(
            fineID,
            transactionID,
            attendance.getStudID(),
            eventID,
            fineAmount,
            LocalDate.now()
        );

        return fine;
    }

    /**
     * Saves a fine and updates the ledger.
     */
    public void saveFine(Fine fine) {
        if (fine == null) {
            throw new IllegalArgumentException("Fine cannot be null");
        }
        repository.save(fine);
        // Update ledger
        ledgerService.addTransactionToLedger(fine.getStudID(), fine);
    }

    /**
     * Generates fines for a list of attendance records.
     * Uses default fine amounts if event is not provided.
     */
    public void generateFinesFromAttendances(List<Attendance> attendances, String eventID) {
        generateFinesFromAttendances(attendances, eventID, null);
    }

    /**
     * Generates fines for a list of attendance records using event-specific fine amounts.
     * If event is provided, uses event's fine amounts. Otherwise, uses default constants.
     */
    public void generateFinesFromAttendances(List<Attendance> attendances, String eventID, Event event) {
        for (Attendance attendance : attendances) {
            Fine fine = createFineFromAttendance(attendance, eventID, event);
            if (fine != null) {
                saveFine(fine);
            }
        }
    }

    public List<Fine> getFinesByStudent(String studID) {
        return repository.findByStudent(studID);
    }

    public List<Fine> getFinesByEvent(String eventID) {
        return repository.findByEvent(eventID);
    }

    public Fine getFineById(String fineID) {
        return repository.findById(fineID);
    }

    public List<Fine> getAllFines() {
        return repository.findAll();
    }

    public void deleteFine(String fineID) {
        repository.delete(fineID);
    }
}

