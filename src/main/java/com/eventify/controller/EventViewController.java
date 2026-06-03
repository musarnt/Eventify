package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.CategoryService;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/events")
public class EventViewController {

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    public EventViewController(EventService eventService,
                               VenueService venueService,
                               CategoryService categoryService) {
        this.eventService = eventService;
        this.venueService = venueService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        model.addAttribute("events", eventService.findAll(pageable).getContent());
        model.addAttribute("view", "events/list");
        model.addAttribute("pageTitle", "Events - Eventify");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("event", new Event());
        populateFormData(model);
        model.addAttribute("view", "events/form");
        model.addAttribute("pageTitle", "New Event - Eventify");
        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        populateFormData(model);
        model.addAttribute("view", "events/form");
        model.addAttribute("pageTitle", "Edit Event - Eventify");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Event event,
                       @RequestParam Long venueId,
                       @RequestParam(required = false) Set<Long> categoryIds,
                       Model model) {
        try {
            event.setVenue(venueService.findById(venueId));
            if (categoryIds != null && !categoryIds.isEmpty()) {
                event.setCategories(categoryService.findByIds(categoryIds));
            } else {
                event.setCategories(new HashSet<>());
            }

            if (event.getId() != null) {
                eventService.update(event.getId(), event);
            } else {
                eventService.create(event);
            }
            return "redirect:/admin/events";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            populateFormData(model);
            model.addAttribute("view", "events/form");
            model.addAttribute("pageTitle", "Event - Eventify");
            return "layout";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        eventService.delete(id);
        return "redirect:/admin/events";
    }

    private void populateFormData(Model model) {
        model.addAttribute("venues", venueService.findAll(Pageable.unpaged()).getContent());
        model.addAttribute("allCategories", categoryService.findAll(Pageable.unpaged()).getContent());
    }
}
