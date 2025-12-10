package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Abstract base class for all financial transactions in the ledger.
 */
public abstract class Transaction {
    protected String transactionID;
    protected String studID;
    protected double amount;
    protected LocalDate date;

    public Transaction() {
    }

    public Transaction(String transactionID, String studID, double amount, LocalDate date) {
        this.transactionID = transactionID;
        this.studID = studID;
        this.amount = amount;
        this.date = date;
    }

    /**
     * Returns the signed amount for ledger calculations.
     * For fines: negative amount
     * For payments and service credits: positive amount
     */
    public abstract double getSignedAmount();

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID='" + transactionID + '\'' +
                ", studID='" + studID + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}

