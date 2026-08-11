package com.yusufaksn.ticketsearch.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "outbox.event.ticket")
public class TicketDocument {

    @Id
    private String id;

    @Field(name = "traceId", type = FieldType.Keyword)
    private String traceId;

    @Field(name = "spanId", type = FieldType.Keyword)
    private String spanId;

    @Field(name = "payload_id", type = FieldType.Keyword)
    private String payloadId;

    @Field(name = "payload_notes", type = FieldType.Text)
    private String payloadNotes;

    @Field(name = "payload_ticketDate", type = FieldType.Date, format = DateFormat.date_optional_time)
    private String payloadTicketDate;

    @Field(name = "payload_assignee", type = FieldType.Keyword)
    private String payloadAssignee;

    @Field(name = "payload_ticketStatus", type = FieldType.Keyword)
    private String payloadTicketStatus;

    @Field(name = "payload_description", type = FieldType.Text)
    private String payloadDescription;

    @Field(name = "payload_priorityType", type = FieldType.Keyword)
    private String payloadPriorityType;
}