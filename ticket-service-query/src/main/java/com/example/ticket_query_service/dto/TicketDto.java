package com.example.ticket_query_service.dto;

import com.example.ticket_query_service.model.PriorityType;
import com.example.ticket_query_service.model.TicketStatus;

import java.time.LocalDateTime;

public record TicketDto(
    String id,
    String description,
    String notes,
    String assignee,
    LocalDateTime ticketDate,
    PriorityType priorityType,
    TicketStatus ticketStatus
) {}