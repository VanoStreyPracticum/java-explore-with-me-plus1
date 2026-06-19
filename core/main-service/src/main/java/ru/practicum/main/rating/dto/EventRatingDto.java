package ru.practicum.main.rating.dto;

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
public class EventRatingDto {
    private Long eventId;
    private Long likes;
    private Long dislikes;
}
