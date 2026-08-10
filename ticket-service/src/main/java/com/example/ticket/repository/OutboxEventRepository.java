package com.example.ticket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticket.model.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    Optional<OutboxEvent> findByAggregateId(String aggregateId);
}
