package ru.practicum.ewm.stats.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionHandler {

    private static final BigDecimal VIEW_WEIGHT = new BigDecimal("0.4");
    private static final BigDecimal REGISTER_WEIGHT = new BigDecimal("0.8");
    private static final BigDecimal LIKE_WEIGHT = new BigDecimal("1.0");

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    private final Map<Long, Map<Long, BigDecimal>> weightsMatrix = new HashMap<>();
    private final Map<Long, Map<Long, BigDecimal>> minWeightsSums = new HashMap<>();
    private final Map<Long, BigDecimal> totalSums = new HashMap<>();

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator")
    public void handleUserAction(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        BigDecimal weight = mapActionToWeight(action.getActionType().toString());

        Map<Long, BigDecimal> userWeights = weightsMatrix.computeIfAbsent(eventId, k -> new HashMap<>());
        BigDecimal oldWeight = userWeights.getOrDefault(userId, BigDecimal.ZERO);
        if (weight.compareTo(oldWeight) > 0) {
            userWeights.put(userId, weight);
            updateSums(eventId, userId, oldWeight, weight);
        }
    }

    private BigDecimal mapActionToWeight(String actionType) {
        return switch (actionType) {
            case "VIEW" -> VIEW_WEIGHT;
            case "REGISTER" -> REGISTER_WEIGHT;
            case "LIKE" -> LIKE_WEIGHT;
            default -> BigDecimal.ZERO;
        };
    }

    private void updateSums(long eventA, long userId, BigDecimal oldWeight, BigDecimal newWeight) {
        totalSums.put(eventA, totalSums.getOrDefault(eventA, BigDecimal.ZERO).subtract(oldWeight).add(newWeight));

        for (Map.Entry<Long, Map<Long, BigDecimal>> entry : weightsMatrix.entrySet()) {
            long eventB = entry.getKey();
            if (eventB == eventA) continue;

            Map<Long, BigDecimal> userWeightsB = entry.getValue();
            BigDecimal weightB = userWeightsB.getOrDefault(userId, BigDecimal.ZERO);
            if (weightB.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal oldMin = oldWeight.min(weightB);
            BigDecimal newMin = newWeight.min(weightB);
            BigDecimal delta = newMin.subtract(oldMin);

            if (delta.compareTo(BigDecimal.ZERO) != 0) {
                long first = Math.min(eventA, eventB);
                long second = Math.max(eventA, eventB);
                Map<Long, BigDecimal> inner = minWeightsSums.computeIfAbsent(first, k -> new HashMap<>());
                BigDecimal oldSum = inner.getOrDefault(second, BigDecimal.ZERO);
                BigDecimal newSum = oldSum.add(delta);
                inner.put(second, newSum);

                BigDecimal sA = totalSums.getOrDefault(eventA, BigDecimal.ZERO);
                BigDecimal sB = totalSums.getOrDefault(eventB, BigDecimal.ZERO);
                double similarity = newSum.doubleValue() / (Math.sqrt(sA.doubleValue()) * Math.sqrt(sB.doubleValue()));

                EventSimilarityAvro similarityAvro = EventSimilarityAvro.newBuilder()
                        .setEventA(first)
                        .setEventB(second)
                        .setScore(similarity)
                        .setTimestamp(Instant.now())
                        .build();
                kafkaTemplate.send("stats.events-similarity.v1", similarityAvro);
                log.info("Updated similarity for pair ({}, {}): {}", first, second, similarity);
            }
        }
    }
}
