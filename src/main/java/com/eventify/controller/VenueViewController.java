package com.eventify.controller;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
@RequestMapping("/admin/venues")
public class VenueViewController {

    private final VenueService venueService;

    public VenueViewController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Slice<Venue> slice = venueService.findAll(pageable);
        model.addAttribute("venues", slice.getContent());
        model.addAttribute("slice", slice);
        model.addAttribute("view", "venues/list");
        model.addAttribute("pageTitle", "Venues - Eventify");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("venue", new Venue());
        model.addAttribute("view", "venues/form");
        model.addAttribute("pageTitle", "New Venue - Eventify");
        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("venue", venueService.findById(id));
        model.addAttribute("view", "venues/form");
        model.addAttribute("pageTitle", "Edit Venue - Eventify");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Venue venue) {
        if (venue.getId() != null) {
            venueService.update(venue.getId(), venue);
        } else {
            venueService.create(venue);
        }
        return "redirect:/admin/venues";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        venueService.delete(id);
        return "redirect:/admin/venues";
    }
}
