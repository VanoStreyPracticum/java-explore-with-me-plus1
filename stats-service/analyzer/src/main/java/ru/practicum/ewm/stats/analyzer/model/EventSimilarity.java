package ru.practicum.ewm.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "event_similarities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @EmbeddedId
    EventSimilarityId id;

    Double score;
}
