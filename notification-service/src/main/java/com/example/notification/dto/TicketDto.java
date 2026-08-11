package com.example.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TicketDto {

    @JsonProperty("payload_id")
    private String id;

    @JsonProperty("payload_description")
    private String description;

    @JsonProperty("payload_notes")
    private String notes;

    @JsonProperty("payload_assignee")
    private String assignee;

    @JsonProperty("payload_ticketDate")
    private String ticketDate;

    @JsonProperty("payload_priorityType")
    private String priorityType;

    @JsonProperty("payload_ticketStatus")
    private String ticketStatus;

    @JsonProperty("payload_createdAt")
    private String createdAt;

    @JsonProperty("payload_updatedAt")
    private String updatedAt;
}