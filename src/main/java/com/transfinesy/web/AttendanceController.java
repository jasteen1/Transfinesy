package com.transfinesy.web;

import com.transfinesy.model.Attendance;
import com.transfinesy.model.Event;
import com.transfinesy.model.Student;
import com.transfinesy.service.AttendanceService;
import com.transfinesy.service.EventService;
import com.transfinesy.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EventService eventService;
    private final StudentService studentService;

    public AttendanceController(AttendanceService attendanceService, EventService eventService, 
                                 StudentService studentService) {
        this.attendanceService = attendanceService;
        this.eventService = eventService;
        this.studentService = studentService;
    }

    @GetMapping("/event/{eventId}")
    public String eventAttendance(@PathVariable String eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            return "redirect:/events";
        }

        List<Attendance> allAttendances = attendanceService.getAttendanceByEvent(eventId);
        List<Student> allStudents = studentService.getAllStudents();
        
        // Create a map for easy student lookup in template
        Map<String, Student> studentMap = new HashMap<>();
        for (Student student : allStudents) {
            studentMap.put(student.getStudID(), student);
        }

        // Separate attendances by session (AM, PM, and legacy records without session)
        List<Attendance> amAttendances = new java.util.ArrayList<>();
        List<Attendance> pmAttendances = new java.util.ArrayList<>();
        List<Attendance> legacyAttendances = new java.util.ArrayList<>();
        
        for (Attendance attendance : allAttendances) {
            String session = attendance.getSession();
            if (session != null) {
                if ("AM".equalsIgnoreCase(session)) {
                    amAttendances.add(attendance);
                } else if ("PM".equalsIgnoreCase(session)) {
                    pmAttendances.add(attendance);
                } else {
                    legacyAttendances.add(attendance);
                }
            } else {
                // Legacy records without session field
                legacyAttendances.add(attendance);
            }
        }

        model.addAttribute("pageTitle", "Event Attendance");
        model.addAttribute("activePage", "events");
        model.addAttribute("event", event);
        model.addAttribute("attendances", allAttendances); // Keep for backward compatibility
        model.addAttribute("amAttendances", amAttendances);
        model.addAttribute("pmAttendances", pmAttendances);
        model.addAttribute("legacyAttendances", legacyAttendances);
        model.addAttribute("students", allStudents);
        model.addAttribute("studentMap", studentMap);

        return "attendance/event";
    }

    @PostMapping("/scan-rfid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scanRFID(@RequestParam String rfidTag, @RequestParam String eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate inputs
            if (rfidTag == null || rfidTag.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "RFID tag is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (eventId == null || eventId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Event ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Trim and clean the eventId
            eventId = eventId.trim();
            
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                response.put("success", false);
                response.put("message", "Event not found with ID: " + eventId);
                return ResponseEntity.badRequest().body(response);
            }

            Attendance attendance = attendanceService.scanRFID(rfidTag, event);
            Student student = studentService.getStudentById(attendance.getStudID());

            response.put("success", true);
            response.put("message", "Attendance recorded successfully");
            response.put("studentName", student.getFullName());
            response.put("status", attendance.getStatus().toString());
            response.put("minutesLate", attendance.getMinutesLate());
            response.put("checkInTime", attendance.getCheckInTime());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/finalize/{eventId}")
    public String finalizeEvent(@PathVariable String eventId, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.finalizeEventAttendance(eventId);
            redirectAttributes.addFlashAttribute("successMessage", "Event finalized successfully. Fines have been generated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error finalizing event: " + e.getMessage());
        }
        return "redirect:/attendance/event/" + eventId;
    }

    // ========== NEW START-STOP ATTENDANCE ENDPOINTS ==========

    @PostMapping("/scan-rfid-window")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scanRFIDWithWindow(
            @RequestParam String rfidTag,
            @RequestParam String eventId,
            @RequestParam String sessionType,
            @RequestParam(defaultValue = "true") boolean isTimeIn) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate inputs
            if (rfidTag == null || rfidTag.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "RFID tag is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (eventId == null || eventId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Event ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Trim and clean the eventId
            eventId = eventId.trim();
            
            Event event = eventService.getEventById(eventId);
            if (event == null) {
                response.put("success", false);
                response.put("message", "Event not found with ID: " + eventId);
                return ResponseEntity.badRequest().body(response);
            }

            Attendance attendance = attendanceService.scanRFIDWithWindow(rfidTag, event, sessionType, isTimeIn);
            Student student = studentService.getStudentById(attendance.getStudID());

            response.put("success", true);
            response.put("message", "Attendance recorded successfully");
            response.put("studentName", student.getFullName());
            response.put("status", attendance.getStatus().toString());
            response.put("minutesLate", attendance.getMinutesLate());
            response.put("checkInTime", attendance.getCheckInTime());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/mark-absentees/{eventId}")
    public String markAbsentees(
            @PathVariable String eventId,
            @RequestParam String sessionType,
            @RequestParam(defaultValue = "true") boolean isTimeIn,
            RedirectAttributes redirectAttributes) {
        try {
            attendanceService.markSessionAbsentees(eventId, sessionType, isTimeIn);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Absentees marked for " + sessionType + " " + (isTimeIn ? "Time-In" : "Time-Out") + " session.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error marking absentees: " + e.getMessage());
        }
        return "redirect:/attendance/event/" + eventId;
    }
}

