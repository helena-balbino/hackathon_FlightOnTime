package com.flightontime.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI
 * 
 * Acesse a documentação em: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flightOnTimeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlightOnTime API")
                        .version("1.0.0")
                        .description("API para previsão de atrasos em voos utilizando Machine Learning")
                        .contact(new Contact()
                                .name("Time Backend - FlightOnTime")
                                .email("backend@flightontime.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local"),
                        new Server()
                                .url("https://api.flightontime.com")
                                .description("Servidor de Produção (futuro)")
                ));
    }
}
