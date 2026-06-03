package com.eventify.repository;

import com.eventify.model.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @BeforeEach
    void setUp() {
        venueRepository.deleteAll();
    }

    @Test
    void shouldPersistVenueAndGenerateId() {
        Venue venue = Venue.builder()
                .name("Metropolitan Theater")
                .address("Street 41 #57-30")
                .city("Medellin")
                .capacity(1200)
                .build();

        Venue saved = venueRepository.save(venue);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Metropolitan Theater");
        assertThat(saved.getCity()).isEqualTo("Medellin");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        venueRepository.save(Venue.builder()
                .name("Metropolitan Theater").address("addr").city("Medellin").capacity(1000).build());
        venueRepository.save(Venue.builder()
                .name("Parque Explora").address("addr").city("Medellin").capacity(500).build());

        List<Venue> results = venueRepository.findByNameContainingIgnoreCase("metro");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Metropolitan Theater");
    }

    @Test
    void shouldFindByCapacityGreaterThanEqual() {
        venueRepository.save(Venue.builder()
                .name("Small Hall").address("addr").city("Bogota").capacity(100).build());
        venueRepository.save(Venue.builder()
                .name("Big Arena").address("addr").city("Medellin").capacity(5000).build());

        List<Venue> large = venueRepository.findByCapacityGreaterThanEqual(1000);

        assertThat(large).hasSize(1);
        assertThat(large.get(0).getName()).isEqualTo("Big Arena");
    }

    @Test
    void shouldPaginateAndSortByCapacityDesc() {
        venueRepository.save(Venue.builder()
                .name("V1").address("addr").city("Cali").capacity(100).build());
        venueRepository.save(Venue.builder()
                .name("V2").address("addr").city("Cali").capacity(500).build());
        venueRepository.save(Venue.builder()
                .name("V3").address("addr").city("Cali").capacity(300).build());

        Pageable firstPage = PageRequest.of(0, 2, Sort.by("capacity").descending());
        Page<Venue> page = venueRepository.findAll(firstPage);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(Venue::getCapacity)
                .containsExactly(500, 300);
    }
}
