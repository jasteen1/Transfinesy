package com.transfinesy.service;

import com.transfinesy.model.*;
import com.transfinesy.repo.FineRepository;
import com.transfinesy.repo.FineRepositoryImpl;
import com.transfinesy.repo.PaymentRepository;
import com.transfinesy.repo.PaymentRepositoryImpl;
import com.transfinesy.repo.CommunityServiceRepository;
import com.transfinesy.repo.CommunityServiceRepositoryImpl;
import com.transfinesy.util.Stack;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for Ledger operations.
 * Builds and manages student ledgers from all transaction sources.
 */
@Service
public class LedgerService {
    private FineRepository fineRepository;
    private PaymentRepository paymentRepository;
    private CommunityServiceRepository serviceRepository;
    // Stack for managing recent transaction history (LIFO - most recent first)
    private Stack<Transaction> recentTransactions;

    public LedgerService() {
        this.fineRepository = new FineRepositoryImpl();
        this.paymentRepository = new PaymentRepositoryImpl();
        this.serviceRepository = new CommunityServiceRepositoryImpl();
        this.recentTransactions = new Stack<>();
    }

    /**
     * Builds a complete ledger for a student by aggregating all transactions.
     */
    public Ledger getLedgerForStudent(String studID) {
        Ledger ledger = new Ledger(studID);

        // Load all fines
        List<Fine> fines = fineRepository.findByStudent(studID);
        for (Fine fine : fines) {
            ledger.addTransaction(fine);
            // Add to recent transactions stack (most recent first)
            recentTransactions.push(fine);
        }

        // Load all payments
        List<Payment> payments = paymentRepository.findByStudent(studID);
        for (Payment payment : payments) {
            ledger.addTransaction(payment);
            // Add to recent transactions stack (most recent first)
            recentTransactions.push(payment);
        }

        // Load community service credits
        List<CommunityService> services = serviceRepository.findByStudent(studID);
        double totalServiceCredits = services.stream()
                .mapToDouble(CommunityService::getCreditAmount)
                .sum();
        ledger.setTotalServiceCredits(totalServiceCredits);

        // For each service credit, we need to add it as a transaction
        // Since CommunityService is not a Transaction, we'll create Payment-like entries
        // In a more sophisticated system, you'd have a ServiceCreditTransaction class
        for (CommunityService service : services) {
            // Create a temporary payment-like transaction for service credits
            String txnID = "SVC-TXN-" + service.getServiceID();
            String description = service.getDescription() != null && !service.getDescription().trim().isEmpty()
                ? "Community Service: " + service.getDescription()
                : "Community Service: " + service.getHoursRendered() + " hours";
            Payment serviceCredit = new Payment(
                "SVC-PAY-" + service.getServiceID(),
                txnID,
                studID,
                service.getCreditAmount(),
                description,
                service.getDate()
            );
            ledger.addTransaction(serviceCredit);
        }

        ledger.computeBalance();
        return ledger;
    }

    /**
     * Gets the current balance for a student.
     */
    public double getBalanceForStudent(String studID) {
        Ledger ledger = getLedgerForStudent(studID);
        return ledger.getBalance();
    }

    /**
     * Adds a transaction to a student's ledger.
     */
    public void addTransactionToLedger(String studID, Transaction transaction) {
        // The ledger is rebuilt from the database each time,
        // so we don't need to maintain an in-memory ledger
        // This method is kept for API consistency
    }

    /**
     * Adds a service credit to a student's ledger.
     */
    public void addServiceCreditToLedger(String studID, double creditAmount, LocalDate date) {
        // Service credits are stored in CommunityService table
        // Ledger is rebuilt from database, so this is mainly for API consistency
    }

    /**
     * Gets transaction history for a student within a date range.
     */
    public List<Transaction> getTransactionHistory(String studID, LocalDate start, LocalDate end) {
        Ledger ledger = getLedgerForStudent(studID);
        return ledger.filterTransactionsByDate(start, end);
    }

    /**
     * Gets all transactions for a student.
     */
    public List<Transaction> getAllTransactions(String studID) {
        Ledger ledger = getLedgerForStudent(studID);
        return ledger.getTransactionHistory();
    }
    
    /**
     * Gets the most recent transactions using the Stack (LIFO).
     * @param limit Maximum number of recent transactions to return
     * @return List of recent transactions (most recent first)
     */
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> recent = new ArrayList<>();
        Stack<Transaction> tempStack = new Stack<>();
        
        // Pop transactions from stack and add to list
        int count = 0;
        while (!recentTransactions.isEmpty() && count < limit) {
            Transaction txn = recentTransactions.pop();
            recent.add(txn);
            tempStack.push(txn); // Keep for restoring
            count++;
        }
        
        // Restore stack
        while (!tempStack.isEmpty()) {
            recentTransactions.push(tempStack.pop());
        }
        
        return recent;
    }
    
    /**
     * Gets the size of the recent transactions stack.
     */
    public int getRecentTransactionsSize() {
        return recentTransactions.size();
    }
}



