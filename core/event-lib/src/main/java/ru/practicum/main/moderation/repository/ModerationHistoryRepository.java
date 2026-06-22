package ru.practicum.main.moderation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.moderation.model.ModerationHistory;

@Repository
public interface ModerationHistoryRepository extends JpaRepository<ModerationHistory, Long> {

    Page<ModerationHistory> findAllByEventIdOrderByTimestampDesc(Long eventId, Pageable pageable);
}
