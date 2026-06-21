package ru.practicum.main.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserShortDto}.
 */
@DisplayName("UserShortDto Tests")
class UserShortDtoTest {

    @Test
    @DisplayName("Должен создать DTO через конструктор")
    void constructor_CreatesDto() {
        UserShortDto dto = new UserShortDto(1L, "Test User");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        UserShortDto dto = UserShortDto.builder()
                .id(1L)
                .name("Builder User")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Builder User");
    }

    @Test
    @DisplayName("Должен создать пустой DTO через no-args конструктор")
    void noArgsConstructor_CreatesEmptyDto() {
        UserShortDto dto = new UserShortDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    @DisplayName("Setters и Getters должны работать")
    void settersAndGetters_Work() {
        UserShortDto dto = new UserShortDto();

        dto.setId(1L);
        dto.setName("Setter User");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Setter User");
    }
}
