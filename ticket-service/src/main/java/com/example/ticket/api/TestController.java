package com.example.ticket.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deployment")
public class TestController {

    @GetMapping("/status")
    public String status() {
        return "CI/CD deployment successful";
    }
}