package dev.lydtech.tracking.service;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.dispatch.message.TrackingStatusUpdated;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TrackingServiceTest {

    private KafkaTemplate kafkaTemplateMock;
    private TrackingService service;

    @BeforeEach
    void setUp() {
        kafkaTemplateMock = mock(KafkaTemplate.class);
        service = new TrackingService(kafkaTemplateMock);
    }

    @Test
    void process_Sucess() throws Exception {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));

        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        service.processDispatchPreparing(testEvent);

        verify(kafkaTemplateMock, times(1)).send(eq("tracking.status"), any(TrackingStatusUpdated.class));

    }

    @Test
    public void process_DispatchPreparingProducerThrowsException() {
        doThrow(new RuntimeException("dispatch preparing producer failure")).when(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdated.class));

        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        Exception exception = assertThrows(RuntimeException.class, () -> service.processDispatchPreparing(testEvent));

        verify(kafkaTemplateMock, times(1)).send(eq("tracking.status"), any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("dispatch preparing producer failure"));
    }

    @Test
    void process_DispatchedSuccess() throws Exception {
        when(kafkaTemplateMock.send(anyString(), any(TrackingStatusUpdated.class))).thenReturn(mock(CompletableFuture.class));

        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID(), LocalDate.now().toString());
        service.processDispatched(testEvent);

        verify(kafkaTemplateMock, times(1)).send(eq("tracking.status"), any(TrackingStatusUpdated.class));
    }

    @Test
    void process_DispatchedThrowsException() throws Exception {
        doThrow(new RuntimeException("dispatched producer failure"))
                .when(kafkaTemplateMock).send(eq("tracking.status"), any(TrackingStatusUpdated.class));

        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID(), LocalDate.now().toString());

        Exception exception = assertThrows(RuntimeException.class, () -> service.processDispatched(testEvent));

        verify(kafkaTemplateMock, times(1)).send(eq("tracking.status"), any(TrackingStatusUpdated.class));
        assertThat(exception.getMessage(), equalTo("dispatched producer failure"));
    }
}