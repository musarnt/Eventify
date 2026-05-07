package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Venue management")
public class VenueController {

    private final VenueService venueService;

    // Constructor injection
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
    @Operation(summary = "List venues", description = "Returns all registered venues")
    @ApiResponse(responseCode = "200", description = "Venue list returned successfully")
    public ResponseEntity<List<Venue>> findAll() {
        return ResponseEntity.ok(venueService.findAll());
    }
}