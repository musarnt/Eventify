package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Venue management")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(summary = "Register venue", description = "Creates a new venue in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venue created successfully"),
            @ApiResponse(responseCode = "400", description = "Venue name is empty or invalid")
    })
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.create(venue));
    }

    @GetMapping
    @Operation(
            summary = "List venues",
            description = "Returns a paginated list of venues. Supports query params: " +
                    "page (default 0), size (default 20), sort (e.g. name,asc | capacity,desc)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of venues returned successfully"),
            @ApiResponse(responseCode = "204", description = "No venues available for the requested page")
    })
    public ResponseEntity<Page<Venue>> findAll(@ParameterObject Pageable pageable) {
        Page<Venue> page = venueService.findAll(pageable);
        if (page.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find venue by id", description = "Returns a single venue by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue found"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    public ResponseEntity<Venue> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update venue", description = "Updates an existing venue by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue updated successfully"),
            @ApiResponse(responseCode = "400", description = "Venue name is empty or invalid"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.update(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete venue", description = "Removes a venue by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venue deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}