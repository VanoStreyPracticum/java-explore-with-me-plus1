package ru.practicum.main.moderation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.moderation.dto.ModerationHistoryDto;
import ru.practicum.main.moderation.model.ModerationHistory;
import ru.practicum.main.moderation.repository.ModerationHistoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ModerationHistoryServiceImpl implements ModerationHistoryService {

    private final ModerationHistoryRepository repository;

    @Override
    public List<ModerationHistoryDto> getEventModerationHistory(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ModerationHistoryDto> result = repository.findAllByEventIdOrderByTimestampDesc(eventId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("Получена история модерации события eventId={}: count={}", eventId, result.size());
        return result;
    }

    private ModerationHistoryDto toDto(ModerationHistory entity) {
        return ModerationHistoryDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .moderatorId(entity.getModeratorId())
                .action(entity.getAction())
                .moderationNote(entity.getModerationNote())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
