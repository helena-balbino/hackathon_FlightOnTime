package com.flightontime.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightontime.api.dto.FlightPredictionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Teste de Regras de Negócio e Robustez")
class FlightBusinessIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.flightontime.api.client.PythonPredictionClient pythonClient;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("Site Todo: Deve processar previsão com sucesso e aplicar regras de horário")
    void deveValidarFluxoCompleto() throws Exception {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("AD")
                .origem("GRU").destino("GIG")
                .dataPartida(LocalDateTime.now().plusDays(2))
                .distanciaKm(400).build();

        mockMvc.perform(post("/api/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previsao").exists())
                .andExpect(jsonPath("$.probabilidade").exists());
    }
}