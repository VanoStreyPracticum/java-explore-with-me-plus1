package ru.practicum.main.category.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CategoryDto} validation.
 */
@DisplayName("CategoryDto Tests")
class CategoryDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Конструкторы и Builder")
    class ConstructorTests {

        @Test
        @DisplayName("Должен создать DTO через конструктор")
        void constructor_CreatesDto() {
            CategoryDto dto = new CategoryDto(1L, "Test Category");

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Test Category");
        }

        @Test
        @DisplayName("Должен создать DTO через builder")
        void builder_CreatesDto() {
            CategoryDto dto = CategoryDto.builder()
                    .id(1L)
                    .name("Builder Category")
                    .build();

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Builder Category");
        }

        @Test
        @DisplayName("Должен создать пустой DTO через no-args конструктор")
        void noArgsConstructor_CreatesEmptyDto() {
            CategoryDto dto = new CategoryDto();

            assertThat(dto.getId()).isNull();
            assertThat(dto.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("Валидация")
    class ValidationTests {

        @Test
        @DisplayName("Валидный DTO не должен иметь нарушений")
        void validDto_NoViolations() {
            CategoryDto dto = new CategoryDto(1L, "Valid Category");

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Пустое название должно нарушать валидацию")
        void blankName_HasViolation() {
            CategoryDto dto = new CategoryDto(1L, "");

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("name");
        }

        @Test
        @DisplayName("Null название должно нарушать валидацию")
        void nullName_HasViolation() {
            CategoryDto dto = new CategoryDto(1L, null);

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Слишком длинное название должно нарушать валидацию")
        void tooLongName_HasViolation() {
            String longName = "A".repeat(51); // more than 50 characters
            CategoryDto dto = new CategoryDto(1L, longName);

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("name");
        }

        @Test
        @DisplayName("Название максимальной длины должно быть валидным")
        void maxLengthName_NoViolation() {
            String maxName = "A".repeat(50); // exactly 50 characters
            CategoryDto dto = new CategoryDto(1L, maxName);

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Название минимальной длины должно быть валидным")
        void minLengthName_NoViolation() {
            CategoryDto dto = new CategoryDto(1L, "A"); // 1 character

            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }
    }
}
