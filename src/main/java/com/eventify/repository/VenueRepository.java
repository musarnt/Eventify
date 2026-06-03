package com.eventify.repository;

import com.eventify.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByNameContainingIgnoreCase(String name);

    List<Venue> findByCityIgnoreCase(String city);

    List<Venue> findByCapacityGreaterThanEqual(Integer minCapacity);
}
