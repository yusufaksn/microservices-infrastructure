package com.example.ticket_query_service.repository;

import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.model.Ticket;
import com.example.ticket_query_service.model.PriorityType;
import com.example.ticket_query_service.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketQueryRepository extends JpaRepository<Ticket, String> {

    Optional<TicketDto> findDtoById(String id);

    List<TicketDto> findAllProjectedBy();

    List<TicketDto> findAllByAssignee(String assignee);

    List<TicketDto> findAllByTicketStatus(TicketStatus ticketStatus);

    List<TicketDto> findAllByPriorityType(PriorityType priorityType);
}