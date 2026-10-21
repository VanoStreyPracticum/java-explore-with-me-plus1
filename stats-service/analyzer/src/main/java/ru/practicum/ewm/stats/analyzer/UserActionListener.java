package ru.practicum.ewm.stats.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.analyzer.repository.UserActionRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {
    private static final double VIEW_WEIGHT = 0.4;
    private static final double REGISTER_WEIGHT = 0.8;
    private static final double LIKE_WEIGHT = 1.0;

    private final UserActionRepository userActionRepository;

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "analyzer")
    @Transactional
    public void handle(UserActionAvro avro) {
        double weight = switch (avro.getActionType()) {
            case VIEW -> VIEW_WEIGHT;
            case REGISTER -> REGISTER_WEIGHT;
            case LIKE -> LIKE_WEIGHT;
        };
        userActionRepository.upsert(avro.getUserId(), avro.getEventId(), weight);
        log.debug("Updated user action: userId={}, eventId={}, weight={}", avro.getUserId(), avro.getEventId(), weight);
    }
}
