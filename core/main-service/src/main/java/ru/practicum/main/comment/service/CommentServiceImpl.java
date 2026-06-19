package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.dto.AdminCommentActionDto;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.model.CommentStatus;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя комментировать неопубликованное событие");
        }
        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(author)
                .created(LocalDateTime.now())
                .status(CommentStatus.PENDING)
                .build();
        comment = commentRepository.save(comment);
        log.info("Создан комментарий: id={}, eventId={}, userId={}", comment.getId(), eventId, userId);
        return toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не можете редактировать чужой комментарий");
        }
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Редактировать можно только комментарий в статусе ожидания");
        }
        comment.setText(updateDto.getText());
        comment = commentRepository.save(comment);
        log.info("Комментарий обновлён: id={}", commentId);
        return toDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не можете удалить чужой комментарий");
        }
        commentRepository.delete(comment);
        log.info("Пользователь удалил комментарий: id={}", commentId);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByAuthorId(userId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
        return toDto(comment);
    }

    @Override
    public List<CommentDto> getAdminComments(List<String> statuses, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (statuses != null && !statuses.isEmpty()) {
            List<CommentStatus> commentStatuses = statuses.stream()
                    .map(CommentStatus::valueOf)
                    .collect(Collectors.toList());
            return commentRepository.findAllByStatusIn(commentStatuses, pageable)
                    .getContent().stream().map(this::toDto).collect(Collectors.toList());
        }
        return commentRepository.findAll(pageable).getContent().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto moderateComment(Long commentId, AdminCommentActionDto actionDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
        String action = actionDto.getAction();
        if ("PUBLISH".equalsIgnoreCase(action)) {
            comment.setStatus(CommentStatus.PUBLISHED);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            comment.setStatus(CommentStatus.REJECTED);
        } else {
            throw new ValidationException("Некорректное действие модерации: " + action);
        }
        if (actionDto.getModerationNote() != null) {
            comment.setModerationNote(actionDto.getModerationNote());
        }
        comment = commentRepository.save(comment);
        log.info("Администратор изменил статус комментария: id={}, статус={}", commentId, comment.getStatus());
        return toDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
        commentRepository.delete(comment);
        log.info("Администратор удалил комментарий: id={}", commentId);
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .status(comment.getStatus())
                .moderationNote(comment.getModerationNote())
                .build();
    }
}
