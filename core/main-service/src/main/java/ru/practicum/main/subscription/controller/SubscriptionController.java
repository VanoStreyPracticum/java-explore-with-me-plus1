package ru.practicum.main.subscription.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDto> subscribe(@PathVariable @Positive Long userId,
                                                     @RequestParam @Positive Long targetUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(userId, targetUserId));
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@PathVariable @Positive Long userId,
                                            @RequestParam @Positive Long targetUserId) {
        subscriptionService.unsubscribe(userId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getSubscriptions(@PathVariable @Positive Long userId,
                                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(userId, from, size));
    }

    @GetMapping("/followers")
    public ResponseEntity<List<SubscriptionDto>> getFollowers(@PathVariable @Positive Long userId,
                                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                              @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(subscriptionService.getFollowers(userId, from, size));
    }
}
