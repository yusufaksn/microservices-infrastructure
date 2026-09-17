package com.example.ticket_query_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.ticket_query_service.dto.TicketDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisScript<String> cacheTicketsPageScript;
    private final RedisScript<List> getTicketsPageScript;

    private static final long TTL_SECONDS = 300; 
    private static final String TICKET_PREFIX = "tickets:";

    @Async
    public void cacheTicketsAsync(int page, int size, Page<TicketDto> result) {
        if (result == null || result.isEmpty()) {
            return;
        }

        try {
            String pageKey = "ticket-pages:" + page + ":" + size;
            List<TicketDto> tickets = result.getContent();

            String idsCsv = tickets.stream()
                    .map(ticket -> String.valueOf(ticket.id()))
                    .collect(Collectors.joining(","));

            List<Object> args = new ArrayList<>();
            args.add(String.valueOf(TTL_SECONDS)); 
            args.add(idsCsv); 

            for (TicketDto ticket : tickets) {
                args.add(TICKET_PREFIX + ticket.id());
                args.add(objectMapper.writeValueAsString(ticket));
            }

            redisTemplate.execute(
                    cacheTicketsPageScript,
                    Collections.singletonList(pageKey), 
                    args.toArray(new Object[0])
            );

            log.debug("Successfully cached page {} with size {}", page, size);

        } catch (JsonProcessingException e) {
            log.error("JSON serialization error while caching tickets for page {} size {}", page, size, e);
        } catch (Exception e) {
            log.error("Redis cache write failed for page {} size {}", page, size, e);
        }
    }

    public List<TicketDto> getTicketsByPageFromRedis(int page, int size) {
        try {
            String pageKey = "ticket-pages:" + page + ":" + size;

            @SuppressWarnings("unchecked")
            List<String> rawJsonList = redisTemplate.execute(
                    getTicketsPageScript,
                    Collections.singletonList(pageKey), 
                    new Object[]{ TICKET_PREFIX }
            );

            if (rawJsonList == null || rawJsonList.isEmpty()) {
                return null;
            }

            List<TicketDto> tickets = new ArrayList<>();
            for (String json : rawJsonList) {
                if (json != null) {
                    tickets.add(objectMapper.readValue(json, TicketDto.class));
                }
            }

            return tickets.isEmpty() ? null : tickets;

        } catch (Exception e) {
            log.warn("Redis read failed for page {} size {}, falling back to DB", page, size, e);
            return null;
        }
    }
}