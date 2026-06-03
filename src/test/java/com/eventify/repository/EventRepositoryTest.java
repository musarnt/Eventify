package com.eventify.repository;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Venue venue;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        venueRepository.deleteAll();
        venue = venueRepository.save(Venue.builder()
                .name("Test Venue")
                .address("Test Address")
                .city("Test City")
                .capacity(500)
                .build());
    }

    @Test
    void shouldPersistEventAndGenerateId() {
        Event event = Event.builder()
                .name("Symphonic Concert")
                .date(LocalDate.of(2026, 8, 15))
                .description("Classical music night")
                .venue(venue)
                .build();

        Event saved = eventRepository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Symphonic Concert");
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        eventRepository.save(Event.builder()
                .name("Symphonic Concert").date(LocalDate.now()).venue(venue).build());
        eventRepository.save(Event.builder()
                .name("Rock Festival").date(LocalDate.now()).venue(venue).build());

        List<Event> results = eventRepository.findByNameContainingIgnoreCase("CONCERT");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Symphonic Concert");
    }

    @Test
    void shouldFindByDateAfter() {
        LocalDate today = LocalDate.of(2026, 1, 1);
        eventRepository.save(Event.builder()
                .name("Past Event").date(today.minusDays(10)).venue(venue).build());
        eventRepository.save(Event.builder()
                .name("Future Event").date(today.plusDays(10)).venue(venue).build());

        List<Event> upcoming = eventRepository.findByDateAfter(today);

        assertThat(upcoming).hasSize(1);
        assertThat(upcoming.get(0).getName()).isEqualTo("Future Event");
    }

    @Test
    void shouldFindByDateBetween() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);

        eventRepository.save(Event.builder()
                .name("In Range").date(LocalDate.of(2026, 6, 15)).venue(venue).build());
        eventRepository.save(Event.builder()
                .name("Out Of Range").date(LocalDate.of(2027, 1, 1)).venue(venue).build());

        List<Event> inRange = eventRepository.findByDateBetween(start, end);

        assertThat(inRange).hasSize(1);
        assertThat(inRange.get(0).getName()).isEqualTo("In Range");
    }

    @Test
    void shouldPaginateAndSortByName() {
        eventRepository.save(Event.builder()
                .name("C Event").date(LocalDate.now()).venue(venue).build());
        eventRepository.save(Event.builder()
                .name("A Event").date(LocalDate.now()).venue(venue).build());
        eventRepository.save(Event.builder()
                .name("B Event").date(LocalDate.now()).venue(venue).build());

        Pageable firstPage = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<Event> page = eventRepository.findAll(firstPage);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).extracting(Event::getName)
                .containsExactly("A Event", "B Event");
    }

    @Test
    void shouldNotReturnInactiveEvents() {
        Event active = eventRepository.save(Event.builder()
                .name("Active Event").date(LocalDate.now()).venue(venue).build());
        Event inactive = eventRepository.save(Event.builder()
                .name("Inactive Event").date(LocalDate.now()).venue(venue).build());

        inactive.deactivate();
        eventRepository.save(inactive);

        List<Event> all = eventRepository.findAll();

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getName()).isEqualTo("Active Event");
    }
}
