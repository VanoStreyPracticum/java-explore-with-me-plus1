package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.analyzer.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_actions (user_id, event_id, weight) VALUES (:userId, :eventId, :weight) " +
           "ON CONFLICT (user_id, event_id) DO UPDATE SET weight = GREATEST(user_actions.weight, EXCLUDED.weight)",
           nativeQuery = true)
    void upsert(@Param("userId") Long userId, @Param("eventId") Long eventId, @Param("weight") Double weight);

    @Query("SELECT ua FROM UserAction ua WHERE ua.id.userId = :userId")
    List<UserAction> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT ua FROM UserAction ua WHERE ua.id.eventId = :eventId")
    List<UserAction> findAllByEventId(@Param("eventId") Long eventId);
}
