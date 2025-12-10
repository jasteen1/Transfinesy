package com.transfinesy.repo;

import com.transfinesy.model.CommunityService;
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
}

