package ru.practicum.ewm.stats.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionHandler {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    // Матрица весов: eventId -> (userId -> max weight)
    private final Map<Long, Map<Long, Double>> weightsMatrix = new HashMap<>();
    // Суммы минимальных весов для пар: eventId1 -> (eventId2 -> S_min)
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();
    // Общие суммы весов по мероприятиям: eventId -> S
    private final Map<Long, Double> totalSums = new HashMap<>();

    @KafkaListener(topics = "stats.user-actions.v1", groupId = "aggregator")
    public void handleUserAction(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        double weight = mapActionToWeight(action.getActionType().toString());

        // Обновляем матрицу весов
        Map<Long, Double> userWeights = weightsMatrix.computeIfAbsent(eventId, k -> new HashMap<>());
        Double oldWeight = userWeights.getOrDefault(userId, 0.0);
        if (weight > oldWeight) {
            userWeights.put(userId, weight);
            // Обновляем частные суммы
            updateSums(eventId, userId, oldWeight, weight);
        }
    }

    private double mapActionToWeight(String actionType) {
        return switch (actionType) {
            case "VIEW" -> 0.4;
            case "REGISTER" -> 0.8;
            case "LIKE" -> 1.0;
            default -> 0.0;
        };
    }

    private void updateSums(long eventA, long userId, double oldWeight, double newWeight) {
        // Обновляем общую сумму весов для мероприятия A
        totalSums.put(eventA, totalSums.getOrDefault(eventA, 0.0) - oldWeight + newWeight);

        // Для каждого другого мероприятия B, с которым взаимодействовал пользователь, обновляем S_min(A,B)
        for (Map.Entry<Long, Map<Long, Double>> entry : weightsMatrix.entrySet()) {
            long eventB = entry.getKey();
            if (eventB == eventA) continue;

            Map<Long, Double> userWeightsB = entry.getValue();
            Double weightB = userWeightsB.getOrDefault(userId, 0.0);
            if (weightB == 0.0) continue; // пользователь не взаимодействовал с B

            double oldMin = Math.min(oldWeight, weightB);
            double newMin = Math.min(newWeight, weightB);
            double delta = newMin - oldMin;

            if (delta != 0.0) {
                // Обновляем S_min(A,B) (упорядочиваем ключи)
                long first = Math.min(eventA, eventB);
                long second = Math.max(eventA, eventB);
                Map<Long, Double> inner = minWeightsSums.computeIfAbsent(first, k -> new HashMap<>());
                double oldSum = inner.getOrDefault(second, 0.0);
                inner.put(second, oldSum + delta);

                // Вычисляем новое сходство и отправляем в Kafka
                double sA = totalSums.getOrDefault(eventA, 0.0);
                double sB = totalSums.getOrDefault(eventB, 0.0);
                double sMin = oldSum + delta;
                double similarity = sMin / (Math.sqrt(sA) * Math.sqrt(sB));

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
