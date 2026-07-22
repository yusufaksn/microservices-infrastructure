package com.example.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.notification.model.ProcessedEvent;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {
    
}
