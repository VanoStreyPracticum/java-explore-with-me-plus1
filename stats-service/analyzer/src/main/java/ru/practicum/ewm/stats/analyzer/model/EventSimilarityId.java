package ru.practicum.ewm.stats.analyzer.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSimilarityId implements Serializable {
    private Long eventA;
    private Long eventB;
}
