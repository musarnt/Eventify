package com.eventify.config;

import com.eventify.model.Category;
import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.CategoryRepository;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(EventService eventService,
                                      VenueService venueService,
                                      CategoryService categoryService,
                                      EventRepository eventRepository,
                                      VenueRepository venueRepository,
                                      CategoryRepository categoryRepository) {
        return args -> {

            if (categoryRepository.count() == 0) {
                categoryService.create(Category.builder()
                        .name("Concerts").description("Live music performances").build());
                categoryService.create(Category.builder()
                        .name("Workshops").description("Hands-on learning sessions").build());
                categoryService.create(Category.builder()
                        .name("Conferences").description("Professional talks and panels").build());
                categoryService.create(Category.builder()
                        .name("Sports").description("Athletic competitions and events").build());
                categoryService.create(Category.builder()
                        .name("Gastronomy").description("Food and culinary experiences").build());
                categoryService.create(Category.builder()
                        .name("Festivals").description("Large-scale cultural celebrations").build());
                categoryService.create(Category.builder()
                        .name("Theater").description("Stage plays and dramatic performances").build());

                System.out.println(" Initial categories seeded");
            }

            Venue theater = null;
            Venue explora = null;

            if (venueRepository.count() == 0) {
                theater = venueService.create(Venue.builder()
                        .name("Metropolitan Theater")
                        .address("Street 41 #57-30")
                        .city("Medellin")
                        .capacity(1200)
                        .build());

                explora = venueService.create(Venue.builder()
                        .name("Parque Explora")
                        .address("Avenue 52 #73-75")
                        .city("Medellin")
                        .capacity(500)
                        .build());

                System.out.println(" Initial venues seeded");
            }

            if (eventRepository.count() == 0 && theater != null && explora != null) {
                Category concerts = categoryRepository.findByNameIgnoreCase("Concerts").orElseThrow();
                Category festivals = categoryRepository.findByNameIgnoreCase("Festivals").orElseThrow();
                Category conferences = categoryRepository.findByNameIgnoreCase("Conferences").orElseThrow();
                Category workshops = categoryRepository.findByNameIgnoreCase("Workshops").orElseThrow();

                eventService.create(Event.builder()
                        .name("Symphonic Concert")
                        .date(LocalDate.of(2026, 8, 15))
                        .description("A classical music night at the Metropolitan Theater")
                        .venue(theater)
                        .categories(Set.of(concerts, festivals))
                        .build());

                eventService.create(Event.builder()
                        .name("Tech Fair")
                        .date(LocalDate.of(2026, 9, 20))
                        .description("Innovation and startups exhibition")
                        .venue(explora)
                        .categories(Set.of(conferences, workshops))
                        .build());

                System.out.println(" Initial events seeded");
            }
        };
    }
}
