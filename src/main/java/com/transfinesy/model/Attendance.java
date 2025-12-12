package com.transfinesy.model;

import java.time.LocalDateTime;

/**
 * Represents an attendance record for a student at an event.
 * 
 * This class models a single attendance record that can represent either:
 * - A time-in record (when student checks in)
 * - A time-out record (when student checks out)
 * 
 * Key Features:
 * - Supports AM/PM session separation (prevents overwriting)
 * - Separate records for TIME_IN and TIME_OUT
 * - Status tracking (PRESENT, LATE, ABSENT, EXCUSED)
 * - Minutes late calculation for late arrivals
 * - Source tracking (RFID scan vs manual entry)
 * 
 * Session and Record Type:
 * - session: "AM", "PM", or null (for legacy records)
 * - recordType: "TIME_IN", "TIME_OUT", or null (for legacy records)
 * 
 * This design allows:
 * - Separate AM and PM attendance records
 * - Separate time-in and time-out records
 * - No data loss when students check in/out multiple times
 * 
 * Legacy Records:
 * - Old records may have null session and recordType
 * - These are displayed separately in the UI as "Legacy Attendance"
 * 
 * @author transFINESy Development Team
 */
public class Attendance {
    private String attendanceID;
    private String studID;
    private String eventID;
    private AttendanceStatus status;   // enum: PRESENT, LATE, ABSENT, EXCUSED
    private int minutesLate;
    private LocalDateTime checkInTime;   // timestamp when council checks the student in
    private LocalDateTime checkOutTime;  // optional, can be null
    private String scanSource;          // values: "RFID" or "MANUAL"
    private String session;             // values: "AM", "PM", or null for legacy records
    private String recordType;           // values: "TIME_IN", "TIME_OUT", or null for legacy records

    public Attendance() {
    }

    public Attendance(String attendanceID, String studID, String eventID, AttendanceStatus status, int minutesLate) {
        this.attendanceID = attendanceID;
        this.studID = studID;
        this.eventID = eventID;
        this.status = status;
        this.minutesLate = minutesLate;
    }

    public Attendance(String attendanceID, String studID, String eventID, AttendanceStatus status, int minutesLate, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        this.attendanceID = attendanceID;
        this.studID = studID;
        this.eventID = eventID;
        this.status = status;
        this.minutesLate = minutesLate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.scanSource = "MANUAL"; // default
    }

    public Attendance(String attendanceID, String studID, String eventID, AttendanceStatus status, int minutesLate, LocalDateTime checkInTime, LocalDateTime checkOutTime, String scanSource) {
        this.attendanceID = attendanceID;
        this.studID = studID;
        this.eventID = eventID;
        this.status = status;
        this.minutesLate = minutesLate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.scanSource = scanSource;
    }

    public String getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(String attendanceID) {
        this.attendanceID = attendanceID;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public int getMinutesLate() {
        return minutesLate;
    }

    public void setMinutesLate(int minutesLate) {
        this.minutesLate = minutesLate;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getScanSource() {
        return scanSource;
    }

    public void setScanSource(String scanSource) {
        this.scanSource = scanSource;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceID='" + attendanceID + '\'' +
                ", studID='" + studID + '\'' +
                ", eventID='" + eventID + '\'' +
                ", status=" + status +
                ", minutesLate=" + minutesLate +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", scanSource='" + scanSource + '\'' +
                ", session='" + session + '\'' +
                ", recordType='" + recordType + '\'' +
                '}';
    }
}

