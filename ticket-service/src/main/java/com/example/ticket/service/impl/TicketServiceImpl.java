package  com.example.ticket.service.impl;




import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticket.dto.TicketDto;
import com.example.ticket.model.PriorityType;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;

import com.example.ticket.repository.TicketRepository;

import com.example.ticket.service.TicketService;
import com.example.ticket.service.TicketNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    //private final TicketElasticRepository ticketElasticRepository;
    private final TicketRepository ticketRepository;
    private final TicketNotificationService ticketNotificationService;
    //private final AccountServiceClient accountServiceClient;
 

    @Override
    @Transactional
    public TicketDto save(TicketDto ticketDto) {
        // Ticket Entity
        // mysql kaydet
        // ticketModel nesnesi oluştur.
        // elastic kaydet
        // nesneyi return et.

       
        Ticket ticket = new Ticket();

       // ResponseEntity<AccountDto> accountDtoResponseEntity = accountServiceClient.get(ticketDto.getAssignee());
        ticket.setDescription(ticketDto.getDescription());
        ticket.setNotes(ticketDto.getNotes());
        ticket.setTicketStatus(TicketStatus.valueOf(ticketDto.getTicketStatus()));
        ticket.setPriorityType(PriorityType.valueOf(ticketDto.getPriorityType()));
  
        ticket.setAssignee(UUID.randomUUID().toString());
        
        ticketRepository.save(ticket);
        //ticketElasticRepository.save(ticketData);

        ticketNotificationService.sendToQueue(ticket);
        

        // Kuyruga notification yaz
        //ticketNotificationService.sendToQueue(ticket);
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
