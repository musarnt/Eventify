package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    // Runs automatically on application startup.
    // Idempotent: each table is seeded only when empty, so user-created data
    // survives application restarts when using persistent H2.
    @Bean
    public CommandLineRunner loadData(EventService eventService,
                                      VenueService venueService,
                                      EventRepository eventRepository,
                                      VenueRepository venueRepository) {
        return args -> {

            if (venueRepository.count() == 0) {
                venueService.create(Venue.builder()
                        .name("Metropolitan Theater")
                        .address("Street 41 #57-30, Medellin")
                        .capacity(1200)
                        .build());

                venueService.create(Venue.builder()
                        .name("Parque Explora")
                        .address("Avenue 52 #73-75, Medellin")
                        .capacity(500)
                        .build());

                System.out.println(" Initial venues seeded");
            }

            if (eventRepository.count() == 0) {
                eventService.create(Event.builder()
                        .name("Symphonic Concert")
                        .date(LocalDate.of(2026, 8, 15))
                        .description("A classical music night at the Metropolitan Theater")
                        .build());

                eventService.create(Event.builder()
                        .name("Tech Fair")
                        .date(LocalDate.of(2026, 9, 20))
                        .description("Innovation and startups exhibition")
                        .build());

                System.out.println(" Initial events seeded");
            }
        };
    }
}