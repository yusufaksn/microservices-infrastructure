package com.example.ticket_query_service.model;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PriorityType {

    URGENT("Acil"),
    LOW("Önemsiz"),
    HIGH("Yüksek Öncelikli");

    private final String label;

    PriorityType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}