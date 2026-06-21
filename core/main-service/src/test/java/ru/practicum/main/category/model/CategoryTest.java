package ru.practicum.main.category.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Category} model.
 */
@DisplayName("Category Model Tests")
class CategoryTest {

    @Test
    @DisplayName("Должен создать категорию через конструктор")
    void constructor_CreatesCategory() {
        Category category = new Category(1L, "Test Category");

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Test Category");
    }

    @Test
    @DisplayName("Должен создать категорию через builder")
    void builder_CreatesCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Builder Category")
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Builder Category");
    }

    @Test
    @DisplayName("Должен создать пустую категорию через no-args конструктор")
    void noArgsConstructor_CreatesEmptyCategory() {
        Category category = new Category();

        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void setters_SetValues() {
        Category category = new Category();

        category.setId(1L);
        category.setName("Setter Category");

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Setter Category");
    }
}
