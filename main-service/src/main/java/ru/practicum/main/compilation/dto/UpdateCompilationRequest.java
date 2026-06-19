package ru.practicum.main.compilation.dto;

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
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50, message = "Заголовок подборки должен быть от 1 до 50 символов")
    private String title;

    private Boolean pinned;

    private List<Long> events;
}
