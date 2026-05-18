package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    // Case-insensitive partial match on the venue name
    List<Venue> findByNameContainingIgnoreCase(String name);

    // Filter venues that can hold at least a given audience size
    List<Venue> findByCapacityGreaterThanEqual(Integer minCapacity);
}