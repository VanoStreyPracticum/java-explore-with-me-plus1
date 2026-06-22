package ru.practicum.main.subscription.service;

import ru.practicum.main.subscription.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto subscribe(Long userId, Long targetUserId);

    void unsubscribe(Long userId, Long targetUserId);

    List<SubscriptionDto> getSubscriptions(Long userId, int from, int size);

    List<SubscriptionDto> getFollowers(Long userId, int from, int size);
}
