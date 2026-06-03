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

    // --- Full entity queries (admin panel) ---
    // We JOIN FETCH only the ToOne side (venue) in the paginated query so SQL pagination
    // stays in the DB. Categories load via @BatchSize on the entity: a single follow-up
    // "WHERE event_id IN (?, ?, ...)" query — no N+1, no in-memory pagination.

    @EntityGraph(attributePaths = {"venue"})
    Slice<Event> findAllBy(Pageable pageable);

    // Partial, case-insensitive search by city and category for the admin panel.
    @Query(value = "SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.venue v LEFT JOIN e.categories c " +
                   "WHERE (:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
                   "AND (:category IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%')))",
           countQuery = "SELECT COUNT(DISTINCT e) FROM Event e LEFT JOIN e.venue v LEFT JOIN e.categories c " +
                        "WHERE (:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
                        "AND (:category IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%')))")
    Slice<Event> searchAll(@Param("city") String city,
                           @Param("category") String category,
                           Pageable pageable);

    // Single-entity lookups fetch everything (venue + categories) since there is no pagination.
    @Override
    @EntityGraph(attributePaths = {"venue", "categories"})
    Optional<Event> findById(Long id);

    // --- DTO projections (API listings, lightweight) ---
    // Each query uses LIKE for partial matching and LOWER for case-insensitivity.

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e")
    Slice<EventSummaryDTO> findAllSummaries(Pageable pageable);

    @Query("SELECT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e WHERE LOWER(e.venue.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    Slice<EventSummaryDTO> findSummariesByCity(@Param("city") String city, Pageable pageable);

    @Query("SELECT DISTINCT new com.eventify.dto.EventSummaryDTO(e.id, e.name, e.date, e.venue.name, e.venue.city) " +
           "FROM Event e JOIN e.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%'))")
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
