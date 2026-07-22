package com.example.notification.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.example.notification.model.ProcessedEvent;

import brave.ScopedSpan;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final MongoTemplate mongoTemplate;
    private final Tracer tracer; 

    /**
     * Checks if the event has already been processed and acquires a lock via DB insert.
     * @param eventId Unique identifier of the outbox event
     * @return true if the event is processed for the FIRST TIME, false if ALREADY PROCESSED.
     */
    public boolean processIfFirstTime(String eventId) {

        ScopedSpan span = tracer.startScopedSpan("mongodb-insert-processed-event");
        
        try {
            span.tag("db.type", "mongodb");
            span.tag("db.collection", "processed_events");
            span.tag("db.operation", "insert");
            span.tag("event.id", eventId);

            ProcessedEvent processedEvent = new ProcessedEvent();
            processedEvent.setId(eventId);
            processedEvent.setProcessedAt(String.valueOf(System.currentTimeMillis()));

            mongoTemplate.insert(processedEvent);
            return true; 

        } catch (DuplicateKeyException e) {
            span.tag("duplicate.key", "true");
            log.info("Event ID: {} already processed. Skipping duplicate message.", eventId);
            return false; 

        } catch (Exception e) {
            span.error(e); 
            throw e;

        } finally {
            span.finish(); 
        }
    }

    /**
     * Removes the idempotency record if notification processing fails (Rollback mechanism).
     * @param eventId Unique identifier of the outbox event
     */
    public void undoProcessing(String eventId) {
        ScopedSpan span = tracer.startScopedSpan("mongodb-delete-processed-event");
        
        try {
            span.tag("db.type", "mongodb");
            span.tag("db.collection", "processed_events");
            span.tag("db.operation", "remove");
            span.tag("event.id", eventId);

            ProcessedEvent processedEvent = new ProcessedEvent();
            processedEvent.setId(eventId);
            mongoTemplate.remove(processedEvent);

        } catch (Exception e) {
            span.error(e);
            log.error("Failed to rollback idempotency record for Event ID: {}", eventId, e);
        } finally {
            span.finish();
        }
    }
}