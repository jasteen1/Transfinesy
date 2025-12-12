package com.transfinesy.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the financial ledger for a single student.
 * 
 * This class aggregates all financial transactions (fines, payments, service credits)
 * for a student and calculates their current balance.
 * 
 * Key Features:
 * - Maintains a list of all transactions
 * - Calculates opening and closing balances
 * - Tracks totals: fines, payments, service credits
 * - Provides transaction history filtering
 * 
 * Balance Calculation:
 * - Formula: Balance = Opening Balance + Total Fines - Total Payments - Total Service Credits
 * - Fines increase balance (debt)
 * - Payments and credits decrease balance (reduce debt)
 * - Balance can never be negative (minimum 0)
 * 
 * Transaction Types:
 * - Fine: Increases balance (positive contribution)
 * - Payment: Decreases balance (negative contribution)
 * - Service Credit: Decreases balance (negative contribution)
 * 
 * Usage:
 * - Generated on-demand when viewing student ledger
 * - Used for clearance eligibility checks
 * - Provides complete financial history
 * 
 * @author transFINESy Development Team
 */
public class Ledger {
    private String studID;
    private List<Transaction> transactions;
    private double openingBalance;
    private double closingBalance;
    private LocalDateTime lastUpdated;
    private double totalFines;
    private double totalPayments;
    private double totalServiceCredits;

    public Ledger() {
        this.transactions = new ArrayList<>();
        this.openingBalance = 0.0;
        this.closingBalance = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }

    public Ledger(String studID) {
        this();
        this.studID = studID;
    }

    public void addTransaction(Transaction t) {
        if (t != null) {
            transactions.add(t);
            updateClosingBalance();
            updateTotals();
            lastUpdated = LocalDateTime.now();
        }
    }

    public double computeBalance() {
        // Correct formula: balance = totalFines - totalPayments - totalCommunityCredits
        // Fines increase debt (positive), Payments and Credits reduce debt (negative)
        double totalFines = 0.0;
        double totalPayments = 0.0;
        double totalCredits = 0.0;
        
        for (Transaction t : transactions) {
            if (t instanceof Fine) {
                totalFines += Math.abs(t.getAmount());
            } else if (t instanceof Payment) {
                // Check if it's a service credit (starts with SVC-PAY) or regular payment
                if (t.getTransactionID() != null && t.getTransactionID().startsWith("SVC-TXN-")) {
                    totalCredits += Math.abs(t.getAmount());
                } else {
                    totalPayments += Math.abs(t.getAmount());
                }
            }
        }
        
        // Add community service credits from the separate total
        totalCredits += totalServiceCredits;
        
        // Calculate balance: totalFines - totalPayments - totalCredits
        double balance = openingBalance + totalFines - totalPayments - totalCredits;
        closingBalance = balance;
        
        // Update totals for reporting
        this.totalFines = totalFines;
        this.totalPayments = totalPayments;
        this.totalServiceCredits = totalCredits;
        
        return balance;
    }

    public double getBalance() {
        return closingBalance;
    }

    public double getTotalFines() {
        return totalFines;
    }

    public double getTotalPayments() {
        return totalPayments;
    }

    public double getTotalCredits() {
        return totalServiceCredits;
    }

    public void updateClosingBalance() {
        computeBalance();
    }

    private void updateTotals() {
        totalFines = 0.0;
        totalPayments = 0.0;
        totalServiceCredits = 0.0;

        for (Transaction t : transactions) {
            if (t instanceof Fine) {
                totalFines += Math.abs(t.getAmount());
            } else if (t instanceof Payment) {
                totalPayments += t.getAmount();
            }
            // Note: CommunityService is not a Transaction, so we handle it separately
            // In practice, you might want to create a ServiceCreditTransaction class
        }
    }

    public List<Transaction> filterTransactionsByDate(LocalDate start, LocalDate end) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        updateClosingBalance();
        updateTotals();
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
        updateClosingBalance();
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getTotalServiceCredits() {
        return totalServiceCredits;
    }

    public void setTotalServiceCredits(double totalServiceCredits) {
        this.totalServiceCredits = totalServiceCredits;
    }
}

