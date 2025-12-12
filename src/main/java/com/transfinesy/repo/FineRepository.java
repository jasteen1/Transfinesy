package com.transfinesy.repo;

import com.transfinesy.model.Fine;
import java.util.List;

/**
 * Repository interface for Fine operations.
 */
public interface FineRepository {
    List<Fine> findAll();
    Fine findById(String fineID);
    List<Fine> findByStudent(String studID);
    List<Fine> findByEvent(String eventID);
    void save(Fine f);
    void update(Fine f);
    void delete(String fineID);
    
    /**
     * Gets sum of all fines (optimized SQL aggregation).
     * @return Sum of all fine amounts
     */
    double getTotalSum();
    
    /**
     * Gets sum of fines for a specific event (optimized SQL aggregation).
     * @param eventID Event ID
     * @return Sum of fine amounts for the event
     */
    double getSumByEvent(String eventID);
}

