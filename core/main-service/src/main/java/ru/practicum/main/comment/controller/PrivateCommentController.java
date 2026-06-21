package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId,
                                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(userId, eventId, newCommentDto));
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long commentId,
                                                    @Valid @RequestBody UpdateCommentDto updateDto) {
        return ResponseEntity.ok(commentService.updateComment(userId, commentId, updateDto));
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getUserComments(@PathVariable @Positive Long userId,
                                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                            @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getUserComments(userId, from, size));
    }
}
