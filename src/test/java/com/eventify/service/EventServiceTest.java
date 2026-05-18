package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Runs tests without loading the Spring context
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event validEvent;

    @BeforeEach
    void setUp() {
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

        assertNotNull(result);
        assertEquals("Jazz Concert", result.getName());
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
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_withNullName_throwsException() {
        validEvent.setName(null);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(validEvent));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void findAll_returnsEventPage() {
        // Build a Pageable and stub the repository to return a Page wrapping our event
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> mockPage = new PageImpl<>(List.of(validEvent), pageable, 1);
        when(eventRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Event> result = eventService.findAll(pageable);

        // Validate page contents and metadata
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
        assertEquals("Jazz Concert", result.getContent().get(0).getName());
        verify(eventRepository, times(1)).findAll(pageable);
    }
}