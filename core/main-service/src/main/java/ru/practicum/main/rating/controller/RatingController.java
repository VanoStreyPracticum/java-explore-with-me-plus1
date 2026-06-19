package ru.practicum.main.rating.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.rating.dto.EventRatingDto;
import ru.practicum.main.rating.dto.NewRatingDto;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("/users/{userId}/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto addOrUpdateRating(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Long eventId,
                                       @Valid @RequestBody NewRatingDto newRatingDto) {
        return ratingService.addOrUpdateRating(userId, eventId, newRatingDto);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRating(@PathVariable @Positive Long userId,
                             @PathVariable @Positive Long eventId) {
        ratingService.removeRating(userId, eventId);
    }

    @GetMapping("/users/{userId}/ratings")
    @ResponseStatus(HttpStatus.OK)
    public List<RatingDto> getUserRatings(@PathVariable @Positive Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size) {
        return ratingService.getUserRatings(userId, from, size);
    }

    @GetMapping("/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto getEventRating(@PathVariable @Positive Long eventId) {
        return ratingService.getEventRating(eventId);
    }
}
