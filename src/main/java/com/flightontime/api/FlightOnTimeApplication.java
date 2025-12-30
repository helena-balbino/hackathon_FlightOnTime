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
                .setConnectTimeout(Duration.ofSeconds(3)) // Tempo para abrir a conexão
                .setReadTimeout(Duration.ofSeconds(5))    // Tempo para esperar o Python responder
                .build();
    }
}