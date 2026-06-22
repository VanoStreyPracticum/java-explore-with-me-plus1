package ru.practicum.ewm.stats.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.model.UserAction;
import ru.practicum.ewm.stats.analyzer.repository.SimilarityRepository;
import ru.practicum.ewm.stats.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.StatsServiceProto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.StatsServiceProto.UserPredictionsRequestProto;
import ru.practicum.ewm.stats.proto.StatsServiceProto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.StatsServiceProto.InteractionsCountRequestProto;

import java.util.*;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class RecommendationsControllerImpl extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final SimilarityRepository similarityRepository;
    private final UserActionRepository userActionRepository;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        // Получаем действия пользователя
        List<UserAction> userActions = userActionRepository.findAllByUserId(userId);
        if (userActions.isEmpty()) {
            responseObserver.onCompleted();
            return;
        }

        // Получаем множество событий, с которыми пользователь взаимодействовал
        Set<Long> interactedEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        // Для каждого действия находим похожие события, с которыми пользователь ещё не взаимодействовал
        Map<Long, Double> predictedScores = new HashMap<>();
        for (UserAction action : userActions) {
            List<EventSimilarity> similarities = similarityRepository.findByEventId(action.getEventId());
            for (EventSimilarity sim : similarities) {
                long similarEvent = sim.getEventA().equals(action.getEventId()) ? sim.getEventB() : sim.getEventA();
                if (!interactedEventIds.contains(similarEvent)) {
                    // Предсказанная оценка = вес действия * сходство
                    double predicted = action.getWeight() * sim.getScore();
                    predictedScores.merge(similarEvent, predicted, Double::sum);
                }
            }
        }

        // Сортируем и отправляем top-N
        predictedScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .forEach(e -> {
                    responseObserver.onNext(RecommendedEventProto.newBuilder()
                            .setEventId(e.getKey())
                            .setScore(e.getValue())
                            .build());
                });
        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        long eventId = request.getEventId();
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        Set<Long> interactedEvents = userActionRepository.findAllByUserId(userId).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        List<EventSimilarity> similarities = similarityRepository.findByEventId(eventId);
        similarities.stream()
                .map(s -> {
                    long other = s.getEventA().equals(eventId) ? s.getEventB() : s.getEventA();
                    return Map.entry(other, s.getScore());
                })
                .filter(e -> !interactedEvents.contains(e.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .forEach(e -> {
                    responseObserver.onNext(RecommendedEventProto.newBuilder()
                            .setEventId(e.getKey())
                            .setScore(e.getValue())
                            .build());
                });
        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> eventIds = request.getEventIdList();
        for (Long eventId : eventIds) {
            double sum = userActionRepository.findAll().stream()
                    .filter(a -> a.getEventId().equals(eventId))
                    .mapToDouble(UserAction::getWeight)
                    .sum();
            responseObserver.onNext(RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(sum)
                    .build());
        }
        responseObserver.onCompleted();
    }
}
