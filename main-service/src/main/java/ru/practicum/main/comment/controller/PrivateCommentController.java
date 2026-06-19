package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST /users/{}/events/{}/comments", userId, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateDto) {
        log.info("PATCH /users/{}/comments/{}", userId, commentId);
        return commentService.updateComment(userId, commentId, updateDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("DELETE /users/{}/comments/{}", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserComments(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /users/{}/comments", userId);
        return commentService.getUserComments(userId, from, size);
    }
}
