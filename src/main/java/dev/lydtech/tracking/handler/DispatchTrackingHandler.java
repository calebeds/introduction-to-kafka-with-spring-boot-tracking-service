package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = "dispatch.tracking",
        groupId = "tracking.dispatch.tracking",
        containerFactory = "kafkaListenerContainerFactory"
)
public class DispatchTrackingHandler {
    private final TrackingService trackingService;


    @KafkaHandler
    public void listen(DispatchPreparing dispatchPreparing) throws Exception {
        try {
            trackingService.processDispatchPreparing(dispatchPreparing);
        } catch (Exception e) {
            log.error("Process failure", e);
        }
    }

    @KafkaHandler
    public void listen(DispatchCompleted dispatchCompleted) throws Exception {
        try {
            trackingService.processDispatched(dispatchCompleted);
        } catch (Exception e) {
            log.error("DispatchCompleted processing failure ", e);
        }
    }
}
