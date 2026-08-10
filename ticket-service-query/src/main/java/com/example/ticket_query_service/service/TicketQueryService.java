package com.example.ticket_query_service.service;

import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.model.PriorityType;
import com.example.ticket_query_service.model.TicketStatus;
import com.example.ticket_query_service.repository.TicketQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketQueryService {

    private final TicketQueryRepository ticketQueryRepository;

    public TicketDto getTicketById(String id) {
        log.info("Fetching ticket details for id={}", id);
        return ticketQueryRepository.findDtoById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    public List<TicketDto> getAllTickets() {
        log.info("Fetching all tickets");
        return ticketQueryRepository.findAllProjectedBy();
    }

    public List<TicketDto> getTicketsByAssignee(String assignee) {
        log.info("Fetching tickets assigned to assignee={}", assignee);
        return ticketQueryRepository.findAllByAssignee(assignee);
    }

    public List<TicketDto> getTicketsByStatus(TicketStatus status) {
        log.info("Fetching tickets with status={}", status);
        return ticketQueryRepository.findAllByTicketStatus(status);
    }

    public List<TicketDto> getTicketsByPriority(PriorityType priority) {
        log.info("Fetching tickets with priority={}", priority);
        return ticketQueryRepository.findAllByPriorityType(priority);
    }
}