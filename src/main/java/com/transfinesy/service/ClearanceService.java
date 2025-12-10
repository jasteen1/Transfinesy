package com.transfinesy.service;

import com.transfinesy.model.Ledger;
import com.transfinesy.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for Clearance operations.
 * Note: This is different from the model class com.transfinesy.model.ClearanceService.
 */
@Service
public class ClearanceService {
    private LedgerService ledgerService;

    public ClearanceService() {
        this.ledgerService = new LedgerService();
    }

    @Autowired
    public ClearanceService(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * Checks if a student is eligible for clearance.
     * Rule: eligible if balance <= 0
     */
    public boolean isEligibleForClearance(Student student) {
        if (student == null) {
            return false;
        }
        Ledger ledger = ledgerService.getLedgerForStudent(student.getStudID());
        return ledger.getBalance() <= 0;
    }

    /**
     * Gets the clearance status for a student.
     */
    public String getClearanceStatus(Student student) {
        if (student == null) {
            return "UNKNOWN";
        }
        Ledger ledger = ledgerService.getLedgerForStudent(student.getStudID());
        double balance = ledger.getBalance();

        if (balance <= 0) {
            return "CLEARED";
        } else {
            return "WITH BALANCE";
        }
    }

    /**
     * Gets the clearance status with balance information.
     */
    public String getClearanceStatusWithBalance(Student student) {
        if (student == null) {
            return "UNKNOWN";
        }
        Ledger ledger = ledgerService.getLedgerForStudent(student.getStudID());
        double balance = ledger.getBalance();

        if (balance <= 0) {
            return "CLEARED";
        } else {
            return String.format("WITH BALANCE (₱%.2f)", balance);
        }
    }
}



