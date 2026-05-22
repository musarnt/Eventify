package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Test
    void listShouldReturnEventsView() throws Exception {
        // Arrange: mock service returns a page with one event
        Event event = Event.builder()
                .id(1L)
                .name("Test Event")
                .date(LocalDate.of(2026, 6, 15))
                .description("A test event")
                .build();
        Page<Event> page = new PageImpl<>(List.of(event));
        when(eventService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("view"));
    }

    @Test
    void newFormShouldReturnFormView() throws Exception {
        mockMvc.perform(get("/admin/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void editFormShouldReturnFormWithEvent() throws Exception {
        Event event = Event.builder()
                .id(1L)
                .name("Existing Event")
                .date(LocalDate.of(2026, 7, 20))
                .build();
        when(eventService.findById(1L)).thenReturn(event);

        mockMvc.perform(get("/admin/events/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("event"));
    }
}