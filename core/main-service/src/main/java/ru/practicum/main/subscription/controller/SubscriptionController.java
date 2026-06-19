package ru.practicum.main.subscription.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable Long userId,
                                     @RequestParam Long targetUserId) {
        log.info("POST /users/{}/subscriptions?targetUserId={}", userId, targetUserId);
        return subscriptionService.subscribe(userId, targetUserId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable Long userId,
                            @RequestParam Long targetUserId) {
        log.info("DELETE /users/{}/subscriptions?targetUserId={}", userId, targetUserId);
        subscriptionService.unsubscribe(userId, targetUserId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getSubscriptions(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("GET /users/{}/subscriptions", userId);
        return subscriptionService.getSubscriptions(userId, from, size);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getFollowers(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("GET /users/{}/subscriptions/followers", userId);
        return subscriptionService.getFollowers(userId, from, size);
    }
}
