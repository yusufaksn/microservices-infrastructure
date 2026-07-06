package com.example.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    @RabbitListener(queues = "ticket-queue")
    public void receiveMessage(String message) {
        System.out.println("_____________________________________");
        System.out.println("Received message from queue: " + message);
    }
        
}
