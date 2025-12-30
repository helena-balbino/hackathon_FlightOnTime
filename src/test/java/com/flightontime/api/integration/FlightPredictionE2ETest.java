package com.flightontime.api.integration;

import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import com.flightontime.api.service.FlightPredictionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration") // Usa o application-integration.properties
public class FlightPredictionE2ETest {

    @Autowired
    private FlightPredictionService predictionService;

    @Test
    @DisplayName("E2E - Deve conectar com o serviço Python real e retornar uma previsão válida")
    void deveObterRespostaRealDoPython() {
        // 1. request com dados que o Squad A mapeou (ex: GRU -> GIG)
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GRU")
                .destino("GIG")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(350)
                .build();

        // 2. Executamos a chamada. Com o perfil 'integration', o 'use-mock' estará 'false'
        try {
            FlightPredictionResponse response = predictionService.predict(request);

            // 3. Validações do que vem do Python
            assertNotNull(response, "A resposta não deveria ser nula");
            assertNotNull(response.getPrevisao(), "O Python deve retornar o texto da previsão");
            assertTrue(response.getProbabilidade() >= 0, "A probabilidade deve ser um número positivo");

            System.out.println("🚀 [E2E SUCCESS] Resposta real do Python: " + response.getPrevisao());
            System.out.println("📊 Probabilidade retornada: " + response.getProbabilidade());

        } catch (Exception e) {
            fail("Falha na integração E2E. O Flask está na porta 5000? Erro: " + e.getMessage());
        }
    }
}