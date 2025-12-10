package com.transfinesy.web;

import com.transfinesy.model.Attendance;
import com.transfinesy.model.AttendanceStatus;
import com.transfinesy.model.Event;
import com.transfinesy.model.Student;
import com.transfinesy.service.AttendanceService;
import com.transfinesy.service.EventService;
import com.transfinesy.service.LedgerService;
import com.transfinesy.service.ReportService;
import com.transfinesy.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ReportService reportService;
    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final EventService eventService;
    private final LedgerService ledgerService;

    public DashboardController(ReportService reportService, AttendanceService attendanceService, StudentService studentService, EventService eventService, LedgerService ledgerService) {
        this.reportService = reportService;
        this.attendanceService = attendanceService;
        this.studentService = studentService;
        this.eventService = eventService;
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String yearLevel,
            @RequestParam(required = false) String section,
            Model model) {
        YearMonth currentMonth = YearMonth.now();
        
        // Get all events for filter dropdown
        List<Event> allEvents = eventService.getAllEvents();
        
        // Get statistics
        double monthly = reportService.getMonthlyCollections(currentMonth);
        double semester = reportService.getSemesterCollections(currentMonth);
        
        // Total fines - sum all fines (not filtered by event for "All Events")
        double totalFines = reportService.getTotalFinesIssued();
        
        // If event filter is applied, calculate fines for that event only
        if (eventId != null && !eventId.isEmpty() && !eventId.equals("all")) {
            totalFines = reportService.getTotalFinesByEvent(eventId);
        }
        
        // Community Service statistics
        int totalServiceHours = reportService.getTotalServiceHours();
        double totalServiceCredits = reportService.getTotalServiceCredits();
        int monthlyServiceHours = reportService.getServiceHoursForMonth(currentMonth);
        double monthlyServiceCredits = reportService.getServiceCreditsForMonth(currentMonth);

        // Count attendance statuses - filter by event if selected
        // Count unique students per status to avoid double counting
        List<Attendance> allAttendance = attendanceService.getAllAttendance();
        if (eventId != null && !eventId.isEmpty() && !eventId.equals("all")) {
            allAttendance = allAttendance.stream()
                .filter(a -> a.getEventID() != null && a.getEventID().equals(eventId))
                .collect(Collectors.toList());
        }
        
        // Count unique students per status (one student can only be counted once per event)
        long presentCount = allAttendance.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
            .map(Attendance::getStudID)
            .distinct()
            .count();
        long lateCount = allAttendance.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.LATE)
            .map(Attendance::getStudID)
            .distinct()
            .count();
        long absentCount = allAttendance.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
            .map(Attendance::getStudID)
            .distinct()
            .count();

        // Get course breakdown - use filtered methods if year level or section filter is applied
        List<Student> allStudents = studentService.getAllStudents();
        Map<String, Double> finesByCourse;
        Map<String, Double> paymentsByCourse;
        
        // Use filtered methods if year level or section filter is applied
        if ((yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) ||
            (section != null && !section.isEmpty() && !section.equals("all"))) {
            // Use filtered methods that properly calculate fines/payments for filtered students
            finesByCourse = reportService.getTotalsByCourseFiltered(yearLevel, section);
            paymentsByCourse = reportService.getPaymentsByCourseFiltered(yearLevel, section);
        } else {
            // No filters - get all totals
            finesByCourse = reportService.getTotalsByCourse();
            paymentsByCourse = reportService.getPaymentsByCourse();
        }

        // Get unique year levels and sections for filters
        List<String> yearLevels = allStudents.stream()
            .map(Student::getYearLevel)
            .filter(yl -> yl != null && !yl.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        List<String> sections = allStudents.stream()
            .map(Student::getSection)
            .filter(s -> s != null && !s.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("monthlyCollections", monthly);
        model.addAttribute("semesterCollections", semester);
        model.addAttribute("totalFines", totalFines);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("lateCount", lateCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("finesByCourse", finesByCourse);
        model.addAttribute("paymentsByCourse", paymentsByCourse);
        model.addAttribute("allEvents", allEvents);
        model.addAttribute("selectedEventId", eventId != null ? eventId : "all");
        model.addAttribute("yearLevels", yearLevels);
        model.addAttribute("sections", sections);
        model.addAttribute("selectedYearLevel", yearLevel != null ? yearLevel : "all");
        model.addAttribute("selectedSection", section != null ? section : "all");
        // Community Service stats
        model.addAttribute("totalServiceHours", totalServiceHours);
        model.addAttribute("totalServiceCredits", totalServiceCredits);
        model.addAttribute("monthlyServiceHours", monthlyServiceHours);
        model.addAttribute("monthlyServiceCredits", monthlyServiceCredits);

        return "dashboard/index";
    }
}

