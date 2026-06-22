package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.EventState;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<EventFullDto> searchEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> searchPublicEvents(String text, List<Long> categories, Boolean paid,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                           String sort, int from, int size, HttpServletRequest request);

    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

    EventFullDto getEventById(Long eventId);

    List<EventShortDto> getRecommendations(Long userId, int maxResults);

    void likeEvent(Long userId, Long eventId);
}
