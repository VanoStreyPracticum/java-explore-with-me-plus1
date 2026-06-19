package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.dto.AdminCommentActionDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateDto);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getAdminComments(List<String> statuses, int from, int size);

    CommentDto moderateComment(Long commentId, AdminCommentActionDto actionDto);

    void deleteCommentByAdmin(Long commentId);
}
