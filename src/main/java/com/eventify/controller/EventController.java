package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management")
public class EventController {

    private final EventService eventService;

    // Constructor injection
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(summary = "Register event", description = "Creates a new event in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Event name is empty or invalid")
    })
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(event));
    }

    @GetMapping
    @Operation(summary = "List events", description = "Returns all registered events")
    @ApiResponse(responseCode = "200", description = "Event list returned successfully")
    public ResponseEntity<List<Event>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }
}