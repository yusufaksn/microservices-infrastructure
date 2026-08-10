package com.example.ticket.service;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.transaction.annotation.Transactional;

import com.example.ticket.dto.TicketDto;
import com.example.ticket.model.OutboxEvent;
import com.example.ticket.model.Ticket;
import com.example.ticket.repository.OutboxEventRepository;
import com.example.ticket.repository.TicketRepository;

@SpringBootTest
@Transactional 
class TicketServiceImplIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSaveTicketAndOutboxEvent() {
        // Arrange
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(userId.toString())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );

        TicketDto dto = new TicketDto();
        dto.setDescription("Test ticket");
        dto.setNotes("Test notes");
        dto.setTicketStatus("OPEN");
        dto.setPriorityType("HIGH");

        // Act
        TicketDto savedDto = ticketService.save(dto);

        // Assert - Ticket
        Ticket ticket = ticketRepository.findById(savedDto.getId())
                .orElseThrow(() -> new AssertionError("Ticket not found in the database"));

        assertThat(ticket.getDescription()).isEqualTo("Test ticket");
        
        // Match assignee with user ID converted to String
        assertThat(ticket.getAssignee()).isEqualTo(userId.toString());

        // Assert - Outbox Event
        OutboxEvent event = outboxEventRepository.findByAggregateId(ticket.getId().toString())
                .orElseThrow(() -> new AssertionError("Outbox event not found in the database"));

        assertThat(event.getAggregateType()).isEqualTo("Ticket");
        assertThat(event.getEventType()).isEqualTo("TicketCreated");
    }
}