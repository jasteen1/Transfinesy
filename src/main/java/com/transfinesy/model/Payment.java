package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Represents a payment transaction (extends Transaction).
 * Payments have positive signed amounts.
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

