package com.eventify.service;

import com.eventify.exception.ResourceNotFoundException;
import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue create(Venue venue) {
        // Defensive: ID must be assigned by the database, never by the client
        venue.setId(null);
        validateName(venue.getName());
        return venueRepository.save(venue);
    }

    public Page<Venue> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
    }

    public Venue update(Long id, Venue updated) {
        // Reuses findById so the 404 logic stays in one place
        Venue existing = findById(id);
        validateName(updated.getName());
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setCapacity(updated.getCapacity());
        return venueRepository.save(existing);
    }

    public void delete(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue", id);
        }
        venueRepository.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Venue name cannot be empty");
        }
    }
}