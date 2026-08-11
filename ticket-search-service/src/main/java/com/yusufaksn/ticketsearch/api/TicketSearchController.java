package com.yusufaksn.ticketsearch.api;

import com.yusufaksn.ticketsearch.dto.TicketResponseDto;
import com.yusufaksn.ticketsearch.service.TicketSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketSearchController {

    private final TicketSearchService ticketSearchService;

    @GetMapping("/search")
    public ResponseEntity<Page<TicketResponseDto>> searchTickets(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ticketSearchService.searchTickets(query, page, size));
    }
}