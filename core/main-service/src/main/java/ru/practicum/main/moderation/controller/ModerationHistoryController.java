package ru.practicum.main.moderation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.moderation.dto.ModerationHistoryDto;
import ru.practicum.main.moderation.service.ModerationHistoryService;

import java.util.List;

@RestController
@RequestMapping("/admin/events/{eventId}/moderation-history")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ModerationHistoryController {

    private final ModerationHistoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ModerationHistoryDto> getHistory(@PathVariable @Positive Long eventId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return service.getEventModerationHistory(eventId, from, size);
    }
}
