package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.AdminCommentActionDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@RequestParam(required = false) List<String> statuses,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("GET /admin/comments?statuses={}&from={}&size={}", statuses, from, size);
        return commentService.getAdminComments(statuses, from, size);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto moderateComment(@PathVariable Long commentId,
                                      @Valid @RequestBody AdminCommentActionDto actionDto) {
        log.info("PATCH /admin/comments/{}", commentId);
        return commentService.moderateComment(commentId, actionDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("DELETE /admin/comments/{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
