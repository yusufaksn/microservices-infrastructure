package com.example.ticket.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.example.ticket.dto.TicketNotification;
import com.example.ticket.model.Ticket;
import com.example.ticket.service.TicketNotificationService;

@Service

public class TicketNotificationImpl implements TicketNotificationService {

    private final RabbitTemplate rabbitTemplate;

    public TicketNotificationImpl(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToQueue(Ticket ticket){
        TicketNotification ticketNotification =new TicketNotification();
        ticketNotification.setAccountId(ticket.getAssignee());
        ticketNotification.setTicketId(ticket.getId());
        ticketNotification.setTicketDescription(ticket.getDescription());
        rabbitTemplate.setObservationEnabled(true);
        rabbitTemplate.convertAndSend("ticket-queue", ticketNotification);
    }
    
}
