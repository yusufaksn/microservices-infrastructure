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
    private Long ticketDate;        
    private Integer priorityType;  
    private Integer ticketStatus;  
    private Long createdAt;         
    private Long updatedAt;       
}