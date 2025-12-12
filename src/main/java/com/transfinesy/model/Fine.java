package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Represents a fine transaction in the system.
 * 
 * This class extends Transaction and models a fine issued to a student.
 * Fines are typically generated automatically when:
 * - A student is absent from an event
 * - A student arrives late to an event
 * 
 * Key Features:
 * - Linked to a specific event (eventID)
 * - Has a unique fine ID and transaction ID
 * - Amount is stored as positive value
 * - getSignedAmount() returns positive (fines increase debt)
 * 
 * Ledger Calculation:
 * - Fines increase the student's outstanding balance
 * - Formula: Balance = Total Fines - Total Payments - Total Credits
 * 
 * @author transFINESy Development Team
 */
public class Fine extends Transaction {
    private String fineID;
    private String eventID;
    private double fineAmount;

    public Fine() {
        super();
    }

    public Fine(String fineID, String transactionID, String studID, String eventID, double fineAmount, LocalDate date) {
        super(transactionID, studID, fineAmount, date);
        this.fineID = fineID;
        this.eventID = eventID;
        this.fineAmount = fineAmount;
    }

    @Override
    public double getSignedAmount() {
        // Fines increase balance (debt), so return positive
        return Math.abs(fineAmount);
    }

    public String getFineID() {
        return fineID;
    }

    public void setFineID(String fineID) {
        this.fineID = fineID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
        // Also update the parent amount
        this.amount = fineAmount;
    }

    @Override
    public String toString() {
        return "Fine{" +
                "fineID='" + fineID + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", studID='" + studID + '\'' +
                ", eventID='" + eventID + '\'' +
                ", fineAmount=" + fineAmount +
                ", date=" + date +
                '}';
    }
}

