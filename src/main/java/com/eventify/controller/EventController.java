package com.eventify.controller;

import com.eventify.dto.EventSummaryDTO;
import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management — soft-deleted events are automatically excluded from all queries")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Register event", description = "Creates a new active event in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Event name is empty or invalid")
    })
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(event));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "List event summaries",
            description = "Returns a lightweight Slice of EventSummaryDTO (no total count). " +
                    "Supports optional filters by city, category, date range, and venue capacity. " +
                    "Only active events are returned (soft-deleted events are excluded). " +
                    "Default sort: date descending."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Slice of event summaries"),
            @ApiResponse(responseCode = "204", description = "No events match the criteria")
    })
    public ResponseEntity<Slice<EventSummaryDTO>> findSummaries(
            @Parameter(description = "Filter by venue city (case-insensitive)")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by category name (case-insensitive)")
            @RequestParam(required = false) String category,
            @Parameter(description = "Filter start date (inclusive, ISO format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter end date (inclusive, ISO format)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by minimum venue capacity")
            @RequestParam(required = false) Integer minCapacity,
            @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        Slice<EventSummaryDTO> slice;

        if (city != null) {
            slice = eventService.findSummariesByCity(city, pageable);
        } else if (category != null) {
            slice = eventService.findSummariesByCategoryName(category, pageable);
        } else if (startDate != null && endDate != null) {
            slice = eventService.findSummariesByDateBetween(startDate, endDate, pageable);
        } else if (minCapacity != null) {
            slice = eventService.findSummariesByMinCapacity(minCapacity, pageable);
        } else {
            slice = eventService.findAllSummaries(pageable);
        }

        if (!slice.hasContent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(slice);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find event by id",
               description = "Returns a single active event with venue and categories eagerly loaded")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found or has been soft-deleted")
    })
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Updates an existing active event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Event name is empty or invalid"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate event",
               description = "Performs a soft delete by setting the event's active flag to false. " +
                       "The event remains in the database for audit purposes but is excluded from all queries.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Event deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
