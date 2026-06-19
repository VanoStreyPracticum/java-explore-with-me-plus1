package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateDto) {
        return commentService.updateComment(userId, commentId, updateDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserComments(@PathVariable @Positive Long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size) {
        return commentService.getUserComments(userId, from, size);
    }
}
