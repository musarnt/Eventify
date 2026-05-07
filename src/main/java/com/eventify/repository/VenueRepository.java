package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VenueRepository {

    // In-memory list simulating a database table
    private final List<Venue> venues = new ArrayList<>();

    public Venue save(Venue venue) {
        venues.add(venue);
        return venue;
    }

    public List<Venue> findAll() {
        return venues;
    }

    public Optional<Venue> findById(Long id) {
        return venues.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }
}