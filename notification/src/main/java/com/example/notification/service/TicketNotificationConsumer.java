package com.example.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.notification.component.TraceContextBinder;
import com.example.notification.dto.OutboxEventDto;
import com.example.notification.dto.TicketDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final TraceContextBinder traceContextBinder;
    private final Tracer tracer;
    private final IdempotencyService idempotencyService;

    @KafkaListener(topics = "postgres_ticket.public.outbox_events", groupId = "notification-group")
    public void consumeTicket(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode afterNode = rootNode.path("payload").path("after");

            if (afterNode.isMissingNode() || afterNode.isNull()) {
                return;
            }

            OutboxEventDto outboxEvent = objectMapper.treeToValue(afterNode, OutboxEventDto.class);

            // 2. Bind Trace Context (Performed first to maintain distributed trace chain)
            TraceContext parentContext = traceContextBinder.bind(outboxEvent.getTraceId(), outboxEvent.getSpanId());
            
            Span newSpan = (parentContext != null) 
                    ? tracer.newChild(parentContext).name("notification-received").start()
                    : tracer.nextSpan().name("notification-received").start();

            // 3. START SPAN SCOPE (MongoDB call must stay INSIDE this block)
            try (Tracer.SpanInScope scope = tracer.withSpanInScope(newSpan)) {
                
                // Idempotency check is inside the active span scope.
                // This ensures Tracer automatically propagates TraceID and SpanID via inheritance to IdempotencyService.
                if (!idempotencyService.processIfFirstTime(outboxEvent.getId())) {
                    return; // Already processed
                }

                TicketDto ticketDto = objectMapper.readValue(outboxEvent.getPayload(), TicketDto.class);
                log.info("[NOTIFICATION] Processing notification for Ticket ID: {}", ticketDto.getId());
                log.info("Notification Details: {}", ticketDto);

            } catch (Exception e) {
                newSpan.error(e);
                idempotencyService.undoProcessing(outboxEvent.getId());
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