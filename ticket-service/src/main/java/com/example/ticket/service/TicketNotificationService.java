package com.example.ticket.service;

import com.example.ticket.model.Ticket;

public interface TicketNotificationService {

    void sendToQueue(Ticket ticket);
}
