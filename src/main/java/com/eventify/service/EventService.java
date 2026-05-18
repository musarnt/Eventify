package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(Event event) {
        // Defensive: ID must be assigned by the database, never by the client
        event.setId(null);
        validateName(event.getName());
        return eventRepository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    public Event update(Long id, Event updated) {
        // Reuses findById so the 404 logic stays in one place
        Event existing = findById(id);
        validateName(updated.getName());
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());
        return eventRepository.save(existing);
    }

    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
    }
}