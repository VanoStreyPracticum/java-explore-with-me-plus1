package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category category = Category.builder()
                .name(newCategoryDto.getName())
                .build();
        category = categoryRepository.save(category);
        log.info("Создана категория: id={}, name={}", category.getId(), category.getName());
        return toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + catId));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Категория не пуста, с ней связаны события");
        }
        categoryRepository.delete(category);
        log.info("Удалена категория: id={}", catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + catId));
        if (categoryDto.getName() != null &&
                !categoryDto.getName().equals(category.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        category = categoryRepository.save(category);
        log.info("Обновлена категория: id={}, name={}", catId, category.getName());
        return toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoryRepository.findAll(pageable).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + catId));
        return toDto(category);
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
