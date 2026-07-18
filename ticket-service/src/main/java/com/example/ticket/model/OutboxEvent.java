package com.example.ticket.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @Column(name = "trace_id", nullable = false, length = 32)
    private String traceId;

    @Column(name = "span_id", nullable = false, length = 16)
    private String spanId;

    @Column(name = "sampled", length = 1)
    private String sampled;

    @Column(name = "db_committed_at", nullable = false)
    private OffsetDateTime dbCommittedAt; 

    @PrePersist
    public void prePersist() {
        if (dbCommittedAt == null) {
            dbCommittedAt = OffsetDateTime.now();
        }

        if (sampled == null) {
            sampled = "1";
        }
    }
}