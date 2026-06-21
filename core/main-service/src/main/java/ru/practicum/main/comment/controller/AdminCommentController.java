package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.AdminCommentActionDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@RequestParam(required = false) List<String> statuses,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getAdminComments(statuses, from, size));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> moderateComment(@PathVariable @Positive Long commentId,
                                                      @Valid @RequestBody AdminCommentActionDto actionDto) {
        return ResponseEntity.ok(commentService.moderateComment(commentId, actionDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Positive Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }
}
