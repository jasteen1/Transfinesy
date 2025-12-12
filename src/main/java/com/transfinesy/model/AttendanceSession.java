package com.transfinesy.model;

/**
 * Enum representing the attendance session type for an event.
 * 
 * This enum defines which sessions an event supports for attendance tracking.
 * 
 * Session Types:
 * - MORNING_ONLY: Event only has a morning session
 * - AFTERNOON_ONLY: Event only has an afternoon session
 * - BOTH: Event has both morning and afternoon sessions
 * 
 * Usage:
 * - Configured when creating/editing events
 * - Determines which time windows are active
 * - Used to separate AM and PM attendance records
 * 
 * @author transFINESy Development Team
 */
public enum AttendanceSession {
    MORNING_ONLY,
    AFTERNOON_ONLY,
    BOTH
}

