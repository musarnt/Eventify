package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventViewController.class)
class EventViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private VenueService venueService;

    @MockitoBean
    private CategoryService categoryService;

    private void stubFormData() {
        when(venueService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(categoryService.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Test
    void listShouldReturnEventsView() throws Exception {
        Venue venue = Venue.builder().id(1L).name("Venue").address("addr").city("City").capacity(100).build();
        Event event = Event.builder()
                .id(1L)
                .name("Test Event")
                .date(LocalDate.of(2026, 6, 15))
                .description("A test event")
                .venue(venue)
                .build();
        Slice<Event> slice = new SliceImpl<>(List.of(event));
        when(eventService.search(any(), any(), any(Pageable.class))).thenReturn(slice);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("view"));
    }

    @Test
    void newFormShouldReturnFormView() throws Exception {
        stubFormData();

        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("allCategories"));
    }

    @Test
    void editFormShouldReturnFormWithEvent() throws Exception {
        stubFormData();
        Venue venue = Venue.builder().id(1L).name("Venue").address("addr").city("City").capacity(100).build();
        Event event = Event.builder()
                .id(1L)
                .name("Existing Event")
                .date(LocalDate.of(2026, 7, 20))
                .venue(venue)
                .build();
        when(eventService.findById(1L)).thenReturn(event);

        mockMvc.perform(get("/admin/events/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("allCategories"));
    }
}
