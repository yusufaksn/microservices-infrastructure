package com.example.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OutboxEventDto {

    private String id;

    @JsonProperty("aggregate_type")
    private String aggregateType;

    @JsonProperty("aggregate_id")
    private String aggregateId;

    @JsonProperty("event_type")
    private String eventType;

    private String payload;

    @JsonProperty("trace_id")
    private String traceId;

    @JsonProperty("span_id")
    private String spanId;

    private String sampled;

    @JsonProperty("db_committed_at")
    private String dbCommittedAt;
}