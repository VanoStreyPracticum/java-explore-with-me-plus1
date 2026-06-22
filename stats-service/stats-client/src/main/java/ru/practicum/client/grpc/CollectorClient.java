package ru.practicum.client.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.StatsServiceProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;

@Component
@Slf4j
public class CollectorClient {
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long NANOS_PER_MILLI = 1_000_000L;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub stub;

    public void sendUserAction(long userId, long eventId, StatsServiceProto.ActionTypeProto actionType) {
        try {
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(System.currentTimeMillis() / MILLIS_PER_SECOND)
                    .setNanos((int) ((System.currentTimeMillis() % MILLIS_PER_SECOND) * NANOS_PER_MILLI))
                    .build();
            StatsServiceProto.UserActionProto request = StatsServiceProto.UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionType)
                    .setTimestamp(timestamp)
                    .build();
            Empty response = stub.collectUserAction(request);
            log.debug("Sent user action: userId={}, eventId={}, type={}", userId, eventId, actionType);
        } catch (Exception e) {
            log.warn("Failed to send user action: {}", e.getMessage());
        }
    }
}
