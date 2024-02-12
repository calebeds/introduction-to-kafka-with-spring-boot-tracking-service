package dev.lydtech.tracking.handler;

import dev.lydtech.dispatch.message.DispatchCompleted;
import dev.lydtech.dispatch.message.DispatchPreparing;
import dev.lydtech.tracking.service.TrackingService;
import dev.lydtech.tracking.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

class DispatchTrackingHandlerTest {

    private TrackingService trackingServiceMock;
    private DispatchTrackingHandler handler;

    @BeforeEach
    void setUp() {
        trackingServiceMock = mock(TrackingService.class);
        handler = new DispatchTrackingHandler(trackingServiceMock);
    }

    @Test
    void listen_DispatchPreparing() throws Exception {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        handler.listen(testEvent);
        verify(trackingServiceMock, times(1)).processDispatchPreparing(testEvent);
    }

    @Test
    void listen_DispatchPreparingThrowsException() throws Exception {
        DispatchPreparing testEvent = TestEventData.buildDispatchPreparingEvent(UUID.randomUUID());
        doThrow(new RuntimeException("Service failure")).when(trackingServiceMock).processDispatchPreparing(testEvent);

        handler.listen(testEvent);

        verify(trackingServiceMock, times(1)).processDispatchPreparing(testEvent);
    }

    @Test
    void listen_DispatchCompleted() throws Exception {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID(), LocalDate.now().toString());

        handler.listen(testEvent);

        verify(trackingServiceMock, times(1)).processDispatched(testEvent);
    }

    @Test
    void listen_DispatchCompletedThrowsException() throws Exception {
        DispatchCompleted testEvent = TestEventData.buildDispatchCompletedEvent(UUID.randomUUID(), LocalDate.now().toString());
        doThrow(new RuntimeException("Service failure"))
                .when(trackingServiceMock).processDispatched(testEvent);

        handler.listen(testEvent);

        verify(trackingServiceMock, times(1)).processDispatched(testEvent);
    }
}