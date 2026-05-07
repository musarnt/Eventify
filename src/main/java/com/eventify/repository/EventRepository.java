package com.eventify.repository;

import com.eventify.model.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    // In-memory list simulating a database table
    private final List<Event> events = new ArrayList<>();

    public Event save(Event event) {
        events.add(event);
        return event;
    }

    public List<Event> findAll() {
        return events;
    }

    public Optional<Event> findById(Long id) {
        return events.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }
}