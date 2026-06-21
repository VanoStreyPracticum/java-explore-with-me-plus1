package ru.practicum.main.moderation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.moderation.dto.ModerationHistoryDto;
import ru.practicum.main.moderation.service.ModerationHistoryService;

import java.util.List;

@RestController
@RequestMapping("/admin/events/{eventId}/moderation-history")
@RequiredArgsConstructor
@Validated
public class ModerationHistoryController {

    private final ModerationHistoryService service;

    @GetMapping
    public ResponseEntity<List<ModerationHistoryDto>> getHistory(@PathVariable @Positive Long eventId,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getEventModerationHistory(eventId, from, size));
    }
}
