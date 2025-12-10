package com.transfinesy.repo;

import com.transfinesy.model.Payment;
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
}

