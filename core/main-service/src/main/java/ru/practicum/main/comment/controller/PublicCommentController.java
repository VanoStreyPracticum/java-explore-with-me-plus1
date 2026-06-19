package ru.practicum.main.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getEventComments(@PathVariable @Positive Long eventId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable @Positive Long eventId,
                                 @PathVariable @Positive Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
