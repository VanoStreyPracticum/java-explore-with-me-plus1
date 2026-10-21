package ru.practicum.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request body for updating a compilation.
 * At least one field should be provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {

    /** New title, 1-50 characters. */
    @Size(min = 1, max = 50, message = "Заголовок подборки должен быть от 1 до 50 символов")
    private String title;

    /** Whether the compilation is pinned on the main page. */
    private Boolean pinned;

    /** List of event IDs to include in the compilation. */
    private List<Long> events;
}
