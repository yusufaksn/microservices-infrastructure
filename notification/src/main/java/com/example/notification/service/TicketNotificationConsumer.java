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

    @KafkaListener(topics = "postgres_ticket.public.outbox_events", groupId = "notification-group")
    public void consumeTicket(String message) {
        try {      
            JsonNode jsonNode = objectMapper.readTree(message).get("payload").get("after");
            if (jsonNode.isMissingNode() || jsonNode.isNull()) {
                return;
            }
            OutboxEventDto outboxEvent = objectMapper.treeToValue(jsonNode, OutboxEventDto.class);            
            String traceId = outboxEvent.getTraceId();
            String spanId = outboxEvent.getSpanId();

            TraceContext parentContext = traceContextBinder.bind(traceId, spanId);

            if (parentContext != null) {

                Span newSpan = tracer.newChild(parentContext)
                                     .name("notification-received")
                                     .start();

                try (Tracer.SpanInScope scope = tracer.withSpanInScope(newSpan)) {
                    TicketDto ticketDto = objectMapper.readValue(outboxEvent.getPayload(), TicketDto.class);
                    log.info("[NOTIFICATION] Processing notification under trace.");
                    log.info("---------------------------------------------------------------------------------------------------------------------");
                    log.info("Notification Details: {}", ticketDto);
                    log.info("---------------------------------------------------------------------------------------------------------------------");
                } catch (Exception e) {
                    newSpan.error(e); 
                    throw e;
                } finally {
                    newSpan.finish();
                }
            }

        } catch (Exception e) {
            log.error("An error occurred while processing the ticket message from Kafka: ", e);
        }
    }
}