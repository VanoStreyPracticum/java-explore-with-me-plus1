package ru.practicum.main.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
@Validated
public class EventRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        return ResponseEntity.ok(requestService.getEventRequests(userId, eventId));
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return ResponseEntity.ok(requestService.updateRequestStatus(userId, eventId, updateRequest));
    }
}
