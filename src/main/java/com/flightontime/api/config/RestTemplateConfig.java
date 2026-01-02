package com.flightontime.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para requisições HTTP
 * 
 * EQUIPE RESPONSÁVEL: Squad B (Integração & Core)
 * 
 * Este Bean será injetado no PythonPredictionClient
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        log.info("🔧 Configurando RestTemplate Bean");
        return new RestTemplate();
    }
}
