package com.example.ticket_query_service.dto;


import org.springframework.stereotype.Component;

import com.example.ticket_query_service.model.Ticket;

@Component
public class TicketMapper {

    public TicketDto toDto(Ticket ticket) {
        return new TicketDto(
                ticket.getId(),
                ticket.getDescription(),
                ticket.getNotes(),
                ticket.getAssignee(),
                ticket.getTicketDate(),
                ticket.getPriorityType(),
                ticket.getTicketStatus()
        );
    }
}