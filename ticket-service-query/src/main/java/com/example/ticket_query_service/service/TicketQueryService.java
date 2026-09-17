package com.example.ticket_query_service.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket_query_service.dto.PagedResult;
import com.example.ticket_query_service.dto.TicketDto;
import com.example.ticket_query_service.dto.TicketMapper;
import com.example.ticket_query_service.repository.TicketQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor 
public class TicketQueryService {

    private final TicketQueryRepository ticketQueryRepository;
    private final TicketMapper ticketMapper;
    private final RedisService redisService;

    public PagedResult<TicketDto> getAll(int page, int size) {


        List<TicketDto> cachedTickets = redisService.getTicketsByPageFromRedis(page, size);

        if (cachedTickets != null && !cachedTickets.isEmpty()) {
            Page<TicketDto> redisPage = new PageImpl<>(
                    cachedTickets,
                    PageRequest.of(page, size),
                    cachedTickets.size() 
            );
            return PagedResult.from(redisPage);
        }

  
        Page<TicketDto> result = ticketQueryRepository
                .findAll(PageRequest.of(page, size))
                .map(ticketMapper::toDto);

    
        if (!result.isEmpty()) {
            redisService.cacheTicketsAsync(page, size, result);
        }

        return PagedResult.from(result);
    }


    

       

    @Cacheable(value = "tickets", key = "#id")
        public TicketDto getTicketById(String id) {
            return ticketQueryRepository.findDtoById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Ticket not found with id: " + id));
    }


}