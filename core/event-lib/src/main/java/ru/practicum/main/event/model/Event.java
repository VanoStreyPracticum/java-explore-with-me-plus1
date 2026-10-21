package ru.practicum.main.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 2000)
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Builder.Default
    @Column(name = "confirmed_requests")
    Long confirmedRequests = 0L;

    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    @Column(length = 7000)
    String description;

    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @Embedded
    Location location;

    @Column(nullable = false)
    @Builder.Default
    Boolean paid = false;

    @Column(name = "participant_limit")
    @Builder.Default
    Integer participantLimit = 0;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    @Builder.Default
    Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    EventState state = EventState.PENDING;

    @Column(nullable = false, length = 120)
    String title;

    @Builder.Default
    Long views = 0L;

    @Builder.Default
    Double rating = 0.0;
}
