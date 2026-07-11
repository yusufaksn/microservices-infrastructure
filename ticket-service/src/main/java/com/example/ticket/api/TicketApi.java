package com.example.ticket.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticket.dto.TicketDto;
import com.example.ticket.service.TicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/ticket")
@RestController
@RequiredArgsConstructor
public class TicketApi {

    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        return ResponseEntity.ok(ticketService.save(ticketDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable String id, @Valid @RequestBody TicketDto ticketDto) {
        return ResponseEntity.ok(ticketService.update(id, ticketDto));
    }

}
