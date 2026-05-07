package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    // Constructor injection — avoids tight coupling
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(Event event) {
        // Business rule: name must not be null or blank
        if (event.getName() == null || event.getName().isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }
}