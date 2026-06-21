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
public class RatingDto {
    private Long id;
    private Long userId;
    private Long eventId;
    /** Vote type: LIKE or DISLIKE. */
    private String vote;
}
