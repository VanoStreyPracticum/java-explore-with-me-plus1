package ru.practicum.ewm.stats.analyzer.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionId implements Serializable {
    private Long userId;
    private Long eventId;
}
