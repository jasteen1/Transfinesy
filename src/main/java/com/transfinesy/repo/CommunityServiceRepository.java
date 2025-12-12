package com.transfinesy.repo;

import com.transfinesy.model.CommunityService;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for CommunityService operations.
 */
public interface CommunityServiceRepository {
    List<CommunityService> findAll();
    CommunityService findById(String serviceID);
    List<CommunityService> findByStudent(String studID);
    void save(CommunityService cs);
    void update(CommunityService cs);
    void delete(String serviceID);
    
    /**
     * Gets sum of all service hours (optimized SQL aggregation).
     * @return Total hours rendered
     */
    int getTotalHours();
    
    /**
     * Gets sum of all service credits (optimized SQL aggregation).
     * @return Total credit amount
     */
    double getTotalCredits();
    
    /**
     * Gets sum of service hours for a date range (optimized SQL aggregation).
     * @param startDate Start date
     * @param endDate End date
     * @return Total hours rendered in the date range
     */
    int getHoursByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets sum of service credits for a date range (optimized SQL aggregation).
     * @param startDate Start date
     * @param endDate End date
     * @return Total credit amount in the date range
     */
    double getCreditsByDateRange(LocalDate startDate, LocalDate endDate);
}

