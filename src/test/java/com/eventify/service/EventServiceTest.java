package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Runs tests without loading the Spring context
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    // Mock repository — simulates DB behavior without real data
    @Mock
    private EventRepository eventRepository;

    // Injects the mock into the service automatically
    @InjectMocks
    private EventService eventService;

    private Event validEvent;

    @BeforeEach
    void setUp() {
        // Base event reused across tests
        validEvent = Event.builder()
                .id(1L)
                .name("Jazz Concert")
                .date(LocalDate.of(2025, 10, 10))
                .description("A live jazz night")
                .build();
    }

    @Test
    void create_withValidData_returnsSavedEvent() {
        when(eventRepository.save(validEvent)).thenReturn(validEvent);

        Event result = eventService.create(validEvent);

        // Verifies the returned event is not null and has the correct name
        assertNotNull(result);
        assertEquals("Jazz Concert", result.getName());

        // Verifies save was called exactly once
        verify(eventRepository, times(1)).save(validEvent);
    }

    @Test
    void create_withEmptyName_throwsException() {
        validEvent.setName("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(validEvent)
        );

        assertEquals("Event name cannot be empty", ex.getMessage());

        // Verifies corrupted data never reaches the repository
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_withNullName_throwsException() {
        validEvent.setName(null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(validEvent));

        // Repository must not be called when validation fails
        verify(eventRepository, never()).save(any());
    }

    @Test
    void findAll_returnsEventList() {
        when(eventRepository.findAll()).thenReturn(List.of(validEvent));

        List<Event> result = eventService.findAll();

        // Verifies the list has exactly one element
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findAll();
    }
}