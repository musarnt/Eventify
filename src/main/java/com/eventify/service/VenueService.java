package com.eventify.service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    // Constructor injection — avoids tight coupling
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue create(Venue venue) {
        // Business rule: name must not be null or blank
        if (venue.getName() == null || venue.getName().isBlank()) {
            throw new IllegalArgumentException("Venue name cannot be empty");
        }
        return venueRepository.save(venue);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }
}