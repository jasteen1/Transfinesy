package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Represents a payment transaction in the system.
 * 
 * This class extends Transaction and models a payment made by a student.
 * Payments reduce the student's outstanding balance.
 * 
 * Key Features:
 * - Linked to a student (studID)
 * - Has a unique payment ID and transaction ID
 * - Includes OR (Official Receipt) number for tracking
 * - Amount is stored as positive value
 * - getSignedAmount() returns negative (payments reduce debt)
 * 
 * Ledger Calculation:
 * - Payments decrease the student's outstanding balance
 * - Formula: Balance = Total Fines - Total Payments - Total Credits
 * 
 * Service Credit Payments:
 * - Payments with transaction ID starting with "SVC-TXN-" are service credits
 * - These are converted from community service hours
 * 
 * @author transFINESy Development Team
 */
public class Payment extends Transaction {
    private String paymentID;
    private String orNumber;

    public Payment() {
        super();
    }

    public Payment(String paymentID, String transactionID, String studID, double amount, String orNumber, LocalDate date) {
        super(transactionID, studID, amount, date);
        this.paymentID = paymentID;
        this.orNumber = orNumber;
    }

    @Override
    public double getSignedAmount() {
        // Payments reduce balance (credits), so return negative
        return -Math.abs(amount);
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(String orNumber) {
        this.orNumber = orNumber;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID='" + paymentID + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", studID='" + studID + '\'' +
                ", amount=" + amount +
                ", orNumber='" + orNumber + '\'' +
                ", date=" + date +
                '}';
    }
}

