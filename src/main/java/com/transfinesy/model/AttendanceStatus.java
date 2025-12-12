package com.transfinesy.model;

/**
 * Enumeration for attendance status values.
 * 
 * This enum provides type-safe values for student attendance status.
 * Used throughout the system to track and filter attendance records.
 * 
 * Status Values:
 * - PRESENT: Student attended on time
 * - LATE: Student attended but was late
 * - ABSENT: Student did not attend
 * - EXCUSED: Student was excused from attendance
 * 
 * Usage:
 * - Set when recording attendance
 * - Used for filtering and reporting
 * - Determines fine generation (ABSENT and LATE may incur fines)
 * 
 * @author transFINESy Development Team
 */
public enum AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    EXCUSED
}

