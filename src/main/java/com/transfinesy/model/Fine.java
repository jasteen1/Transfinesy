package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Represents a fine transaction (extends Transaction).
 * Fines have negative signed amounts.
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

