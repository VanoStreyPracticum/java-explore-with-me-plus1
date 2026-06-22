package ru.practicum.ewm.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {
    @Id
    @Column(name = "user_id")
    Long userId;

    @Id
    @Column(name = "event_id")
    Long eventId;

    Double weight;
}
