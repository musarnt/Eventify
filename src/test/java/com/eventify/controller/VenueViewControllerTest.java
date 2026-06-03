package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VenueViewController.class)
class VenueViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VenueService venueService;

    @Test
    void listShouldReturnVenuesView() throws Exception {
        Venue venue = Venue.builder()
                .id(1L)
                .name("Test Venue")
                .address("123 Main St")
                .city("Test City")
                .capacity(500)
                .build();
        Page<Venue> page = new PageImpl<>(List.of(venue));
        when(venueService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("view"));
    }

    @Test
    void newFormShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/admin/venues/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("venue"));
    }

    @Test
    void editFormShouldReturnFormWithVenue() throws Exception {
        Venue venue = Venue.builder()
                .id(1L)
                .name("Existing Venue")
                .address("456 Oak Ave")
                .city("Test City")
                .capacity(300)
                .build();
        when(venueService.findById(1L)).thenReturn(venue);

        mockMvc.perform(get("/admin/venues/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("venue"));
    }
}