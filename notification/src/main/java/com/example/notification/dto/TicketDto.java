package com.example.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDto {
    private String id;          
    private String description;     
    private String notes;         
    private String assignee;        
    private String ticketDate;        
    private String priorityType;  
    private String ticketStatus;  
    private String createdAt;         
    private String updatedAt;       
}