package ru.practicum.main.comment.dto;

import jakarta.validation.constraints.NotNull;
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
public class AdminCommentActionDto {
    @NotNull(message = "Действие обязательно")
    private String action; // PUBLISH or REJECT

    private String moderationNote;
}
