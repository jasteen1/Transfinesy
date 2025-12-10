package com.transfinesy.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a school event where attendance is checked.
 */
public class Event {
    private String eventID;
    private String eventName;
    private LocalDate eventDate;
    private Integer semester;              // 1 or 2
    private String schoolYear;             // e.g., "2025-2026"
    
    // Legacy fields (kept for backward compatibility)
    private LocalTime amTimeIn;            // nullable; can be null if no morning session
    private LocalTime amTimeOut;           // nullable
    private LocalTime pmTimeIn;            // nullable
    private LocalTime pmTimeOut;          // nullable
    
    // New Start-Stop attendance fields
    private AttendanceSession sessionType; // MORNING_ONLY, AFTERNOON_ONLY, BOTH
    
    // Morning session Start-Stop times
    private LocalTime timeInStartAM;       // Start time for AM time-in window
    private LocalTime timeInStopAM;        // Stop time for AM time-in window
    private LocalTime timeOutStartAM;      // Start time for AM time-out window
    private LocalTime timeOutStopAM;       // Stop time for AM time-out window
    
    // Afternoon session Start-Stop times
    private LocalTime timeInStartPM;       // Start time for PM time-in window
    private LocalTime timeInStopPM;        // Stop time for PM time-in window
    private LocalTime timeOutStartPM;      // Start time for PM time-out window
    private LocalTime timeOutStopPM;       // Stop time for PM time-out window
    
    private Double fineAmountAbsent;       // fine amount for absent students
    private Double fineAmountLate;        // fine amount for late students

    public Event() {
    }

    public Event(String eventID, String eventName, LocalDate eventDate, Integer semester, String schoolYear) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.semester = semester;
        this.schoolYear = schoolYear;
    }

    /**
     * Gets the last time-out for this event (maximum of amTimeOut and pmTimeOut).
     * Returns null if no time-out is set.
     */
    public LocalTime getLastTimeOut() {
        if (amTimeOut == null && pmTimeOut == null) {
            return null;
        }
        if (amTimeOut == null) {
            return pmTimeOut;
        }
        if (pmTimeOut == null) {
            return amTimeOut;
        }
        return amTimeOut.isAfter(pmTimeOut) ? amTimeOut : pmTimeOut;
    }

    /**
     * Gets the relevant time-in for a given check-in time.
     * Returns amTimeIn if check-in is before noon, pmTimeIn otherwise.
     */
    public LocalTime getRelevantTimeIn(java.time.LocalDateTime checkInTime) {
        if (checkInTime == null) {
            return null;
        }
        int hour = checkInTime.getHour();
        if (hour < 12) {
            return amTimeIn;
        } else {
            return pmTimeIn;
        }
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public LocalTime getAmTimeIn() {
        return amTimeIn;
    }

    public void setAmTimeIn(LocalTime amTimeIn) {
        this.amTimeIn = amTimeIn;
    }

    public LocalTime getAmTimeOut() {
        return amTimeOut;
    }

    public void setAmTimeOut(LocalTime amTimeOut) {
        this.amTimeOut = amTimeOut;
    }

    public LocalTime getPmTimeIn() {
        return pmTimeIn;
    }

    public void setPmTimeIn(LocalTime pmTimeIn) {
        this.pmTimeIn = pmTimeIn;
    }

    public LocalTime getPmTimeOut() {
        return pmTimeOut;
    }

    public void setPmTimeOut(LocalTime pmTimeOut) {
        this.pmTimeOut = pmTimeOut;
    }

    public Double getFineAmountAbsent() {
        return fineAmountAbsent;
    }

    public void setFineAmountAbsent(Double fineAmountAbsent) {
        this.fineAmountAbsent = fineAmountAbsent;
    }

    public Double getFineAmountLate() {
        return fineAmountLate;
    }

    public void setFineAmountLate(Double fineAmountLate) {
        this.fineAmountLate = fineAmountLate;
    }

    // New Start-Stop attendance getters and setters
    public AttendanceSession getSessionType() {
        return sessionType;
    }

    public void setSessionType(AttendanceSession sessionType) {
        this.sessionType = sessionType;
    }

    public LocalTime getTimeInStartAM() {
        return timeInStartAM;
    }

    public void setTimeInStartAM(LocalTime timeInStartAM) {
        this.timeInStartAM = timeInStartAM;
    }

    public LocalTime getTimeInStopAM() {
        return timeInStopAM;
    }

    public void setTimeInStopAM(LocalTime timeInStopAM) {
        this.timeInStopAM = timeInStopAM;
    }

    public LocalTime getTimeOutStartAM() {
        return timeOutStartAM;
    }

    public void setTimeOutStartAM(LocalTime timeOutStartAM) {
        this.timeOutStartAM = timeOutStartAM;
    }

    public LocalTime getTimeOutStopAM() {
        return timeOutStopAM;
    }

    public void setTimeOutStopAM(LocalTime timeOutStopAM) {
        this.timeOutStopAM = timeOutStopAM;
    }

    public LocalTime getTimeInStartPM() {
        return timeInStartPM;
    }

    public void setTimeInStartPM(LocalTime timeInStartPM) {
        this.timeInStartPM = timeInStartPM;
    }

    public LocalTime getTimeInStopPM() {
        return timeInStopPM;
    }

    public void setTimeInStopPM(LocalTime timeInStopPM) {
        this.timeInStopPM = timeInStopPM;
    }

    public LocalTime getTimeOutStartPM() {
        return timeOutStartPM;
    }

    public void setTimeOutStartPM(LocalTime timeOutStartPM) {
        this.timeOutStartPM = timeOutStartPM;
    }

    public LocalTime getTimeOutStopPM() {
        return timeOutStopPM;
    }

    public void setTimeOutStopPM(LocalTime timeOutStopPM) {
        this.timeOutStopPM = timeOutStopPM;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventID='" + eventID + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", semester=" + semester +
                ", schoolYear='" + schoolYear + '\'' +
                '}';
    }
}

