package com.example.ticket.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-action")
public class TestController {

    @GetMapping
    public String test() {
        return "CI/CD deployment successful";
    }
}