package com.example.notification.service;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.support.Acknowledgment;

import com.example.notification.component.TraceContextBinder;

@SpringBootTest
class TicketNotificationConsumerIntegrationTest {

    @Autowired
    private TicketNotificationConsumer consumer;

    @MockBean
    private IdempotencyService idempotencyService;

    @SpyBean
    private TraceContextBinder traceContextBinder;

    @AfterEach
    void tearDown() {
        // Clear resources after each test run if necessary
    }

    @Test
    @DisplayName("Should process message when event ID is received for the first time")
    void shouldProcessMessageWhenReceivedForFirstTime() {
        // Arrange
        String ticketId = UUID.randomUUID().toString();
        String jsonMessage = createSampleKafkaMessage(ticketId, "OPEN");
        Acknowledgment ack = mock(Acknowledgment.class);

        when(idempotencyService.processIfFirstTime(ticketId)).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> consumer.consumeTicket(jsonMessage, ack))
                .doesNotThrowAnyException();

        // Verify Idempotency check was performed once
        verify(idempotencyService, times(1)).processIfFirstTime(ticketId);
        verify(idempotencyService, never()).undoProcessing(any());

        // Verify offset was committed after successful processing
        verify(ack, times(1)).acknowledge();
    }

    @Test
    @DisplayName("Should skip processing (Idempotent) when same event ID is received twice")
    void shouldSkipProcessingWhenDuplicateMessageIsReceived() {
        // Arrange
        String ticketId = UUID.randomUUID().toString();
        String jsonMessage = createSampleKafkaMessage(ticketId, "OPEN");
        Acknowledgment firstAck = mock(Acknowledgment.class);
        Acknowledgment secondAck = mock(Acknowledgment.class);

        // First call allows, second call blocks (duplicate)
        when(idempotencyService.processIfFirstTime(ticketId))
                .thenReturn(true) // 1st attempt: First time
                .thenReturn(false); // 2nd attempt: Already processed

        // Act 1: Process first message
        consumer.consumeTicket(jsonMessage, firstAck);

        // Act 2: Process duplicate message
        consumer.consumeTicket(jsonMessage, secondAck);

        // Assert
        // Idempotency check called twice in total
        verify(idempotencyService, times(2)).processIfFirstTime(ticketId);

        // Ensure failure rollback was NEVER called since duplicate was ignored
        // gracefully
        verify(idempotencyService, never()).undoProcessing(ticketId);

        // Both calls should still acknowledge — first because it processed,
        // second because it was gracefully skipped
        verify(firstAck, times(1)).acknowledge();
        verify(secondAck, times(1)).acknowledge();
    }

    private String createSampleKafkaMessage(String ticketId, String status) {
        return """
                {
                  "payload_id": "%s",
                  "payload_description": "Test Ticket",
                  "payload_ticketStatus": "%s",
                  "traceId": "463ac35c9f6413ad",
                  "spanId": "463ac35c9f6413ad"
                }
                """.formatted(ticketId, status);
    }
}