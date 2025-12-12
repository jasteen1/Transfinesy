package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Abstract base class for all financial transactions in the ledger.
 * 
 * This class provides the common structure for all financial transactions
 * in the system. It is extended by Fine and Payment classes.
 * 
 * Key Features:
 * - Unique transaction ID for tracking
 * - Linked to a student (studID)
 * - Amount and date information
 * - Abstract method for signed amount calculation
 * 
 * Signed Amount:
 * - Different transaction types have different signs:
 *   - Fines: Positive (increase debt)
 *   - Payments: Negative (decrease debt)
 *   - Service Credits: Negative (decrease debt)
 * 
 * Subclasses:
 * - Fine: Represents fines issued to students
 * - Payment: Represents payments made by students
 * 
 * Usage:
 * - All transactions are stored in student ledgers
 * - Used for balance calculations
 * - Provides transaction history
 * 
 * @author transFINESy Development Team
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

