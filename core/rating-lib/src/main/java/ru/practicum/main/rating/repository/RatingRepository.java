package ru.practicum.main.rating.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.rating.model.Rating;
import ru.practicum.main.rating.model.Vote;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndEventId(Long userId, Long eventId);

    Page<Rating> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.event.id = :eventId AND r.vote = :vote")
    Long countByEventIdAndVote(@Param("eventId") Long eventId, @Param("vote") Vote vote);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
