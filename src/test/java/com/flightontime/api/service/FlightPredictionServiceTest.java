package com.flightontime.api.service;

import com.flightontime.api.client.PythonPredictionClient;
import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import com.flightontime.api.mapper.AirlineCodeMapper;
import com.flightontime.api.mapper.AirportCodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para FlightPredictionService
 * 
 * EQUIPE RESPONSÁVEL: Dupla "Business Logic & Mock"
 * 
 * ATUALIZADO SEMANA 2: Adicionados mocks para as dependências
 */
@DisplayName("FlightPredictionService - Testes Unitários")
@ExtendWith(MockitoExtension.class)
class FlightPredictionServiceTest {

    @Mock
    private AirportCodeMapper airportMapper;
    
    @Mock
    private AirlineCodeMapper airlineMapper;
    
    @Mock
    private PythonPredictionClient pythonClient;

    private FlightPredictionService service;

    @BeforeEach
    void setUp() {
        service = new FlightPredictionService(airportMapper, airlineMapper, pythonClient);
        
        // Configura o serviço para usar MOCK (não chamar Python)
        ReflectionTestUtils.setField(service, "useMockService", true);
        
        // Configura comportamento padrão dos mappers
        when(airportMapper.toIcao(anyString())).thenAnswer(invocation -> {
            String iata = invocation.getArgument(0);
            // Simula conversão básica: retorna o próprio código em uppercase
            return "SB" + iata.toUpperCase();
        });
        
        when(airlineMapper.toIcao(anyString())).thenAnswer(invocation -> {
            String iata = invocation.getArgument(0);
            return iata.toUpperCase() + "A";
        });
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

    @Test
    @DisplayName("Squad B - Deve ativar Fallback (Mock) quando o serviço Python falhar")
    void deveAtivarFallbackQuandoPythonFalhar() {
        // 1. Configuramos o serviço para tentar usar o Python (useMockService = false)
        org.springframework.test.util.ReflectionTestUtils.setField(service, "useMockService", false);

        // 2. Criamos um request de teste
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GRU")
                .destino("GIG")
                .dataPartida(LocalDateTime.of(2025, 11, 20, 14, 30))
                .distanciaKm(350)
                .build();

        // 3. Simulamos uma falha crítica no Python (ex: Timeout ou Conexão Recusada)
        when(pythonClient.getPrediction(any())).thenThrow(new RuntimeException("Python Service Offline"));

        // Executamos a chamada. O service deve capturar o erro e chamar o predictWithMock internamente
        FlightPredictionResponse response = service.predict(request);

        assertNotNull(response, "A resposta não deve ser nula mesmo com erro no Python");
        assertTrue(response.getProbabilidade() > 0, "Deve retornar uma probabilidade calculada pelo Mock");

        // Verificamos se o client do Python foi realmente consultado antes de falhar
        org.mockito.Mockito.verify(pythonClient, org.mockito.Mockito.atLeastOnce()).getPrediction(any());

        System.out.println("✅ Squad B: Fallback validado! O sistema usou o Mock após erro no Python.");
    }

    @ParameterizedTest
    @CsvSource({
            "G3, GRU, GIG, 350",
            "AD, VCP, CNF, 450",
            "LA, GRU, MAO, 2800",
            "G3, CGH, SDU, 400"
    })
    @DisplayName("Squad B & A - Teste de Massa de Dados (Sucesso)")
    void deveProcessarMassaDeDadosComSucesso(String cia, String ori, String dest, int dist) {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia(cia)
                .origem(ori)
                .destino(dest)
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(dist)
                .build();

        FlightPredictionResponse response = service.predict(request);

        assertNotNull(response);
        System.out.println("✅ Teste de Massa: " + cia + " voando de " + ori + " para " + dest + " - Status: " + response.getPrevisao());
    }
}
