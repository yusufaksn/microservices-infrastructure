package  com.example.ticket.service.impl;


import java.time.OffsetDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ticket.component.SecurityUtils;

import com.example.ticket.dto.TicketDto;
import com.example.ticket.model.PriorityType;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.model.OutboxEvent;
import com.example.ticket.repository.OutboxEventRepository;
import com.example.ticket.repository.TicketRepository;
import com.example.ticket.service.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final SecurityUtils securityUtils;
    private final ModelMapper modelMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final Tracer tracer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TicketDto save(TicketDto ticketDto) {
        Ticket ticket = modelMapper.map(ticketDto, Ticket.class);
       
        ticket.setDescription(ticketDto.getDescription());
        ticket.setNotes(ticketDto.getNotes());
        ticket.setTicketStatus(TicketStatus.valueOf(ticketDto.getTicketStatus()));
        ticket.setPriorityType(PriorityType.valueOf(ticketDto.getPriorityType()));
        ticket.setAssignee(securityUtils.getCurrentUserId());
        ticketRepository.save(ticket);

        Span currentSpan = tracer.currentSpan();

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateId(ticket.getId());
        outboxEvent.setAggregateType("Ticket");
        outboxEvent.setEventType("TicketCreated");


        JsonNode payload = objectMapper.valueToTree(ticket);
        outboxEvent.setPayload(payload);
        outboxEvent.setDbCommittedAt(OffsetDateTime.now());

 
        if (currentSpan != null) {
            outboxEvent.setTraceId(currentSpan.context().traceId());
            outboxEvent.setSpanId(currentSpan.context().spanId());
            outboxEvent.setSampled(currentSpan.context().sampled() ? "1" : "0");
        }
        
        outboxEventRepository.save(outboxEvent);
        return ticketDto;
    }

    @Override
    public TicketDto update(String id, TicketDto ticketDto) {
        return null;
    }

    @Override
    public TicketDto getById(String ticketId) {
        return null;
    }

    @Override
    public Page<TicketDto> getPagination(Pageable pageable) {
        return null;
    }
}
