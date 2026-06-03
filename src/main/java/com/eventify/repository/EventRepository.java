package com.eventify.repository;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // --- Full entity queries (admin panel, eager-loaded) ---

    @EntityGraph(attributePaths = {"venue", "categories"})
    Slice<Event> findAllBy(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"venue", "categories"})
    Optional<Event> findById(Long id);

    // --- DTO projections (API listings, lightweight) ---

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e")
    Slice<EventSummaryDTO> findAllSummaries(Pageable pageable);

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e WHERE LOWER(e.venue.city) = LOWER(:city)")
    Slice<EventSummaryDTO> findSummariesByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e JOIN e.categories c WHERE LOWER(c.name) = LOWER(:category)")
    Slice<EventSummaryDTO> findSummariesByCategoryName(@Param("category") String category, Pageable pageable);

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e WHERE e.date BETWEEN :start AND :end")
    Slice<EventSummaryDTO> findSummariesByDateBetween(@Param("start") LocalDate start,
                                                      @Param("end") LocalDate end,
                                                      Pageable pageable);

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e WHERE e.venue.capacity >= :minCapacity")
    Slice<EventSummaryDTO> findSummariesByMinCapacity(@Param("minCapacity") Integer minCapacity, Pageable pageable);

    // --- Derived queries (kept from HU-02) ---

    List<Event> findByNameContainingIgnoreCase(String name);

    List<Event> findByDateAfter(LocalDate date);

    List<Event> findByDateBetween(LocalDate start, LocalDate end);
}
