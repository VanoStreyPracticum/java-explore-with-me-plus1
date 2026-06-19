package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    @NotBlank(message = "Заголовок подборки обязателен")
    @Size(min = 1, max = 50, message = "Заголовок подборки должен быть от 1 до 50 символов")
    private String title;

    @Builder.Default
    private Boolean pinned = false;

    private List<Long> events;
}
