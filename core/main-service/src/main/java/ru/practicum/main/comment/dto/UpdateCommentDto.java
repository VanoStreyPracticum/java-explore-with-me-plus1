package ru.practicum.main.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {
    @Size(min = 1, max = 2000, message = "Текст комментария должен быть от 1 до 2000 символов")
    private String text;
}
