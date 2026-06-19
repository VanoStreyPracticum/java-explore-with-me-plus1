package ru.practicum.main.moderation.service;

import lombok.RequiredArgsConstructor;
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
@Transactional(readOnly = true)
public class ModerationHistoryServiceImpl implements ModerationHistoryService {

    private final ModerationHistoryRepository repository;

    @Override
    public List<ModerationHistoryDto> getEventModerationHistory(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAllByEventIdOrderByTimestampDesc(eventId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
