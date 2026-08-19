package com.example.ticket_query_service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticket_query_service.dto.PagedResult;
import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.service.TicketQueryService;

import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<PagedResult<TicketDto>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ticketQueryService.getAll(page, size));
    }

}