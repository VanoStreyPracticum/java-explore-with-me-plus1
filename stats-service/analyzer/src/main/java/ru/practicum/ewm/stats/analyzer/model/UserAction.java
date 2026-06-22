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
    @EmbeddedId
    UserActionId id;

    Double weight;

    public Long getUserId() { return id.getUserId(); }
    public Long getEventId() { return id.getEventId(); }
}
