package com.example.ticket_query_service.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TicketStatus {

    OPEN("Acik"),
    IN_PROGRESS("Üzerinde Calisiliyor"),
    RESOLVED("Cözüldü"),
    CLOSED("Kapandi");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

    @JsonValue 
    public String getLabel() {
        return label;
    }
}