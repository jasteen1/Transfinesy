package com.transfinesy.service;

import com.transfinesy.model.CommunityService;
import com.transfinesy.repo.CommunityServiceRepository;
import com.transfinesy.repo.CommunityServiceRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for CommunityService operations.
 */
@Service
public class CommunityServiceService {
    private CommunityServiceRepository repository;
    private LedgerService ledgerService;

    // Conversion rate: 1 hour = 50 pesos
    private static final double CREDIT_PER_HOUR = 50.0;

    public CommunityServiceService() {
        this.repository = new CommunityServiceRepositoryImpl();
        this.ledgerService = new LedgerService();
    }

    public CommunityServiceService(LedgerService ledgerService) {
        this.repository = new CommunityServiceRepositoryImpl();
        this.ledgerService = ledgerService;
    }

    /**
     * Calculates credit amount based on hours rendered.
     */
    public double calculateCreditAmount(int hoursRendered) {
        return hoursRendered * CREDIT_PER_HOUR;
    }

    /**
     * Records community service and updates ledger.
     */
    public void recordCommunityService(String studID, int hoursRendered, LocalDate date) {
        recordCommunityService(studID, hoursRendered, date, null);
    }

    /**
     * Records community service with description and updates ledger.
     */
    public void recordCommunityService(String studID, int hoursRendered, LocalDate date, String description) {
        if (studID == null || studID.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (hoursRendered <= 0) {
            throw new IllegalArgumentException("Hours rendered must be positive");
        }

        double creditAmount = calculateCreditAmount(hoursRendered);
        String serviceID = "SVC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        CommunityService service = new CommunityService(serviceID, studID, hoursRendered, creditAmount, date, description);
        repository.save(service);
        
        // Ledger entry is automatically created when ledger is rebuilt
        // The LedgerService.getLedgerForStudent() method includes community service credits
        // No need to manually add - ledger is rebuilt from database each time
    }

    public List<CommunityService> getServicesByStudent(String studID) {
        return repository.findByStudent(studID);
    }

    public CommunityService getServiceById(String serviceID) {
        return repository.findById(serviceID);
    }

    public List<CommunityService> getAllServices() {
        return repository.findAll();
    }

    public void updateService(CommunityService service) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        repository.update(service);
    }

    public void deleteService(String serviceID) {
        repository.delete(serviceID);
    }
}

