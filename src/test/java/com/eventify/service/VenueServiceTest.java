package com.eventify.service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Runs tests without loading the Spring context
@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    // Mock repository — simulates DB behavior without real data
    @Mock
    private VenueRepository venueRepository;

    // Injects the mock into the service automatically
    @InjectMocks
    private VenueService venueService;

    private Venue validVenue;

    @BeforeEach
    void setUp() {
        // Base venue reused across tests
        validVenue = Venue.builder()
                .id(1L)
                .name("Metropolitan Theater")
                .address("Street 41 #57-30, Medellin")
                .capacity(1200)
                .build();
    }

    @Test
    void create_withValidData_returnsSavedVenue() {
        when(venueRepository.save(validVenue)).thenReturn(validVenue);

        Venue result = venueService.create(validVenue);

        // Verifies the returned venue is not null and has the correct name
        assertNotNull(result);
        assertEquals("Metropolitan Theater", result.getName());

        // Verifies save was called exactly once
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

        // Verifies corrupted data never reaches the repository
        verify(venueRepository, never()).save(any());
    }

    @Test
    void create_withNullName_throwsException() {
        validVenue.setName(null);

        assertThrows(IllegalArgumentException.class, () -> venueService.create(validVenue));

        // Repository must not be called when validation fails
        verify(venueRepository, never()).save(any());
    }

    @Test
    void findAll_returnsVenueList() {
        when(venueRepository.findAll()).thenReturn(List.of(validVenue));

        List<Venue> result = venueService.findAll();

        // Verifies the list has exactly one element
        assertEquals(1, result.size());
        verify(venueRepository, times(1)).findAll();
    }
}