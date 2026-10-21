package ru.practicum.main.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for custom exceptions.
 */
@DisplayName("Custom Exceptions Tests")
class ExceptionsTest {

    @Nested
    @DisplayName("NotFoundException")
    class NotFoundExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            NotFoundException exception = new NotFoundException("Entity not found");

            assertThat(exception.getMessage()).isEqualTo("Entity not found");
        }

        @Test
        @DisplayName("Должен быть наследником RuntimeException")
        void isRuntimeException() {
            NotFoundException exception = new NotFoundException("test");

            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            assertThatThrownBy(() -> {
                throw new NotFoundException("Event with id=1 not found");
            })
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Event with id=1 not found");
        }
    }

    @Nested
    @DisplayName("ConflictException")
    class ConflictExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            ConflictException exception = new ConflictException("Conflict occurred");

            assertThat(exception.getMessage()).isEqualTo("Conflict occurred");
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            assertThatThrownBy(() -> {
                throw new ConflictException("Email already exists");
            })
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Email already exists");
        }
    }

    @Nested
    @DisplayName("ValidationException")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            ValidationException exception = new ValidationException("Validation failed");

            assertThat(exception.getMessage()).isEqualTo("Validation failed");
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            assertThatThrownBy(() -> {
                throw new ValidationException("Invalid date format");
            })
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid date format");
        }
    }

    @Nested
    @DisplayName("ApiError")
    class ApiErrorTests {

        @Test
        @DisplayName("Должен создать ApiError через builder")
        void builderCreatesApiError() {
            LocalDateTime timestamp = LocalDateTime.now();

            ApiError apiError = ApiError.builder()
                    .status("NOT_FOUND")
                    .reason("Resource not found")
                    .message("Event with id=1 was not found")
                    .timestamp(timestamp)
                    .errors(List.of("error1", "error2"))
                    .build();

            assertThat(apiError.getStatus()).isEqualTo("NOT_FOUND");
            assertThat(apiError.getReason()).isEqualTo("Resource not found");
            assertThat(apiError.getMessage()).isEqualTo("Event with id=1 was not found");
            assertThat(apiError.getTimestamp()).isEqualTo(timestamp);
            assertThat(apiError.getErrors()).containsExactly("error1", "error2");
        }

        @Test
        @DisplayName("Должен создать пустой ApiError")
        void noArgsConstructorCreatesEmptyApiError() {
            ApiError apiError = new ApiError();

            assertThat(apiError.getStatus()).isNull();
            assertThat(apiError.getMessage()).isNull();
        }
    }
}
