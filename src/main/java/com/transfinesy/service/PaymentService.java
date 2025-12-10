package com.transfinesy.service;

import org.springframework.stereotype.Service;

import com.transfinesy.model.Payment;
import com.transfinesy.repo.PaymentRepository;
import com.transfinesy.repo.PaymentRepositoryImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for Payment operations.
 */
@Service
public class PaymentService {
    private PaymentRepository repository;
    private LedgerService ledgerService;

    public PaymentService() {
        this.repository = new PaymentRepositoryImpl();
        this.ledgerService = new LedgerService();
    }

    public PaymentService(LedgerService ledgerService) {
        this.repository = new PaymentRepositoryImpl();
        this.ledgerService = ledgerService;
    }

    /**
     * Records a payment and updates the ledger.
     */
    public void recordPayment(String studID, double amount, String orNumber, LocalDate date) {
        if (studID == null || studID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (orNumber == null || orNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("OR Number is required");
        }
        // OR Number validation: digits only
        if (!orNumber.matches("^\\d+$")) {
            throw new IllegalArgumentException("OR Number must contain digits only");
        }

        String paymentID = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String transactionID = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment(paymentID, transactionID, studID, amount, orNumber, date);
        repository.save(payment);
        ledgerService.addTransactionToLedger(studID, payment);
    }
    
    /**
     * Updates an existing payment and triggers ledger recalculation.
     */
    public void updatePayment(String paymentID, String studID, double amount, String orNumber, LocalDate date) {
        if (paymentID == null || paymentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        if (studID == null || studID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (orNumber == null || orNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("OR Number is required");
        }
        // OR Number validation: digits only
        if (!orNumber.matches("^\\d+$")) {
            throw new IllegalArgumentException("OR Number must contain digits only");
        }
        
        Payment existingPayment = repository.findById(paymentID);
        if (existingPayment == null) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentID);
        }
        
        // Update payment
        existingPayment.setStudID(studID);
        existingPayment.setAmount(amount);
        existingPayment.setOrNumber(orNumber);
        existingPayment.setDate(date);
        
        repository.update(existingPayment);
        // Ledger will be recalculated when accessed (it's rebuilt from database)
    }
    
    /**
     * Deletes a payment and triggers ledger recalculation.
     */
    public void deletePayment(String paymentID) {
        if (paymentID == null || paymentID.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        
        Payment payment = repository.findById(paymentID);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found with ID: " + paymentID);
        }
        
        repository.delete(paymentID);
        // Ledger will be recalculated when accessed (it's rebuilt from database)
    }

    public List<Payment> getPaymentsByStudent(String studID) {
        return repository.findByStudent(studID);
    }

    public Payment getPaymentById(String paymentID) {
        return repository.findById(paymentID);
    }

    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

}

