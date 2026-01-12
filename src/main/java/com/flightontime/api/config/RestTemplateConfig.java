package com.flightontime.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para requisições HTTP
 * com timeout configurável
 * 
 * EQUIPE RESPONSÁVEL: Squad B (Integração & Core)
 * 
 * Este Bean será injetado no PythonPredictionClient
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${prediction.service.timeout:5000}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {
        log.info("🔧 Configurando RestTemplate Bean com timeout de {}ms", timeout);
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        
        return new RestTemplate(factory);
    }
}
