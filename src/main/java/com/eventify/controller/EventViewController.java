package com.eventify.controller;

import com.eventify.model.Event;
import com.eventify.service.EventService;
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

@Controller
@RequestMapping("/admin/events")
public class EventViewController {

    private final EventService eventService;

    public EventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    // List all events with pagination
    @GetMapping
    public String list(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        model.addAttribute("events", eventService.findAll(pageable).getContent());
        model.addAttribute("view", "events/list");
        model.addAttribute("pageTitle", "Events - Eventify");
        return "layout";
    }

    // Show empty form for new event
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("view", "events/form");
        model.addAttribute("pageTitle", "New Event - Eventify");
        return "layout";
    }

    // Show pre-filled form for editing
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        model.addAttribute("view", "events/form");
        model.addAttribute("pageTitle", "Edit Event - Eventify");
        return "layout";
    }

    // Handle form submission (create or update)
    @PostMapping("/save")
    public String save(@ModelAttribute Event event, Model model) {
        try {
            if (event.getId() != null) {
                eventService.update(event.getId(), event);
            } else {
                eventService.create(event);
            }
            return "redirect:/admin/events";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("view", "events/form");
            model.addAttribute("pageTitle", "Event - Eventify");
            return "layout";
        }
    }

    // Delete and redirect back to list
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        eventService.delete(id);
        return "redirect:/admin/events";
    }
}