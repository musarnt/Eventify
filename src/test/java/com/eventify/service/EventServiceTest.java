package com.eventify.service;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event validEvent;
    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = Venue.builder()
                .id(1L)
                .name("Metropolitan Theater")
                .address("Street 41 #57-30")
                .city("Medellin")
                .capacity(1200)
                .build();

        validEvent = Event.builder()
                .id(1L)
                .name("Jazz Concert")
                .date(LocalDate.of(2026, 10, 10))
                .description("A live jazz night")
                .venue(venue)
                .build();
    }

    @Test
    void create_withValidData_returnsSavedEvent() {
        when(eventRepository.save(validEvent)).thenReturn(validEvent);

        Event result = eventService.create(validEvent);

        assertNotNull(result);
        assertEquals("Jazz Concert", result.getName());
        assertTrue(result.isActive());
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
    void findAll_returnsEventSlice() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Event> mockSlice = new SliceImpl<>(List.of(validEvent), pageable, false);
        when(eventRepository.findAllBy(pageable)).thenReturn(mockSlice);

        Slice<Event> result = eventService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Jazz Concert", result.getContent().get(0).getName());
        verify(eventRepository, times(1)).findAllBy(pageable);
    }

    @Test
    void delete_softDeletesEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        when(eventRepository.save(validEvent)).thenReturn(validEvent);

        eventService.delete(1L);

        assertFalse(validEvent.isActive());
        verify(eventRepository, times(1)).save(validEvent);
        verify(eventRepository, never()).deleteById(any());
    }
}
