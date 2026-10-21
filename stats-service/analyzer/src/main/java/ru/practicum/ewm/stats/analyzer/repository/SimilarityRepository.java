package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;

import java.util.List;

public interface SimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO event_similarities (event_a, event_b, score) VALUES (:eventA, :eventB, :score) " +
           "ON CONFLICT (event_a, event_b) DO UPDATE SET score = EXCLUDED.score",
           nativeQuery = true)
    void upsert(@Param("eventA") Long eventA, @Param("eventB") Long eventB, @Param("score") Double score);

    @Query("SELECT s FROM EventSimilarity s WHERE s.id.eventA = :eventId OR s.id.eventB = :eventId")
    List<EventSimilarity> findByEventId(@Param("eventId") Long eventId);
}
