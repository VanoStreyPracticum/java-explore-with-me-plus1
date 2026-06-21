package ru.practicum.main.moderation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationHistoryDto {
    private Long id;
    private Long eventId;
    private Long moderatorId;
    private String action;
    private String moderationNote;
    private LocalDateTime timestamp;
}
