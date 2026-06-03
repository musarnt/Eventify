package com.eventify.service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Runs tests without loading the Spring context
@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    private Venue validVenue;

    @BeforeEach
    void setUp() {
        validVenue = Venue.builder()
                .id(1L)
                .name("Metropolitan Theater")
                .address("Street 41 #57-30")
                .city("Medellin")
                .capacity(1200)
                .build();
    }

    @Test
    void create_withValidData_returnsSavedVenue() {
        when(venueRepository.save(validVenue)).thenReturn(validVenue);

        Venue result = venueService.create(validVenue);

        assertNotNull(result);
        assertEquals("Metropolitan Theater", result.getName());
        verify(venueRepository, times(1)).save(validVenue);
    }

    @Test
    void create_withEmptyName_throwsException() {
        validVenue.setName("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> venueService.create(validVenue)
        );

        assertEquals("Venue name cannot be empty", ex.getMessage());
        verify(venueRepository, never()).save(any());
    }

    @Test
    void create_withNullName_throwsException() {
        validVenue.setName(null);

        assertThrows(IllegalArgumentException.class, () -> venueService.create(validVenue));
        verify(venueRepository, never()).save(any());
    }

    @Test
    void findAll_returnsVenuePage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> mockPage = new PageImpl<>(List.of(validVenue), pageable, 1);
        when(venueRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Venue> result = venueService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
        assertEquals("Metropolitan Theater", result.getContent().get(0).getName());
        verify(venueRepository, times(1)).findAll(pageable);
    }
}