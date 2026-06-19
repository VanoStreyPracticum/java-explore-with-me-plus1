package ru.practicum.main.subscription.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable @Positive Long userId,
                                     @RequestParam @Positive Long targetUserId) {
        return subscriptionService.subscribe(userId, targetUserId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable @Positive Long userId,
                            @RequestParam @Positive Long targetUserId) {
        subscriptionService.unsubscribe(userId, targetUserId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getSubscriptions(@PathVariable @Positive Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getSubscriptions(userId, from, size);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getFollowers(@PathVariable @Positive Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getFollowers(userId, from, size);
    }
}
