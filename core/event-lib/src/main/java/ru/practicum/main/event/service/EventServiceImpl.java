package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.client.grpc.AnalyzerClient;
import ru.practicum.client.grpc.CollectorClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.ewm.stats.proto.StatsServiceProto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.moderation.model.ModerationHistory;
import ru.practicum.main.moderation.repository.ModerationHistoryRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private static final String APP_NAME = "ewm-main-service";
    private static final int HOURS_BEFORE_EVENT_USER = 2;
    private static final int HOURS_BEFORE_EVENT_ADMIN = 1;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;
    private final CollectorClient collectorClient;
    private final AnalyzerClient analyzerClient;
    private final ModerationHistoryRepository moderationHistoryRepository;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        validateUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<EventShortDto> result = eventMapper.toEventShortDtoList(
                eventRepository.findAllByInitiatorId(userId, pageable).getContent());
        log.info("Получены события пользователя userId={}: count={}", userId, result.size());
        return result;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + newEventDto.getCategory()));
        validateEventDate(newEventDto.getEventDate(), HOURS_BEFORE_EVENT_USER);
        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setLocation(eventMapper.toLocation(newEventDto.getLocation()));
        Event savedEvent = eventRepository.save(event);
        log.info("Создано событие: id={}", savedEvent.getId());
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        validateUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        log.info("Получено событие пользователя userId={}, eventId={}", userId, eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        validateUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }
        if (updateRequest.getEventDate() != null) {
            validateEventDate(updateRequest.getEventDate(), HOURS_BEFORE_EVENT_USER);
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + updateRequest.getCategory()));
            event.setCategory(category);
        }
        eventMapper.updateEventFromUserRequest(updateRequest, event);
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }
        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено пользователем: id={}", updatedEvent.getId());
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> searchEventsForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<EventFullDto> result = eventMapper.toEventFullDtoList(
                eventRepository.findEventsForAdmin(
                        users, states, categories, rangeStart, rangeEnd, pageable).getContent());
        log.info("Администратор выполнил поиск событий: users={}, states={}, categories={}, count={}",
                users, states, categories, result.size());
        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        if (updateRequest.getEventDate() != null) {
            validateEventDate(updateRequest.getEventDate(), HOURS_BEFORE_EVENT_ADMIN);
        }

        String moderationNote = updateRequest.getModerationNote();
        boolean stateChanged = false;
        String action = null;

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Опубликовать можно только событие в статусе ожидания");
                    }
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT_ADMIN))) {
                        throw new ConflictException("Дата начала события должна быть не ранее чем за час от публикации");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    action = "PUBLISH_EVENT";
                    stateChanged = true;
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отклонить опубликованное событие");
                    }
                    event.setState(EventState.CANCELED);
                    action = "REJECT_EVENT";
                    stateChanged = true;
                }
            }
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + updateRequest.getCategory()));
            event.setCategory(category);
        }

        eventMapper.updateEventFromAdminRequest(updateRequest, event);
        Event updatedEvent = eventRepository.save(event);

        if (stateChanged) {
            ModerationHistory history = ModerationHistory.builder()
                    .event(updatedEvent)
                    .moderatorId(null)
                    .action(action)
                    .moderationNote(moderationNote)
                    .timestamp(LocalDateTime.now())
                    .build();
            moderationHistoryRepository.save(history);
        }

        log.info("Событие обновлено администратором: id={}", updatedEvent.getId());
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> searchPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd;
        if (end != null && start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть после даты окончания");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findPublicEvents(
                text, categories, paid, start, end,
                onlyAvailable != null && onlyAvailable, pageable).getContent();
        saveHit(request);
        Map<Long, Long> viewsMap = getViewsForEvents(events);
        events.forEach(e -> e.setViews(viewsMap.getOrDefault(e.getId(), 0L)));
        if ("VIEWS".equalsIgnoreCase(sort)) {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getViews).reversed())
                    .collect(Collectors.toList());
        } else {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .collect(Collectors.toList());
        }
        log.info("Публичный поиск событий: text={}, categories={}, count={}", text, categories, events.size());
        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        saveHit(request);
        Map<Long, Long> viewsMap = getViewsForEvents(List.of(event));
        event.setViews(viewsMap.getOrDefault(eventId, 0L));
        try {
            Long userId = extractUserIdFromHeader(request);
            if (userId != 0L) {
                collectorClient.sendUserAction(userId, eventId, StatsServiceProto.ActionTypeProto.ACTION_VIEW);
            }
        } catch (Exception e) {
            log.warn("Не удалось отправить действие просмотра: {}", e.getMessage());
        }
        double rating = 0.0;
        try {
            rating = analyzerClient.getInteractionsCount(eventId);
        } catch (Exception e) {
            log.warn("Не удалось получить рейтинг из Analyzer: {}", e.getMessage());
        }
        event.setRating(rating);
        log.info("Получено опубликованное событие id={}", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        log.info("Получено событие id={}", eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getRecommendations(Long userId, int maxResults) {
        try {
            List<StatsServiceProto.RecommendedEventProto> recs = analyzerClient.getRecommendationsForUser(userId, maxResults);
            return recs.stream()
                    .map(r -> {
                        Event event = eventRepository.findById(r.getEventId()).orElse(null);
                        if (event == null) return null;
                        EventShortDto dto = eventMapper.toEventShortDto(event);
                        dto.setRating(r.getScore());
                        return dto;
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Не удалось получить рекомендации: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        try {
            collectorClient.sendUserAction(userId, eventId, StatsServiceProto.ActionTypeProto.ACTION_LIKE);
            log.info("Пользователь {} лайкнул событие {}", userId, eventId);
        } catch (Exception e) {
            log.warn("Не удалось отправить лайк: {}", e.getMessage());
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private void validateEventDate(LocalDateTime eventDate, int hoursBeforeEvent) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hoursBeforeEvent))) {
            throw new ValidationException(
                    String.format("Дата события должна быть не ранее чем через %d часа от текущего момента",
                            hoursBeforeEvent));
        }
    }

    private void saveHit(HttpServletRequest request) {
        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .app(APP_NAME)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();
            statsClient.hit(hit);
            log.debug("Статистика сохранена: uri={}, ip={}", hit.getUri(), hit.getIp());
        } catch (Exception e) {
            log.warn("Ошибка при сохранении статистики: {}", e.getMessage());
        }
    }

    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }
        try {
            List<String> uris = events.stream()
                    .map(e -> "/events/" + e.getId())
                    .collect(Collectors.toList());
            LocalDateTime start = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now().minusYears(1));
            StatsRequestDto requestDto = new StatsRequestDto();
            requestDto.setStart(start);
            requestDto.setEnd(LocalDateTime.now());
            requestDto.setUris(uris);
            requestDto.setUnique(true);
            List<StatsResponseDto> stats = statsClient.getStats(requestDto);
            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> extractEventIdFromUri(s.getUri()),
                            StatsResponseDto::getHits,
                            (a, b) -> a));
        } catch (Exception e) {
            log.warn("Ошибка при получении статистики: {}", e.getMessage());
            return Map.of();
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-EWM-USER-ID");
        if (userIdHeader != null) {
            return Long.parseLong(userIdHeader);
        }
        return 0L;
    }
}
