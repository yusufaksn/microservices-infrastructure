package com.example.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticket.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
}
