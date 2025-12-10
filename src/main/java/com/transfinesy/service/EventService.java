package com.transfinesy.service;

import com.transfinesy.model.Event;
import com.transfinesy.repo.EventRepository;
import com.transfinesy.repo.EventRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Event operations.
 */
@Service
public class EventService {
    private EventRepository repository;

    public EventService() {
        this.repository = new EventRepositoryImpl();
    }

    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    public Event getEventById(String eventID) {
        return repository.findById(eventID);
    }

    public void addEvent(Event event) {
        validateEvent(event);
        repository.save(event);
    }

    public void updateEvent(Event event) {
        if (event == null || event.getEventID() == null) {
            throw new IllegalArgumentException("Event ID is required");
        }
        validateEvent(event);
        repository.update(event);
    }
    
    /**
     * Validates event data according to business rules.
     */
    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        if (event.getEventID() == null || event.getEventID().trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID is required");
        }
        
        // Event date validation: must be between year 2000 and current year
        if (event.getEventDate() != null) {
            int eventYear = event.getEventDate().getYear();
            int currentYear = java.time.Year.now().getValue();
            
            if (eventYear < 2000) {
                throw new IllegalArgumentException("Event date cannot be before year 2000");
            }
            if (eventYear > currentYear) {
                throw new IllegalArgumentException("Event date cannot be after current year (" + currentYear + ")");
            }
        }
        
        // Validate semester: must be 1 or 2
        if (event.getSemester() != null && event.getSemester() != 1 && event.getSemester() != 2) {
            throw new IllegalArgumentException("Semester must be 1 or 2");
        }
    }

    public void deleteEvent(String eventID) {
        repository.delete(eventID);
    }
}

