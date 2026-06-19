package ru.practicum.main.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.rating.dto.EventRatingDto;
import ru.practicum.main.rating.dto.NewRatingDto;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.model.Rating;
import ru.practicum.main.rating.model.Vote;
import ru.practicum.main.rating.repository.RatingRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RatingDto addOrUpdateRating(Long userId, Long eventId, NewRatingDto newRatingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя оценить неопубликованное событие");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может оценивать своё событие");
        }
        Vote vote;
        try {
            vote = Vote.valueOf(newRatingDto.getVote().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Некорректный голос: " + newRatingDto.getVote());
        }
        Rating rating = ratingRepository.findByUserIdAndEventId(userId, eventId)
                .orElse(null);
        if (rating == null) {
            rating = Rating.builder()
                    .user(user)
                    .event(event)
                    .vote(vote)
                    .created(LocalDateTime.now())
                    .build();
        } else {
            rating.setVote(vote);
            rating.setCreated(LocalDateTime.now());
        }
        rating = ratingRepository.save(rating);
        log.info("Пользователь {} оценил событие {}: {}", userId, eventId, vote);
        return toDto(rating);
    }

    @Override
    @Transactional
    public void removeRating(Long userId, Long eventId) {
        Rating rating = ratingRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Оценка не найдена"));
        ratingRepository.delete(rating);
        log.info("Пользователь {} удалил оценку события {}", userId, eventId);
    }

    @Override
    public List<RatingDto> getUserRatings(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return ratingRepository.findAllByUserId(userId, pageable)
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRatingDto getEventRating(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено: id=" + eventId);
        }
        Long likes = ratingRepository.countByEventIdAndVote(eventId, Vote.LIKE);
        Long dislikes = ratingRepository.countByEventIdAndVote(eventId, Vote.DISLIKE);
        return EventRatingDto.builder()
                .eventId(eventId)
                .likes(likes)
                .dislikes(dislikes)
                .build();
    }

    private RatingDto toDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .userId(rating.getUser().getId())
                .eventId(rating.getEvent().getId())
                .vote(rating.getVote().name())
                .build();
    }
}
