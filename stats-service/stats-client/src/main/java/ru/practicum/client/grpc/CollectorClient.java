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

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub stub;

    public void sendUserAction(long userId, long eventId, StatsServiceProto.ActionTypeProto actionType) {
        try {
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(System.currentTimeMillis() / 1000)
                    .setNanos((int) ((System.currentTimeMillis() % 1000) * 1000000))
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
