package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        return ResponseEntity.ok(categoryService.getCategoryById(catId));
    }
}
