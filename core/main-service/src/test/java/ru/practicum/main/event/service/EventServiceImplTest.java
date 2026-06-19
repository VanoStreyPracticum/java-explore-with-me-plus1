package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.client.StatsClient;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.moderation.repository.ModerationHistoryRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private StatsClient statsClient;
    @Mock
    private ModerationHistoryRepository moderationHistoryRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private User initiator;
    private Category category;
    private Event event;

    @BeforeEach
    void setUp() {
        initiator = User.builder().id(1L).name("Test user").email("test@test.com").build();
        category = Category.builder().id(1L).name("Concerts").build();
        event = Event.builder()
                .id(1L)
                .title("test")
                .annotation("test annotation .....................")
                .description("test description .....................")
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(ru.practicum.main.event.model.Location.builder().lat(55.75f).lon(37.62f).build())
                .initiator(initiator)
                .category(category)
                .state(EventState.PENDING)
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .createdOn(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getUserEvents")
    class GetUserEventsTests {
        @Test
        void getUserEvents_ShouldReturnEvents() {
            when(userRepository.existsById(1L)).thenReturn(true);
            Page<Event> page = new PageImpl<>(List.of(event));
            when(eventRepository.findAllByInitiatorId(eq(1L), any())).thenReturn(page);
            when(eventMapper.toEventShortDtoList(any())).thenReturn(Collections.singletonList(new EventShortDto()));
            List<EventShortDto> result = eventService.getUserEvents(1L, 0, 10);
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        void getUserEvents_UserNotFound_ShouldThrow() {
            when(userRepository.existsById(1L)).thenReturn(false);
            assertThrows(NotFoundException.class, () -> eventService.getUserEvents(1L, 0, 10));
        }
    }

    @Nested
    @DisplayName("createEvent")
    class CreateEventTests {
        @Test
        void createEvent_ShouldSave() {
            NewEventDto dto = NewEventDto.builder()
                    .title("title")
                    .annotation("annotation is long enough .................")
                    .description("description is long enough .................")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(1))
                    .location(LocationDto.builder().lat(55.0f).lon(37.0f).build())
                    .build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(eventMapper.toEvent(dto)).thenReturn(new Event());
            when(eventMapper.toLocation(any())).thenReturn(ru.practicum.main.event.model.Location.builder().lat(55.0f).lon(37.0f).build());
            when(eventRepository.save(any())).thenReturn(event);
            when(eventMapper.toEventFullDto(any())).thenReturn(new EventFullDto());
            EventFullDto result = eventService.createEvent(1L, dto);
            assertNotNull(result);
            verify(eventRepository).save(any());
        }

        @Test
        void createEvent_EventDateTooSoon_ShouldThrow() {
            NewEventDto dto = NewEventDto.builder()
                    .title("title")
                    .annotation("annotation is long enough .................")
                    .description("description is long enough .................")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusHours(1))
                    .location(LocationDto.builder().lat(55.0f).lon(37.0f).build())
                    .build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
            assertThrows(ValidationException.class, () -> eventService.createEvent(1L, dto));
        }

        @Test
        void createEvent_UserNotFound_ShouldThrow() {
            NewEventDto dto = NewEventDto.builder().build();
            when(userRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.createEvent(1L, dto));
        }

        @Test
        void createEvent_CategoryNotFound_ShouldThrow() {
            NewEventDto dto = NewEventDto.builder()
                    .title("title")
                    .annotation("annotation is long enough .................")
                    .description("description is long enough .................")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(1))
                    .location(LocationDto.builder().lat(55.0f).lon(37.0f).build())
                    .build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.createEvent(1L, dto));
        }
    }

    @Nested
    @DisplayName("getUserEventById")
    class GetUserEventByIdTests {
        @Test
        void getUserEventById_ShouldReturn() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());
            assertNotNull(eventService.getUserEventById(1L, 1L));
        }

        @Test
        void getUserEventById_NotFound_ShouldThrow() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.getUserEventById(1L, 1L));
        }
    }

    @Nested
    @DisplayName("updateEventByUser")
    class UpdateEventByUserTests {
        @Test
        void updateEventByUser_SendToReview_ShouldUpdate() {
            UpdateEventUserRequest request = UpdateEventUserRequest.builder()
                    .stateAction(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW)
                    .build();
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
            when(eventRepository.save(any())).thenReturn(event);
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());
            EventFullDto result = eventService.updateEventByUser(1L, 1L, request);
            assertNotNull(result);
        }

        @Test
        void updateEventByUser_PublishedEvent_ShouldThrow() {
            event.setState(EventState.PUBLISHED);
            UpdateEventUserRequest request = UpdateEventUserRequest.builder().build();
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
            assertThrows(ConflictException.class, () -> eventService.updateEventByUser(1L, 1L, request));
        }

        @Test
        void updateEventByUser_EventNotFound_ShouldThrow() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.updateEventByUser(1L, 1L, UpdateEventUserRequest.builder().build()));
        }
    }

    @Nested
    @DisplayName("searchEventsForAdmin")
    class SearchEventsForAdminTests {
        @Test
        void searchEventsForAdmin_ShouldReturn() {
            Page<Event> page = new PageImpl<>(List.of(event));
            when(eventRepository.findEventsForAdmin(any(), any(), any(), any(), any(), any())).thenReturn(page);
            when(eventMapper.toEventFullDtoList(any())).thenReturn(Collections.singletonList(new EventFullDto()));
            List<EventFullDto> result = eventService.searchEventsForAdmin(null, null, null, null, null, 0, 10);
            assertFalse(result.isEmpty());
        }

        @Test
        void searchEventsForAdmin_Empty_ShouldReturnEmpty() {
            Page<Event> page = new PageImpl<>(Collections.emptyList());
            when(eventRepository.findEventsForAdmin(any(), any(), any(), any(), any(), any())).thenReturn(page);
            when(eventMapper.toEventFullDtoList(any())).thenReturn(Collections.emptyList());
            List<EventFullDto> result = eventService.searchEventsForAdmin(null, null, null, null, null, 0, 10);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateEventByAdmin")
    class UpdateEventByAdminTests {
        @Test
        void updateEventByAdmin_PublishEvent_Success() {
            event.setState(EventState.PENDING);
            event.setEventDate(LocalDateTime.now().plusDays(1));
            UpdateEventAdminRequest request = UpdateEventAdminRequest.builder()
                    .stateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)
                    .moderationNote("Approved")
                    .build();
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventRepository.save(any())).thenReturn(event);
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());

            EventFullDto result = eventService.updateEventByAdmin(1L, request);
            assertNotNull(result);
            verify(moderationHistoryRepository).save(any());
        }

        @Test
        void updateEventByAdmin_RejectEvent_Success() {
            event.setState(EventState.PENDING);
            UpdateEventAdminRequest request = UpdateEventAdminRequest.builder()
                    .stateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT)
                    .moderationNote("Spam")
                    .build();
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventRepository.save(any())).thenReturn(event);
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());

            EventFullDto result = eventService.updateEventByAdmin(1L, request);
            assertNotNull(result);
            verify(moderationHistoryRepository).save(any());
        }

        @Test
        void updateEventByAdmin_PublishEvent_WrongState_ShouldThrow() {
            event.setState(EventState.PUBLISHED);
            UpdateEventAdminRequest request = UpdateEventAdminRequest.builder()
                    .stateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)
                    .build();
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            assertThrows(ConflictException.class, () -> eventService.updateEventByAdmin(1L, request));
        }

        @Test
        void updateEventByAdmin_RejectEvent_PublishedEvent_ShouldThrow() {
            event.setState(EventState.PUBLISHED);
            UpdateEventAdminRequest request = UpdateEventAdminRequest.builder()
                    .stateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT)
                    .build();
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            assertThrows(ConflictException.class, () -> eventService.updateEventByAdmin(1L, request));
        }

        @Test
        void updateEventByAdmin_EventNotFound_ShouldThrow() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.updateEventByAdmin(1L, UpdateEventAdminRequest.builder().build()));
        }
    }

    @Nested
    @DisplayName("getPublishedEventById")
    class GetPublishedEventByIdTests {
        @Test
        void getPublishedEventById_ShouldReturn() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getRequestURI()).thenReturn("/events/1");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(eventRepository.findByIdAndState(1L, EventState.PUBLISHED)).thenReturn(Optional.of(event));
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());
            assertNotNull(eventService.getPublishedEventById(1L, request));
        }

        @Test
        void getPublishedEventById_NotFound_ShouldThrow() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(eventRepository.findByIdAndState(1L, EventState.PUBLISHED)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.getPublishedEventById(1L, request));
        }
    }

    @Nested
    @DisplayName("getEventById")
    class GetEventByIdTests {
        @Test
        void getEventById_ShouldReturn() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventMapper.toEventFullDto(event)).thenReturn(new EventFullDto());
            assertNotNull(eventService.getEventById(1L));
        }

        @Test
        void getEventById_NotFound_ShouldThrow() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> eventService.getEventById(1L));
        }
    }
}
