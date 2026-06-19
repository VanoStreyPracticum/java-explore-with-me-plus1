package ru.practicum.main.subscription.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.subscription.model.Subscription;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByFollowerIdAndTargetId(Long followerId, Long targetId);

    Page<Subscription> findAllByFollowerId(Long followerId, Pageable pageable);

    Page<Subscription> findAllByTargetId(Long targetId, Pageable pageable);

    boolean existsByFollowerIdAndTargetId(Long followerId, Long targetId);
}
