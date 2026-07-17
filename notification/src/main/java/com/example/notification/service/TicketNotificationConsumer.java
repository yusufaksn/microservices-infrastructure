package com.example.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.notification.dto.TicketDto;
import com.example.notification.dto.TicketKafkaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketNotificationConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "postgres_ticket.public.tickets", groupId = "notification-group")
    public void consumeTicket(String message) {
        try {
            TicketKafkaEvent event = objectMapper.readValue(message, TicketKafkaEvent.class);
            
            if (event.getPayload() == null) {
                return;
            }

            String operation = event.getPayload().getOp();
            TicketDto currentData = event.getPayload().getAfter(); // New ticket data

            // Trigger notification only if the operation is "c" (Create - New Record)
            if ("c".equals(operation) && currentData != null) {
                log.info("🔔 [NOTIFICATION] A new ticket has been assigned!");
                log.info("--------------------------------------------------");
                log.info("Ticket ID      : {}", currentData.getId());
                log.info("Assignee       : {}", currentData.getAssignee());
                log.info("Description    : {}", currentData.getDescription());
                log.info("Priority Type  : {}", currentData.getPriorityType());
                log.info("Ticket Status  : {}", currentData.getTicketStatus());
                log.info("--------------------------------------------------");
                
                // TODO: Trigger email/SMS or push notification service here
            }

        } catch (Exception e) {
            log.error("An error occurred while processing the ticket message from Kafka: ", e);
        }
    }
}