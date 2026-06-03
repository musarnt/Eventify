package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event create(Event event) {
        event.setId(null);
        event.setActive(true);
        validateName(event.getName());
        validateDate(event.getDate());
        return eventRepository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    @Transactional
    public Event update(Long id, Event updated) {
        Event existing = findById(id);
        validateName(updated.getName());
        validateDate(updated.getDate());
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());
        existing.setVenue(updated.getVenue());
        existing.setCategories(updated.getCategories());
        return eventRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Event event = findById(id);
        event.deactivate();
        eventRepository.save(event);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        if (date.isBefore(LocalDate.of(2026, 1, 1))) {
            throw new IllegalArgumentException("Event date cannot be before 2026");
        }
    }
}
