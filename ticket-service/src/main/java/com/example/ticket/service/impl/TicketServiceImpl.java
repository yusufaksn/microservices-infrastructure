package  com.example.ticket.service.impl;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket.dto.TicketDto;
import com.example.ticket.model.PriorityType;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.repository.TicketRepository;
import com.example.ticket.service.TicketNotificationService;
import com.example.ticket.service.TicketService;
import com.example.ticket.component.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketNotificationService ticketNotificationService;
    private final SecurityUtils securityUtils;
 
    @Override
    @Transactional 
    public TicketDto save(TicketDto ticketDto) {
        Ticket ticket = new Ticket();

        ticket.setDescription(ticketDto.getDescription());
        ticket.setNotes(ticketDto.getNotes());
        ticket.setTicketStatus(TicketStatus.valueOf(ticketDto.getTicketStatus()));
        ticket.setPriorityType(PriorityType.valueOf(ticketDto.getPriorityType()));
        ticket.setAssignee(securityUtils.getCurrentUserId());
        
        ticketRepository.save(ticket);

        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
            new org.springframework.transaction.support.TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    ticketNotificationService.sendToQueue(ticket);
                }
            }
        );

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
