package com.yusufaksn.ticketsearch.dto;

import lombok.Data;

@Data
public class TicketSearchRequest {
    private String query;    
    private String status;     
    private String priorityType;  
    private String assignee;      
    private int page = 0;
    private int size = 10;
}