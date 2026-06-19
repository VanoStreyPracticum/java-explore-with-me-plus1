package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getEventComments(@PathVariable Long eventId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("GET /events/{}/comments", eventId);
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        log.info("GET /events/{}/comments/{}", eventId, commentId);
        return commentService.getCommentById(commentId);
    }
}
