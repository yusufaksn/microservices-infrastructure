package com.example.ticket_query_service.api;

import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.model.PriorityType;
import com.example.ticket_query_service.model.TicketStatus;
import com.example.ticket_query_service.service.TicketQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketQueryController {

    private final TicketQueryService ticketQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable String id) {
        return ResponseEntity.ok(ticketQueryService.getTicketById(id));
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        return ResponseEntity.ok(ticketQueryService.getAllTickets());
    }

    @GetMapping("/assignee/{assignee}")
    public ResponseEntity<List<TicketDto>> getTicketsByAssignee(@PathVariable String assignee) {
        return ResponseEntity.ok(ticketQueryService.getTicketsByAssignee(assignee));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDto>> getTicketsByStatus(@PathVariable TicketStatus status) {
        return ResponseEntity.ok(ticketQueryService.getTicketsByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketDto>> getTicketsByPriority(@PathVariable PriorityType priority) {
        return ResponseEntity.ok(ticketQueryService.getTicketsByPriority(priority));
    }
}