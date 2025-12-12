package com.transfinesy.model;

/**
 * Model class for checking clearance eligibility.
 * 
 * This class provides methods to determine if a student is eligible for clearance
 * based on their ledger balance. Note that the actual business logic implementation
 * is in com.transfinesy.service.ClearanceService.
 * 
 * Clearance Rules:
 * - Student is eligible if ledger balance <= 0
 * - Student is not eligible if ledger balance > 0
 * 
 * Clearance Status:
 * - "CLEARED": Balance <= 0, student is eligible
 * - "WITH BALANCE": Balance > 0, student has outstanding balance
 * - "PENDING SERVICE": Special status (not currently used)
 * 
 * Usage:
 * - Called when viewing clearance page
 * - Used to determine which students can be cleared
 * - Displays clearance status in UI
 * 
 * @author transFINESy Development Team
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

