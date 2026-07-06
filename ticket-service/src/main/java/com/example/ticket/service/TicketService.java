package com.example.ticket.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ticket.dto.TicketDto;

public interface TicketService {

    TicketDto save(TicketDto ticketDto);

    TicketDto update(String id, TicketDto ticketDto);

    TicketDto getById(String ticketId);

    Page<TicketDto> getPagination(Pageable pageable);
}
