package ru.practicum.main.rating.service;

import ru.practicum.main.rating.dto.EventRatingDto;
import ru.practicum.main.rating.dto.NewRatingDto;
import ru.practicum.main.rating.dto.RatingDto;

import java.util.List;

public interface RatingService {

    RatingDto addOrUpdateRating(Long userId, Long eventId, NewRatingDto newRatingDto);

    void removeRating(Long userId, Long eventId);

    List<RatingDto> getUserRatings(Long userId, int from, int size);

    EventRatingDto getEventRating(Long eventId);
}
