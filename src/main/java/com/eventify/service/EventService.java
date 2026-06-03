package com.eventify.service;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    public Slice<Event> findAll(Pageable pageable) {
        return eventRepository.findAllBy(pageable);
    }

    // Partial, case-insensitive search by city and/or category. Either filter may be null/blank.
    public Slice<Event> search(String city, String category, Pageable pageable) {
        String cityFilter = (city == null || city.isBlank()) ? null : city.trim();
        String categoryFilter = (category == null || category.isBlank()) ? null : category.trim();
        if (cityFilter == null && categoryFilter == null) {
            return findAll(pageable);
        }
        return eventRepository.searchAll(cityFilter, categoryFilter, pageable);
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

    // --- DTO projections for API ---

    public Slice<EventSummaryDTO> findAllSummaries(Pageable pageable) {
        return eventRepository.findAllSummaries(pageable);
    }

    public Slice<EventSummaryDTO> findSummariesByCity(String city, Pageable pageable) {
        return eventRepository.findSummariesByCity(city, pageable);
    }

    public Slice<EventSummaryDTO> findSummariesByCategoryName(String category, Pageable pageable) {
        return eventRepository.findSummariesByCategoryName(category, pageable);
    }

    public Slice<EventSummaryDTO> findSummariesByDateBetween(LocalDate start, LocalDate end, Pageable pageable) {
        return eventRepository.findSummariesByDateBetween(start, end, pageable);
    }

    public Slice<EventSummaryDTO> findSummariesByMinCapacity(Integer minCapacity, Pageable pageable) {
        return eventRepository.findSummariesByMinCapacity(minCapacity, pageable);
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
