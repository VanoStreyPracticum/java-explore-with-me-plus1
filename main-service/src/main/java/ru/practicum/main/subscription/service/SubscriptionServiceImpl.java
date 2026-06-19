package ru.practicum.main.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.model.Subscription;
import ru.practicum.main.subscription.repository.SubscriptionRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionDto subscribe(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new ConflictException("Нельзя подписаться на самого себя");
        }
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + targetUserId));
        if (subscriptionRepository.existsByFollowerIdAndTargetId(userId, targetUserId)) {
            throw new ConflictException("Подписка уже существует");
        }
        Subscription subscription = Subscription.builder()
                .follower(follower)
                .target(target)
                .created(LocalDateTime.now())
                .build();
        subscription = subscriptionRepository.save(subscription);
        log.info("Создана подписка: follower={}, target={}", userId, targetUserId);
        return toDto(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long userId, Long targetUserId) {
        Subscription subscription = subscriptionRepository
                .findByFollowerIdAndTargetId(userId, targetUserId)
                .orElseThrow(() -> new NotFoundException("Подписка не найдена"));
        subscriptionRepository.delete(subscription);
        log.info("Удалена подписка: follower={}, target={}", userId, targetUserId);
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionRepository.findAllByFollowerId(userId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDto> getFollowers(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionRepository.findAllByTargetId(userId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private SubscriptionDto toDto(Subscription subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .followerId(subscription.getFollower().getId())
                .targetId(subscription.getTarget().getId())
                .created(subscription.getCreated())
                .build();
    }
}
