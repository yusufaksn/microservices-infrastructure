package com.yusufaksn.ticketsearch.dto;

import com.yusufaksn.ticketsearch.document.TicketDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {
    private String id;
    private String description;
    private String notes;
    private String ticketStatus;
    private String priorityType;
    private String assignee;
    private String ticketDate;
    private String traceId;

    public static TicketResponseDto fromDocument(TicketDocument doc) {
        return TicketResponseDto.builder()
                .id(doc.getPayloadId() != null ? doc.getPayloadId() : doc.getId())
                .description(doc.getPayloadDescription())
                .notes(doc.getPayloadNotes())
                .ticketStatus(doc.getPayloadTicketStatus())
                .priorityType(doc.getPayloadPriorityType())
                .assignee(doc.getPayloadAssignee())
                .ticketDate(doc.getPayloadTicketDate())
                .traceId(doc.getTraceId())
                .build();
    }
}