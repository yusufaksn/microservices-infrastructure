package com.example.ticket.repository;

import com.example.ticket.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    
}
