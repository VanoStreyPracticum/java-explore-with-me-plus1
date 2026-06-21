package ru.practicum.main.rating.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.rating.dto.EventRatingDto;
import ru.practicum.main.rating.dto.NewRatingDto;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RatingController {

    private final RatingService ratingService;

    @PutMapping("/users/{userId}/events/{eventId}/rating")
    public ResponseEntity<RatingDto> addOrUpdateRating(@PathVariable @Positive Long userId,
                                                       @PathVariable @Positive Long eventId,
                                                       @Valid @RequestBody NewRatingDto newRatingDto) {
        return ResponseEntity.ok(ratingService.addOrUpdateRating(userId, eventId, newRatingDto));
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/rating")
    public ResponseEntity<Void> removeRating(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long eventId) {
        ratingService.removeRating(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/ratings")
    public ResponseEntity<List<RatingDto>> getUserRatings(@PathVariable @Positive Long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                          @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId, from, size));
    }

    @GetMapping("/events/{eventId}/rating")
    public ResponseEntity<EventRatingDto> getEventRating(@PathVariable @Positive Long eventId) {
        return ResponseEntity.ok(ratingService.getEventRating(eventId));
    }
}
