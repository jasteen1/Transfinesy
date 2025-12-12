package com.transfinesy.service;

import com.transfinesy.model.CommunityService;
import com.transfinesy.model.Fine;
import com.transfinesy.model.Payment;
import com.transfinesy.model.Student;
import com.transfinesy.repo.CommunityServiceRepository;
import com.transfinesy.repo.CommunityServiceRepositoryImpl;
import com.transfinesy.repo.FineRepository;
import com.transfinesy.repo.FineRepositoryImpl;
import com.transfinesy.repo.PaymentRepository;
import com.transfinesy.repo.PaymentRepositoryImpl;
import com.transfinesy.repo.StudentRepository;
import com.transfinesy.repo.StudentRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for generating reports and transparency dashboards.
 */
@Service
public class ReportService {
    private FineRepository fineRepository;
    private PaymentRepository paymentRepository;
    private StudentRepository studentRepository;
    private CommunityServiceRepository serviceRepository;

    public ReportService() {
        this.fineRepository = new FineRepositoryImpl();
        this.paymentRepository = new PaymentRepositoryImpl();
        this.studentRepository = new StudentRepositoryImpl();
        this.serviceRepository = new CommunityServiceRepositoryImpl();
    }

    /**
     * Gets total collections for a specific month (OPTIMIZED: uses SQL aggregation).
     */
    public double getMonthlyCollections(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return paymentRepository.getSumByDateRange(start, end);
    }

    /**
     * Gets total collections for a specific month filtered by year level and section.
     */
    public double getMonthlyCollectionsFiltered(YearMonth yearMonth, String yearLevel, String section) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<Payment> allPayments = paymentRepository.findAll();
        
        // Build student filter maps
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        for (Student student : allStudents) {
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        // Filter and sum payments
        return allPayments.stream()
                .filter(p -> !p.getDate().isBefore(start) && !p.getDate().isAfter(end))
                .filter(p -> {
                    String studentYear = studentYearMap.get(p.getStudID());
                    String studentSection = studentSectionMap.get(p.getStudID());
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Gets total collections for the current semester.
     * Assumes semester starts in January and June.
     */
    public double getSemesterCollections(YearMonth currentMonth) {
        YearMonth semesterStart;
        int month = currentMonth.getMonthValue();
        if (month >= 1 && month <= 5) {
            semesterStart = YearMonth.of(currentMonth.getYear(), 1);
        } else {
            semesterStart = YearMonth.of(currentMonth.getYear(), 6);
        }

        double total = 0.0;
        YearMonth current = semesterStart;
        while (!current.isAfter(currentMonth)) {
            total += getMonthlyCollections(current);
            current = current.plusMonths(1);
        }
        return total;
    }

    /**
     * Gets total collections for the current semester filtered by year level and section.
     */
    public double getSemesterCollectionsFiltered(YearMonth currentMonth, String yearLevel, String section) {
        YearMonth semesterStart;
        int month = currentMonth.getMonthValue();
        if (month >= 1 && month <= 5) {
            semesterStart = YearMonth.of(currentMonth.getYear(), 1);
        } else {
            semesterStart = YearMonth.of(currentMonth.getYear(), 6);
        }

        double total = 0.0;
        YearMonth current = semesterStart;
        while (!current.isAfter(currentMonth)) {
            total += getMonthlyCollectionsFiltered(current, yearLevel, section);
            current = current.plusMonths(1);
        }
        return total;
    }

    /**
     * Gets total fines issued (all time or for a period) - OPTIMIZED: uses SQL aggregation.
     */
    public double getTotalFinesIssued() {
        return fineRepository.getTotalSum();
    }

    /**
     * Gets total fines issued filtered by course, year level and section.
     */
    public double getTotalFinesIssuedFiltered(String course, String yearLevel, String section) {
        List<Fine> allFines = fineRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return allFines.stream()
                .filter(f -> {
                    String studentCourse = studentCourseMap.get(f.getStudID());
                    String studentYear = studentYearMap.get(f.getStudID());
                    String studentSection = studentSectionMap.get(f.getStudID());
                    
                    if (course != null && !course.isEmpty() && !course.equals("all")) {
                        if (studentCourse == null || !studentCourse.equals(course)) {
                            return false;
                        }
                    }
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToDouble(Fine::getFineAmount)
                .sum();
    }
    
    /**
     * Gets total fines for a specific event - OPTIMIZED: uses SQL aggregation.
     */
    public double getTotalFinesByEvent(String eventID) {
        return fineRepository.getSumByEvent(eventID);
    }

    /**
     * Gets total fines for a specific event filtered by year level and section.
     */
    public double getTotalFinesByEventFiltered(String eventID, String yearLevel, String section) {
        List<Fine> eventFines = fineRepository.findByEvent(eventID);
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return eventFines.stream()
                .filter(f -> {
                    String studentYear = studentYearMap.get(f.getStudID());
                    String studentSection = studentSectionMap.get(f.getStudID());
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToDouble(Fine::getFineAmount)
                .sum();
    }

    /**
     * Gets total payments collected (all time) - OPTIMIZED: uses SQL aggregation.
     */
    public double getTotalPayments() {
        return paymentRepository.getTotalSum();
    }

    /**
     * Gets total payments collected filtered by course, year level and section.
     */
    public double getTotalPaymentsFiltered(String course, String yearLevel, String section) {
        List<Payment> allPayments = paymentRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return allPayments.stream()
                .filter(p -> {
                    String studentCourse = studentCourseMap.get(p.getStudID());
                    String studentYear = studentYearMap.get(p.getStudID());
                    String studentSection = studentSectionMap.get(p.getStudID());
                    
                    if (course != null && !course.isEmpty() && !course.equals("all")) {
                        if (studentCourse == null || !studentCourse.equals(course)) {
                            return false;
                        }
                    }
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Gets total service credits filtered by course, year level and section.
     */
    public double getTotalServiceCreditsFiltered(String course, String yearLevel, String section) {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return allServices.stream()
                .filter(s -> {
                    String studentCourse = studentCourseMap.get(s.getStudID());
                    String studentYear = studentYearMap.get(s.getStudID());
                    String studentSection = studentSectionMap.get(s.getStudID());
                    
                    if (course != null && !course.isEmpty() && !course.equals("all")) {
                        if (studentCourse == null || !studentCourse.equals(course)) {
                            return false;
                        }
                    }
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToDouble(CommunityService::getCreditAmount)
                .sum();
    }

    /**
     * Gets total service hours filtered by course, year level and section.
     */
    public int getTotalServiceHoursFiltered(String course, String yearLevel, String section) {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return allServices.stream()
                .filter(s -> {
                    String studentCourse = studentCourseMap.get(s.getStudID());
                    String studentYear = studentYearMap.get(s.getStudID());
                    String studentSection = studentSectionMap.get(s.getStudID());
                    
                    if (course != null && !course.isEmpty() && !course.equals("all")) {
                        if (studentCourse == null || !studentCourse.equals(course)) {
                            return false;
                        }
                    }
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .mapToInt(CommunityService::getHoursRendered)
                .sum();
    }

    /**
     * Gets count of unique students who rendered community service.
     */
    public long getServiceStudentCount() {
        List<CommunityService> allServices = serviceRepository.findAll();
        return allServices.stream()
                .map(CommunityService::getStudID)
                .distinct()
                .count();
    }

    /**
     * Gets count of unique students who rendered community service (filtered by course, year level and section).
     */
    public long getServiceStudentCountFiltered(String course, String yearLevel, String section) {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        return allServices.stream()
                .filter(s -> {
                    String studentCourse = studentCourseMap.get(s.getStudID());
                    String studentYear = studentYearMap.get(s.getStudID());
                    String studentSection = studentSectionMap.get(s.getStudID());
                    
                    if (course != null && !course.isEmpty() && !course.equals("all")) {
                        if (studentCourse == null || !studentCourse.equals(course)) {
                            return false;
                        }
                    }
                    
                    if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                        if (studentYear == null || !studentYear.equals(yearLevel)) {
                            return false;
                        }
                    }
                    
                    if (section != null && !section.isEmpty() && !section.equals("all")) {
                        if (studentSection == null || !studentSection.equals(section)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .map(CommunityService::getStudID)
                .distinct()
                .count();
    }

    /**
     * Calculates outstanding balance: Total Fines - Total Payments - Total Service Credits
     * Returns 0 if the result is negative (overpaid/over-credited).
     * @param totalFines Total fines issued
     * @param totalPayments Total payments collected
     * @param totalServiceCredits Total service credit value
     * @return Outstanding balance (never negative, minimum 0)
     */
    public double calculateOutstandingBalance(double totalFines, double totalPayments, double totalServiceCredits) {
        double balance = totalFines - totalPayments - totalServiceCredits;
        return Math.max(0.0, balance); // Return 0 if balance is negative
    }
    
    /**
     * Gets totals by course with year level and section filtering.
     * Only includes fines from ABSENT and valid LATE calculations.
     */
    public Map<String, Double> getTotalsByCourseFiltered(String yearLevel, String section) {
        Map<String, Double> totals = new HashMap<>();
        
        // Load all fines once
        List<Fine> allFines = fineRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        // Build student mappings
        List<Student> allStudents = studentRepository.findAll();
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        // Group fines by course with filters
        for (Fine fine : allFines) {
            String studentYear = studentYearMap.get(fine.getStudID());
            String studentSection = studentSectionMap.get(fine.getStudID());
            String course = studentCourseMap.get(fine.getStudID());
            
            // Apply year level filter
            if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                if (studentYear == null || !studentYear.equals(yearLevel)) {
                    continue; // Skip this fine if year level doesn't match
                }
            }
            
            // Apply section filter
            if (section != null && !section.isEmpty() && !section.equals("all")) {
                if (studentSection == null || !studentSection.equals(section)) {
                    continue; // Skip this fine if section doesn't match
                }
            }
            
            // Only count fines from ABSENT and valid LATE (fines with amount > 0)
            if (course != null && !course.isEmpty() && fine.getFineAmount() > 0) {
                totals.put(course, totals.getOrDefault(course, 0.0) + fine.getFineAmount());
            }
        }
        
        return totals;
    }
    
    /**
     * Gets payments by course with year level and section filtering.
     */
    public Map<String, Double> getPaymentsByCourseFiltered(String yearLevel, String section) {
        Map<String, Double> totals = new HashMap<>();
        
        // Load all payments once
        List<Payment> allPayments = paymentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        Map<String, String> studentYearMap = new HashMap<>();
        Map<String, String> studentSectionMap = new HashMap<>();
        
        // Build student mappings
        List<Student> allStudents = studentRepository.findAll();
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
            studentYearMap.put(student.getStudID(), student.getYearLevel());
            studentSectionMap.put(student.getStudID(), student.getSection());
        }
        
        // Group payments by course with filters
        for (Payment payment : allPayments) {
            String studentYear = studentYearMap.get(payment.getStudID());
            String studentSection = studentSectionMap.get(payment.getStudID());
            String course = studentCourseMap.get(payment.getStudID());
            
            // Apply year level filter
            if (yearLevel != null && !yearLevel.isEmpty() && !yearLevel.equals("all")) {
                if (studentYear == null || !studentYear.equals(yearLevel)) {
                    continue; // Skip this payment if year level doesn't match
                }
            }
            
            // Apply section filter
            if (section != null && !section.isEmpty() && !section.equals("all")) {
                if (studentSection == null || !studentSection.equals(section)) {
                    continue; // Skip this payment if section doesn't match
                }
            }
            
            if (course != null && !course.isEmpty()) {
                totals.put(course, totals.getOrDefault(course, 0.0) + payment.getAmount());
            }
        }
        
        return totals;
    }

    /**
     * Gets total service credits issued (monetary value) - OPTIMIZED: uses SQL aggregation.
     */
    public double getTotalServiceCredits() {
        return serviceRepository.getTotalCredits();
    }

    /**
     * Gets total community service hours rendered - OPTIMIZED: uses SQL aggregation.
     */
    public int getTotalServiceHours() {
        return serviceRepository.getTotalHours();
    }

    /**
     * Gets total service hours for a specific day.
     */
    public int getServiceHoursForDay(LocalDate date) {
        List<CommunityService> allServices = serviceRepository.findAll();
        return allServices.stream()
                .filter(s -> s.getDate().equals(date))
                .mapToInt(CommunityService::getHoursRendered)
                .sum();
    }

    /**
     * Gets total service hours for a specific month - OPTIMIZED: uses SQL aggregation.
     */
    public int getServiceHoursForMonth(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return serviceRepository.getHoursByDateRange(start, end);
    }

    /**
     * Gets total service hours for current semester.
     */
    public int getServiceHoursForSemester(YearMonth currentMonth) {
        YearMonth semesterStart;
        int month = currentMonth.getMonthValue();
        if (month >= 1 && month <= 5) {
            semesterStart = YearMonth.of(currentMonth.getYear(), 1);
        } else {
            semesterStart = YearMonth.of(currentMonth.getYear(), 6);
        }

        int total = 0;
        YearMonth current = semesterStart;
        while (!current.isAfter(currentMonth)) {
            total += getServiceHoursForMonth(current);
            current = current.plusMonths(1);
        }
        return total;
    }

    /**
     * Gets total service hours for a specific year.
     */
    public int getServiceHoursForYear(int year) {
        List<CommunityService> allServices = serviceRepository.findAll();
        return allServices.stream()
                .filter(s -> s.getDate().getYear() == year)
                .mapToInt(CommunityService::getHoursRendered)
                .sum();
    }

    /**
     * Gets total monetary equivalent of community service for a month - OPTIMIZED: uses SQL aggregation.
     */
    public double getServiceCreditsForMonth(YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return serviceRepository.getCreditsByDateRange(start, end);
    }

    /**
     * Gets top N students with highest community service contributions.
     */
    public List<Map<String, Object>> getTopServiceContributors(int limit) {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, Student> studentMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getStudID, s -> s));

        // Group by student and sum hours/credits
        Map<String, Integer> hoursByStudent = new HashMap<>();
        Map<String, Double> creditsByStudent = new HashMap<>();

        for (CommunityService service : allServices) {
            hoursByStudent.put(service.getStudID(),
                    hoursByStudent.getOrDefault(service.getStudID(), 0) + service.getHoursRendered());
            creditsByStudent.put(service.getStudID(),
                    creditsByStudent.getOrDefault(service.getStudID(), 0.0) + service.getCreditAmount());
        }

        // Sort by credits (descending) and take top N
        return creditsByStudent.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    Student student = studentMap.get(entry.getKey());
                    result.put("studID", entry.getKey());
                    result.put("studentName", student != null ? student.getFullName() : entry.getKey());
                    result.put("totalHours", hoursByStudent.getOrDefault(entry.getKey(), 0));
                    result.put("totalCredits", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets community service breakdown by course.
     */
    public Map<String, Integer> getServiceHoursByCourse() {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentCourseMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getStudID, Student::getCourse));

        Map<String, Integer> hoursByCourse = new HashMap<>();
        for (CommunityService service : allServices) {
            String course = studentCourseMap.get(service.getStudID());
            if (course != null && !course.isEmpty()) {
                hoursByCourse.put(course, hoursByCourse.getOrDefault(course, 0) + service.getHoursRendered());
            }
        }
        return hoursByCourse;
    }

    /**
     * Gets community service breakdown by year level.
     */
    public Map<String, Integer> getServiceHoursByYearLevel() {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentYearMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getStudID, Student::getYearLevel));

        Map<String, Integer> hoursByYear = new HashMap<>();
        for (CommunityService service : allServices) {
            String yearLevel = studentYearMap.get(service.getStudID());
            if (yearLevel != null && !yearLevel.isEmpty()) {
                hoursByYear.put(yearLevel, hoursByYear.getOrDefault(yearLevel, 0) + service.getHoursRendered());
            }
        }
        return hoursByYear;
    }

    /**
     * Gets community service breakdown by section.
     */
    public Map<String, Integer> getServiceHoursBySection() {
        List<CommunityService> allServices = serviceRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        Map<String, String> studentSectionMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getStudID, Student::getSection));

        Map<String, Integer> hoursBySection = new HashMap<>();
        for (CommunityService service : allServices) {
            String section = studentSectionMap.get(service.getStudID());
            if (section != null && !section.isEmpty()) {
                hoursBySection.put(section, hoursBySection.getOrDefault(section, 0) + service.getHoursRendered());
            }
        }
        return hoursBySection;
    }

    /**
     * Gets total fines by course - OPTIMIZED: batch query instead of N+1
     */
    public double getTotalFinesByCourse(String course) {
        List<Student> students = studentRepository.findByCourse(course);
        if (students.isEmpty()) return 0.0;
        
        // Batch query: get all fines for all students in this course at once
        List<Fine> allFines = fineRepository.findAll();
        Set<String> studentIds = students.stream()
            .map(Student::getStudID)
            .collect(Collectors.toSet());
        
        return allFines.stream()
            .filter(f -> studentIds.contains(f.getStudID()))
            .mapToDouble(Fine::getFineAmount)
            .sum();
    }

    /**
     * Gets totals by course (fines, payments, etc.) - OPTIMIZED: single pass
     */
    public Map<String, Double> getTotalsByCourse() {
        Map<String, Double> totals = new HashMap<>();
        
        // OPTIMIZED: Load all fines once, then group by student course
        List<Fine> allFines = fineRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        
        // Build student ID to course mapping
        List<Student> allStudents = studentRepository.findAll();
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
        }
        
        // Group fines by course in single pass
        for (Fine fine : allFines) {
            String course = studentCourseMap.get(fine.getStudID());
            if (course != null && !course.isEmpty()) {
                totals.put(course, totals.getOrDefault(course, 0.0) + fine.getFineAmount());
            }
        }

        return totals;
    }

    /**
     * Gets total payments by course - OPTIMIZED: single pass
     */
    public Map<String, Double> getPaymentsByCourse() {
        Map<String, Double> totals = new HashMap<>();
        
        // OPTIMIZED: Load all payments once, then group by student course
        List<Payment> allPayments = paymentRepository.findAll();
        Map<String, String> studentCourseMap = new HashMap<>();
        
        // Build student ID to course mapping
        List<Student> allStudents = studentRepository.findAll();
        for (Student student : allStudents) {
            studentCourseMap.put(student.getStudID(), student.getCourse());
        }
        
        // Group payments by course in single pass
        for (Payment payment : allPayments) {
            String course = studentCourseMap.get(payment.getStudID());
            if (course != null && !course.isEmpty()) {
                totals.put(course, totals.getOrDefault(course, 0.0) + payment.getAmount());
            }
        }

        return totals;
    }
}

