package ru.practicum.ewm.stats.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.analyzer.repository.SimilarityRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimilarityListener {

    private final SimilarityRepository similarityRepository;

    @KafkaListener(topics = "stats.events-similarity.v1", groupId = "analyzer")
    @Transactional
    public void handle(EventSimilarityAvro avro) {
        similarityRepository.upsert(avro.getEventA(), avro.getEventB(), avro.getScore());
        log.debug("Updated similarity: ({},{})={}", avro.getEventA(), avro.getEventB(), avro.getScore());
    }
}
