package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(requestService.getUserRequests(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.cancelRequest(userId, requestId));
    }
}
