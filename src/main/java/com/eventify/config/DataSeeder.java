package com.eventify.config;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    // Runs automatically on application startup
    @Bean
    public CommandLineRunner loadData(EventService eventService, VenueService venueService) {
        return args -> {

            // Seed initial venues
            venueService.create(Venue.builder()
                    .id(1L)
                    .name("Metropolitan Theater")
                    .address("Street 41 #57-30, Medellin")
                    .capacity(1200)
                    .build());

            venueService.create(Venue.builder()
                    .id(2L)
                    .name("Parque Explora")
                    .address("Avenue 52 #73-75, Medellin")
                    .capacity(500)
                    .build());

            // Seed initial events
            eventService.create(Event.builder()
                    .id(1L)
                    .name("Symphonic Concert")
                    .date(LocalDate.of(2025, 8, 15))
                    .description("A classical music night at the Metropolitan Theater")
                    .build());

            eventService.create(Event.builder()
                    .id(2L)
                    .name("Tech Fair")
                    .date(LocalDate.of(2025, 9, 20))
                    .description("Innovation and startups exhibition")
                    .build());

            System.out.println("✅ Initial data loaded successfully");
        };
    }
}