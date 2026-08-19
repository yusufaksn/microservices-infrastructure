package com.example.ticket_query_service.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket_query_service.dto.PagedResult;
import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.dto.TicketMapper;
import com.example.ticket_query_service.repository.TicketQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketQueryService {

    private final TicketQueryRepository ticketQueryRepository;
    private final TicketMapper ticketMapper;

    @Cacheable(value = "tickets", key = "'page:' + #page + ':size:' + #size")
    public PagedResult<TicketDto> getAll(int page, int size) {
        Page<TicketDto> result = ticketQueryRepository
                .findAll(PageRequest.of(page, size))
                .map(ticketMapper::toDto);
        return PagedResult.from(result);
    }

    @Cacheable(value = "tickets", key = "'id:' + #id")
    public TicketDto getTicketById(String id) {
        return ticketQueryRepository.findDtoById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }


}