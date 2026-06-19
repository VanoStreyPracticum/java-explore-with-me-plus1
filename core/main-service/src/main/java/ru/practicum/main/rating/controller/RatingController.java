package ru.practicum.main.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.rating.dto.EventRatingDto;
import ru.practicum.main.rating.dto.NewRatingDto;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("/users/{userId}/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto addOrUpdateRating(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @Valid @RequestBody NewRatingDto newRatingDto) {
        log.info("PUT /users/{}/events/{}/rating", userId, eventId);
        return ratingService.addOrUpdateRating(userId, eventId, newRatingDto);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRating(@PathVariable Long userId,
                             @PathVariable Long eventId) {
        log.info("DELETE /users/{}/events/{}/rating", userId, eventId);
        ratingService.removeRating(userId, eventId);
    }

    @GetMapping("/users/{userId}/ratings")
    @ResponseStatus(HttpStatus.OK)
    public List<RatingDto> getUserRatings(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("GET /users/{}/ratings", userId);
        return ratingService.getUserRatings(userId, from, size);
    }

    @GetMapping("/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventRatingDto getEventRating(@PathVariable Long eventId) {
        log.info("GET /events/{}/rating", eventId);
        return ratingService.getEventRating(eventId);
    }
}
