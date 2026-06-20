package ru.practicum.main.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.searchEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        return ResponseEntity.ok(eventService.updateEventByAdmin(eventId, updateRequest));
    }
}
