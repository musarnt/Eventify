package com.eventify.repository;

import com.eventify.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Case-insensitive partial match on the event name
    List<Event> findByNameContainingIgnoreCase(String name);

    // Useful for upcoming-events queries
    List<Event> findByDateAfter(LocalDate date);

    // Useful for filtering events in a given range
    List<Event> findByDateBetween(LocalDate start, LocalDate end);
}