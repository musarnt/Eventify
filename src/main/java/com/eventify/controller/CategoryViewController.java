package com.eventify.controller;

import com.eventify.model.Category;
import com.eventify.service.CategoryService;
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
@RequestMapping("/admin/categories")
public class CategoryViewController {

    private final CategoryService categoryService;

    public CategoryViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
                       Model model) {
        Slice<Category> slice = categoryService.findAll(pageable);
        model.addAttribute("categories", slice.getContent());
        model.addAttribute("slice", slice);
        model.addAttribute("view", "categories/list");
        model.addAttribute("pageTitle", "Categories - Eventify");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("view", "categories/form");
        model.addAttribute("pageTitle", "New Category - Eventify");
        return "layout";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("view", "categories/form");
        model.addAttribute("pageTitle", "Edit Category - Eventify");
        return "layout";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category, Model model) {
        try {
            if (category.getId() != null) {
                categoryService.update(category.getId(), category);
            } else {
                categoryService.create(category);
            }
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("view", "categories/form");
            model.addAttribute("pageTitle", "Category - Eventify");
            return "layout";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}
