package ru.practicum.main.moderation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.moderation.dto.ModerationHistoryDto;
import ru.practicum.main.moderation.service.ModerationHistoryService;

import java.util.List;

@RestController
@RequestMapping("/admin/events/{eventId}/moderation-history")
@RequiredArgsConstructor
@Slf4j
public class ModerationHistoryController {

    private final ModerationHistoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ModerationHistoryDto> getHistory(@PathVariable Long eventId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("GET /admin/events/{}/moderation-history", eventId);
        return service.getEventModerationHistory(eventId, from, size);
    }
}
