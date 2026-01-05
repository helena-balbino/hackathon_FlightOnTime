package com.flightontime.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@SpringBootApplication
public class FlightOnTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightOnTimeApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))  // Aumentado de 3s para 5s
                .setReadTimeout(Duration.ofSeconds(10))    // Aumentado de 5s para 10s (predições de ML podem demorar)
                .build();
    }
}
