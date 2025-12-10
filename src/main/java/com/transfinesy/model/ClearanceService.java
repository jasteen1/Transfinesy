package com.transfinesy.model;

/**
 * Service class for checking clearance eligibility.
 * Note: This is a model class, but the actual business logic
 * will be in com.transfinesy.service.ClearanceService.
 * This class is kept here as per the specification.
 */
public class ClearanceService {
    
    /**
     * Checks if a student is eligible for clearance.
     * Rule: eligible if ledger balance <= 0
     */
    public boolean isEligibleForClearance(Student student, Ledger ledger) {
        if (student == null || ledger == null) {
            return false;
        }
        return ledger.getBalance() <= 0;
    }

    /**
     * Gets the clearance status for a student.
     * Returns: "CLEARED", "WITH BALANCE", or "PENDING SERVICE"
     */
    public String getClearanceStatus(Student student, Ledger ledger) {
        if (student == null || ledger == null) {
            return "UNKNOWN";
        }
        
        double balance = ledger.getBalance();
        if (balance <= 0) {
            return "CLEARED";
        } else if (balance > 0) {
            return "WITH BALANCE";
        } else {
            return "PENDING SERVICE";
        }
    }
}

