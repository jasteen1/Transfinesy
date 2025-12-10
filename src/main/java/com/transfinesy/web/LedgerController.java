package com.transfinesy.web;

import com.transfinesy.model.Ledger;
import com.transfinesy.model.Student;
import com.transfinesy.service.ClearanceService;
import com.transfinesy.service.LedgerService;
import com.transfinesy.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;
    private final ClearanceService clearanceService;
    private final StudentService studentService;

    public LedgerController(LedgerService ledgerService, ClearanceService clearanceService, StudentService studentService) {
        this.ledgerService = ledgerService;
        this.clearanceService = clearanceService;
        this.studentService = studentService;
    }

    @GetMapping
    public String viewLedger(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "All Fields") String searchType,
            Model model) {
        
        List<Student> students = studentService.getAllStudents();
        
        // Apply search filter if provided
        if (search != null && !search.trim().isEmpty()) {
            switch (searchType) {
                case "Student ID":
                    // Only matches Student ID field
                    students = studentService.searchByStudentID(search);
                    break;
                case "Name":
                    // Only matches first_name and last_name fields (restricted search)
                    students = studentService.searchByNameOnly(search);
                    break;
                case "Course":
                    // Only matches course field
                    students = studentService.searchByCourse(search);
                    break;
                case "Year Level":
                    // Only matches year_level field
                    students = studentService.searchByYearLevel(search);
                    break;
                case "Section":
                    // Only matches section field
                    students = studentService.searchBySection(search);
                    break;
                case "RFID Tag":
                    // Only matches RFID field with partial matching
                    students = studentService.searchByRFID(search);
                    break;
                case "All Fields":
                default:
                    // Searches all fields including RFID
                    students = studentService.searchStudents(search);
                    break;
            }
        }

        Ledger ledger = null;
        String clearanceStatus = "-";
        Student selectedStudent = null;

        if (studentId != null && !studentId.trim().isEmpty()) {
            selectedStudent = studentService.getStudentById(studentId);
            if (selectedStudent == null) {
                model.addAttribute("errorMessage", "No matching student found.");
            } else {
                ledger = ledgerService.getLedgerForStudent(studentId);
                clearanceStatus = clearanceService.getClearanceStatusWithBalance(selectedStudent);
            }
        }
        
        // Check if search returned no results
        if (search != null && !search.trim().isEmpty() && students.isEmpty()) {
            model.addAttribute("errorMessage", "No matching student found.");
        }

        model.addAttribute("pageTitle", "Ledger & Transactions");
        model.addAttribute("activePage", "ledger");
        model.addAttribute("students", students);
        model.addAttribute("selectedStudent", selectedStudent);
        model.addAttribute("ledger", ledger);
        model.addAttribute("clearanceStatus", clearanceStatus);
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);

        return "ledger/view";
    }
}






