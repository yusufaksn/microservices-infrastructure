package com.example.ticket_query_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<List> getTicketsPageScript() {
        return RedisScript.of(
                new ClassPathResource("scripts/get_tickets_page.lua"),
                List.class
        );
    }

    @Bean
    public RedisScript<String> cacheTicketsPageScript() {
        return RedisScript.of(
                new ClassPathResource("scripts/cache_tickets_page.lua"),
                String.class
        );
    }
}