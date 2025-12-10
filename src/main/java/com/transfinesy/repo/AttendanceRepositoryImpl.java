package com.transfinesy.repo;

import com.transfinesy.config.DBConfig;
import com.transfinesy.model.Attendance;
import com.transfinesy.model.AttendanceStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of AttendanceRepository.
 */
public class AttendanceRepositoryImpl implements AttendanceRepository {

    @Override
    public List<Attendance> findAll() {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source FROM attendance";

        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("stud_id"),
                    rs.getString("event_id"),
                    AttendanceStatus.valueOf(rs.getString("status")),
                    rs.getInt("minutes_late")
                );
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                String scanSource = rs.getString("scan_source");
                if (checkIn != null) attendance.setCheckInTime(checkIn.toLocalDateTime());
                if (checkOut != null) attendance.setCheckOutTime(checkOut.toLocalDateTime());
                attendance.setScanSource(scanSource != null ? scanSource : "MANUAL");
                attendances.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendances;
    }

    @Override
    public Attendance findById(String attendanceID) {
        String sql = "SELECT attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source FROM attendance WHERE attendance_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, attendanceID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("stud_id"),
                    rs.getString("event_id"),
                    AttendanceStatus.valueOf(rs.getString("status")),
                    rs.getInt("minutes_late")
                );
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                String scanSource = rs.getString("scan_source");
                if (checkIn != null) attendance.setCheckInTime(checkIn.toLocalDateTime());
                if (checkOut != null) attendance.setCheckOutTime(checkOut.toLocalDateTime());
                attendance.setScanSource(scanSource != null ? scanSource : "MANUAL");
                return attendance;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Attendance> findByEvent(String eventID) {
        List<Attendance> attendances = new ArrayList<>();
        // Include scan_source in SELECT statement
        String sql = "SELECT attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source FROM attendance WHERE event_id = ? ORDER BY check_in_time DESC";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("stud_id"),
                    rs.getString("event_id"),
                    AttendanceStatus.valueOf(rs.getString("status")),
                    rs.getInt("minutes_late")
                );
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                String scanSource = null;
                try {
                    scanSource = rs.getString("scan_source");
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                    scanSource = "MANUAL";
                }
                if (checkIn != null) attendance.setCheckInTime(checkIn.toLocalDateTime());
                if (checkOut != null) attendance.setCheckOutTime(checkOut.toLocalDateTime());
                attendance.setScanSource(scanSource != null ? scanSource : "MANUAL");
                attendances.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching attendance for event: " + eventID);
            System.err.println("SQL Error: " + e.getMessage());
        }

        return attendances;
    }

    @Override
    public List<Attendance> findByStudent(String studID) {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source FROM attendance WHERE stud_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("stud_id"),
                    rs.getString("event_id"),
                    AttendanceStatus.valueOf(rs.getString("status")),
                    rs.getInt("minutes_late")
                );
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                String scanSource = rs.getString("scan_source");
                if (checkIn != null) attendance.setCheckInTime(checkIn.toLocalDateTime());
                if (checkOut != null) attendance.setCheckOutTime(checkOut.toLocalDateTime());
                attendance.setScanSource(scanSource != null ? scanSource : "MANUAL");
                attendances.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendances;
    }

    @Override
    public Attendance findByStudentAndEvent(String studID, String eventID) {
        String sql = "SELECT attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source FROM attendance WHERE stud_id = ? AND event_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studID);
            pstmt.setString(2, eventID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("stud_id"),
                    rs.getString("event_id"),
                    AttendanceStatus.valueOf(rs.getString("status")),
                    rs.getInt("minutes_late")
                );
                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");
                String scanSource = rs.getString("scan_source");
                if (checkIn != null) attendance.setCheckInTime(checkIn.toLocalDateTime());
                if (checkOut != null) attendance.setCheckOutTime(checkOut.toLocalDateTime());
                attendance.setScanSource(scanSource != null ? scanSource : "MANUAL");
                return attendance;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(Attendance a) {
        String sql = "INSERT INTO attendance (attendance_id, stud_id, event_id, status, minutes_late, check_in_time, check_out_time, scan_source) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getAttendanceID());
            pstmt.setString(2, a.getStudID());
            pstmt.setString(3, a.getEventID());
            pstmt.setString(4, a.getStatus().name());
            pstmt.setInt(5, a.getMinutesLate());
            pstmt.setTimestamp(6, a.getCheckInTime() != null ? Timestamp.valueOf(a.getCheckInTime()) : null);
            pstmt.setTimestamp(7, a.getCheckOutTime() != null ? Timestamp.valueOf(a.getCheckOutTime()) : null);
            pstmt.setString(8, a.getScanSource() != null ? a.getScanSource() : "MANUAL");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save attendance", e);
        }
    }

    @Override
    public void update(Attendance a) {
        String sql = "UPDATE attendance SET stud_id = ?, event_id = ?, status = ?, minutes_late = ?, check_in_time = ?, check_out_time = ?, scan_source = ? WHERE attendance_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, a.getStudID());
            pstmt.setString(2, a.getEventID());
            pstmt.setString(3, a.getStatus().name());
            pstmt.setInt(4, a.getMinutesLate());
            pstmt.setTimestamp(5, a.getCheckInTime() != null ? Timestamp.valueOf(a.getCheckInTime()) : null);
            pstmt.setTimestamp(6, a.getCheckOutTime() != null ? Timestamp.valueOf(a.getCheckOutTime()) : null);
            pstmt.setString(7, a.getScanSource() != null ? a.getScanSource() : "MANUAL");
            pstmt.setString(8, a.getAttendanceID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update attendance", e);
        }
    }

    @Override
    public void delete(String attendanceID) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, attendanceID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete attendance", e);
        }
    }
}

