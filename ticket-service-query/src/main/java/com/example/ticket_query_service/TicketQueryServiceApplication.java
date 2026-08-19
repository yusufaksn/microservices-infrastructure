package com.example.ticket_query_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TicketQueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketQueryServiceApplication.class, args);
	}

}
