package com.example.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.notification.component.TraceContextBinder;
import com.example.notification.dto.TicketDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final TraceContextBinder traceContextBinder;
    private final Tracer tracer;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "outbox.event.Ticket", groupId = "notification-group")
    public void consumeTicket(String message, Acknowledgment ack) {
        try {
            log.info("[NOTIFICATION] Inbound message: {}", message);

            JsonNode rootNode = objectMapper.readTree(message);
            String traceId = rootNode.path("traceId").asText(null);
            String spanId = rootNode.path("spanId").asText(null);
            TicketDto ticketDto = objectMapper.treeToValue(rootNode, TicketDto.class);
            String ticketId = rootNode.has("id") ? rootNode.path("id").asText() : rootNode.path("payload_id").asText();

            TraceContext parentContext = traceContextBinder.bind(traceId, spanId);
            Span newSpan = (parentContext != null)
                    ? tracer.newChild(parentContext).name("notification-received").start()
                    : tracer.nextSpan().name("notification-received").start();

            try (Tracer.SpanInScope scope = tracer.withSpanInScope(newSpan)) {

                if (!idempotencyService.processIfFirstTime(ticketId)) {
                    log.info("[NOTIFICATION] Ticket ID {} already processed, skipping.", ticketId);
                    ack.acknowledge();
                    return;
                }

                log.info("[NOTIFICATION] Processing notification for Ticket ID: {}", ticketId);
                log.info("Notification Details: {}", ticketDto);

                ack.acknowledge();

            } catch (Exception e) {
                newSpan.error(e);
                idempotencyService.undoProcessing(ticketId);
                throw e;
            } finally {
                newSpan.finish();
            }

        } catch (Exception e) {
            log.error("Error occurred while processing ticket message from Kafka: ", e);
            throw new RuntimeException(e);
        }
    }
}