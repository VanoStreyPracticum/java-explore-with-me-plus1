package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.grpc.CollectorClient;
import ru.practicum.ewm.stats.proto.StatsServiceProto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.status.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final CollectorClient collectorClient;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Получение заявок пользователя userId={}", userId);
        if (userId <= 0) {
            throw new ValidationException("userId must be greater than 0");
        }
        validateUserExists(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Создание заявки: userId={}, eventId={}", userId, eventId);
        if (userId <= 0 || eventId <= 0) {
            throw new ValidationException("Invalid userId or eventId");
        }
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может подать заявку на участие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Заявка на участие уже существует");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        ParticipationRequest savedRequest = requestRepository.save(request);
        collectorClient.sendUserAction(userId, eventId, StatsServiceProto.ActionTypeProto.ACTION_REGISTER);
        log.info("Создана заявка: id={}", savedRequest.getId());
        return requestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена заявки: userId={}, requestId={}", userId, requestId);
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: id=" + requestId));
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Заявка не принадлежит пользователю");
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);
        log.info("Заявка отменена: id={}", updatedRequest.getId());
        return requestMapper.toDto(updatedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        log.info("Получение заявок на событие: userId={}, eventId={}", userId, eventId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или не принадлежит пользователю"));
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Изменение статуса заявок: userId={}, eventId={}", userId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Только инициатор события может изменять статус заявок");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());
        for (ParticipationRequest request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Заявка не принадлежит данному событию");
            }
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            for (ParticipationRequest request : requests) {
                request.setStatus(RequestStatus.CONFIRMED);
            }
            requestRepository.saveAll(requests);
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(requests.stream().map(requestMapper::toDto).collect(Collectors.toList()))
                    .rejectedRequests(new ArrayList<>())
                    .build();
        }
        long confirmedCount = event.getConfirmedRequests();
        long limit = event.getParticipantLimit();
        if (confirmedCount >= limit) {
            throw new ConflictException("Достигнут лимит участников");
        }
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Можно изменить статус только у заявок в ожидании");
            }
            if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (confirmedCount < limit) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedCount++;
                    confirmed.add(requestMapper.toDto(request));
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(requestMapper.toDto(request));
                }
            } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(requestMapper.toDto(request));
            }
        }
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        log.info("Обновлены статусы заявок: подтверждено={}, отклонено={}", confirmed.size(), rejected.size());
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }
}
