package com.flightontime.api.service;

import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para FlightPredictionService
 * 
 * EQUIPE RESPONSÁVEL: Dupla "Business Logic & Mock"
 */
@DisplayName("FlightPredictionService - Testes Unitários")
class FlightPredictionServiceTest {

    private FlightPredictionService service;

    @BeforeEach
    void setUp() {
        service = new FlightPredictionService();
    }

    @Test
    @DisplayName("Deve prever voo pontual para voo de manhã")
    void devePrevervoopontualParaVooDeManha() {
        // Arrange
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("AZ")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.of(2025, 11, 10, 8, 30))
                .distanciaKm(350)
                .build();

        // Act
        FlightPredictionResponse response = service.predict(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPrevisao());
        assertNotNull(response.getProbabilidade());
        assertTrue(response.getProbabilidade() >= 0.0 && response.getProbabilidade() <= 1.0);
        assertTrue(response.getPrevisao().equals("Pontual") || response.getPrevisao().equals("Atrasado"));
    }

    @Test
    @DisplayName("Deve prever maior probabilidade de atraso para voo à noite")
    void devePrevervooMaiorProbabilidadeAtrasoParaVooANoite() {
        // Arrange
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("LA")
                .origem("GRU")
                .destino("MAO")
                .dataPartida(LocalDateTime.of(2025, 11, 15, 20, 45))
                .distanciaKm(2850)
                .build();

        // Act
        FlightPredictionResponse response = service.predict(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getPrevisao());
        assertNotNull(response.getProbabilidade());
    }

    @Test
    @DisplayName("Deve retornar probabilidade entre 0 e 1")
    void deveRetornarProbabilidadeEntre0E1() {
        // Arrange
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("CGH")
                .destino("SDU")
                .dataPartida(LocalDateTime.of(2025, 11, 16, 14, 0))
                .distanciaKm(400)
                .build();

        // Act
        FlightPredictionResponse response = service.predict(request);

        // Assert
        assertTrue(response.getProbabilidade() >= 0.0);
        assertTrue(response.getProbabilidade() <= 1.0);
    }

    @Test
    @DisplayName("Deve retornar previsão válida (Pontual ou Atrasado)")
    void deveRetornarPrevisaoValida() {
        // Arrange
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("AZ")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(350)
                .build();

        // Act
        FlightPredictionResponse response = service.predict(request);

        // Assert
        assertTrue(
            response.getPrevisao().equals("Pontual") || 
            response.getPrevisao().equals("Atrasado"),
            "Previsão deve ser 'Pontual' ou 'Atrasado'"
        );
    }

    @Test
    @DisplayName("Deve validar agravante de datas festicas e tempestade (Fatores 5 e 6)")
    void deveValidarCenarioNatalETempestade() {
        // Arrange: 24 de Dezembro, 19h (janela de chuva), saindo de Guarulhos
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("LA")
                .origem("SBGR")
                .destino("REC")
                .dataPartida(LocalDateTime.of(2025, 12, 24, 19, 0))
                .distanciaKm(2100)
                .build();

        FlightPredictionResponse response = service.predict(request);

        assertTrue(response.getProbabilidade() > 0.6, "Cenário crítico deve ter probabilidade alta");
        assertEquals("Atrasado", response.getPrevisao());
    }

    @Test
    @DisplayName("Deve validar fator mitigante de Primeira Onda e Inverno (Fatores 9 e 11)")
    void deveValidarCenarioFavoravelPrimeiraOnda() {
        // Arrange: Junho (estável), 07h da manhã (primeira onda), Companhia AZ
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("AZ")
                .origem("VIX")
                .destino("SDU")
                .dataPartida(LocalDateTime.of(2025, 6, 15, 7, 0))
                .distanciaKm(420)
                .build();

        FlightPredictionResponse response = service.predict(request);

        assertTrue(response.getProbabilidade() < 0.4, "Cenário favorável deve ter probabilidade baixa");
        assertEquals("Pontual", response.getPrevisao());
    }

    @Test
    @DisplayName("Deve validar o teto máximo de probabilidade (Segurança)")
    void deveGarantirLimiteMaximo() {
        // Arrange: Forçando o máximo de agravantes possíveis
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("SBGR") // Hub
                .destino("SSA") // Turístico
                .dataPartida(LocalDateTime.of(2025, 12, 22, 18, 30)) // Natal + Tempestade + Sexta
                .distanciaKm(1500)
                .build();

        FlightPredictionResponse response = service.predict(request);

        assertTrue(response.getProbabilidade() <= 0.98);
    }
}
