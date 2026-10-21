package ru.practicum.client.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.StatsServiceProto;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class AnalyzerClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub stub;

    public List<StatsServiceProto.RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        StatsServiceProto.UserPredictionsRequestProto request = StatsServiceProto.UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<StatsServiceProto.RecommendedEventProto> iterator = stub.getRecommendationsForUser(request);
        return asStream(iterator).collect(Collectors.toList());
    }

    public List<StatsServiceProto.RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        StatsServiceProto.SimilarEventsRequestProto request = StatsServiceProto.SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        Iterator<StatsServiceProto.RecommendedEventProto> iterator = stub.getSimilarEvents(request);
        return asStream(iterator).collect(Collectors.toList());
    }

    public double getInteractionsCount(Long eventId) {
        StatsServiceProto.InteractionsCountRequestProto request = StatsServiceProto.InteractionsCountRequestProto.newBuilder()
                .addEventId(eventId)
                .build();
        Iterator<StatsServiceProto.RecommendedEventProto> iterator = stub.getInteractionsCount(request);
        return asStream(iterator).findFirst().map(StatsServiceProto.RecommendedEventProto::getScore).orElse(0.0);
    }

    private <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
