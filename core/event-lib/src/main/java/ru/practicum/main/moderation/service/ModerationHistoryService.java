package ru.practicum.main.moderation.service;

import ru.practicum.main.moderation.dto.ModerationHistoryDto;

import java.util.List;

public interface ModerationHistoryService {

    List<ModerationHistoryDto> getEventModerationHistory(Long eventId, int from, int size);
}
