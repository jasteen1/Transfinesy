package com.transfinesy.web;

import com.transfinesy.model.CommunityService;
import com.transfinesy.model.Student;
import com.transfinesy.service.CommunityServiceService;
import com.transfinesy.service.LedgerService;
import com.transfinesy.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/community-service")
public class CommunityServiceController {

    private final CommunityServiceService serviceService;
    private final StudentService studentService;
    private final LedgerService ledgerService;

    public CommunityServiceController(CommunityServiceService serviceService, StudentService studentService, LedgerService ledgerService) {
        this.serviceService = serviceService;
        this.studentService = studentService;
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public String listServices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "All Fields") String searchType,
            Model model) {
        
        List<CommunityService> services = serviceService.getAllServices();
        
        // Apply search filter if provided
        if (search != null && !search.trim().isEmpty()) {
            List<CommunityService> filteredServices = services.stream()
                .filter(service -> {
                    Student student = studentService.getStudentById(service.getStudID());
                    String studentName = student != null ? student.getFullName() : "";
                    
                    switch (searchType) {
                        case "All Fields":
                            return service.getServiceID().toLowerCase().contains(search.toLowerCase()) ||
                                   service.getStudID().toLowerCase().contains(search.toLowerCase()) ||
                                   studentName.toLowerCase().contains(search.toLowerCase()) ||
                                   (student != null && student.getCourse().toLowerCase().contains(search.toLowerCase())) ||
                                   (student != null && student.getYearLevel().toLowerCase().contains(search.toLowerCase())) ||
                                   (student != null && student.getSection().toLowerCase().contains(search.toLowerCase())) ||
                                   (student != null && student.getRfidTag() != null && student.getRfidTag().toLowerCase().contains(search.toLowerCase()));
                        case "Student ID":
                            return service.getStudID().toLowerCase().contains(search.toLowerCase());
                        case "Student Name":
                            return studentName.toLowerCase().contains(search.toLowerCase());
                        case "Service ID":
                            return service.getServiceID().toLowerCase().contains(search.toLowerCase());
                        case "Course":
                            return student != null && student.getCourse().toLowerCase().contains(search.toLowerCase());
                        case "Year Level":
                            return student != null && student.getYearLevel().toLowerCase().contains(search.toLowerCase());
                        case "Section":
                            return student != null && student.getSection().toLowerCase().contains(search.toLowerCase());
                        case "RFID Tag":
                            return student != null && student.getRfidTag() != null && student.getRfidTag().toLowerCase().contains(search.toLowerCase());
                        default:
                            return true;
                    }
                })
                .toList();
            services = filteredServices;
        }

        List<Student> students = studentService.getAllStudents();
        
        // Create a map for easy lookup in template
        Map<String, Student> studentMap = new HashMap<>();
        for (Student student : students) {
            studentMap.put(student.getStudID(), student);
        }

        model.addAttribute("pageTitle", "Community Service");
        model.addAttribute("activePage", "community-service");
        model.addAttribute("services", services);
        model.addAttribute("students", students);
        model.addAttribute("studentMap", studentMap);
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);
        model.addAttribute("today", LocalDate.now());

        return "community-service/list";
    }

    @PostMapping("/save")
    public String saveService(
            @RequestParam String studentId,
            @RequestParam int hours,
            @RequestParam String date,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            // Removed balance check - allow community service recording regardless of balance
            // This allows advance service credits and corrections
            if (studentId == null || studentId.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid input: Student ID is required.");
                return "redirect:/community-service";
            }
            if (hours <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid input: Hours must be positive.");
                return "redirect:/community-service";
            }
            
            LocalDate serviceDate = LocalDate.parse(date);
            serviceService.recordCommunityService(studentId, hours, serviceDate, description);
            redirectAttributes.addFlashAttribute("successMessage", "Community service recorded successfully. Ledger updated.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording service: " + e.getMessage());
        }
        return "redirect:/community-service";
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(id);
            redirectAttributes.addFlashAttribute("successMessage", "Community service record deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting service: " + e.getMessage());
        }
        return "redirect:/community-service";
    }
}





