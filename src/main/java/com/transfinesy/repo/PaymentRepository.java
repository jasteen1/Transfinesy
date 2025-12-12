package com.transfinesy.repo;

import com.transfinesy.model.Payment;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Payment operations.
 */
public interface PaymentRepository {
    List<Payment> findAll();
    Payment findById(String paymentID);
    List<Payment> findByStudent(String studID);
    void save(Payment p);
    void update(Payment p);
    void delete(String paymentID);
    
    /**
     * Gets sum of payments for a specific month (optimized SQL aggregation).
     * @param startDate Start date of the month
     * @param endDate End date of the month
     * @return Sum of payment amounts
     */
    double getSumByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets sum of all payments (optimized SQL aggregation).
     * @return Sum of all payment amounts
     */
    double getTotalSum();
}

