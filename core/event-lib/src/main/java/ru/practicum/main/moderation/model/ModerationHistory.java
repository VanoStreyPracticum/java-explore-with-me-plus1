package ru.practicum.main.moderation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderation_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "moderator_id")
    private Long moderatorId;

    @Column(nullable = false)
    private String action;

    @Column(name = "moderation_note")
    private String moderationNote;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
