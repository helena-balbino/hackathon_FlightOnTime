package com.flightontime.api.client;

import com.flightontime.api.dto.PythonPredictionRequest;
import com.flightontime.api.dto.PythonPredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client HTTP para comunicação com o microserviço Python (Data Science)
 * 
 * EQUIPE RESPONSÁVEL: Squad B (Integração & Core)
 * 
 * RESPONSABILIDADE:
 * - Fazer requisição POST para o serviço Python
 * - Converter exceções HTTP em exceções de negócio
 * - Logar requisições/respostas para debugging
 * 
 * TECH STACK:
 * - RestTemplate (Spring Framework)
 * - Alternativa moderna: WebClient (considerar para versões futuras)
 */
@Slf4j
@Component
public class PythonPredictionClient {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl;

    /**
     * Construtor com injeção de dependências
     * 
     * @param restTemplate Bean configurado no Spring Context
     * @param pythonServiceUrl URL do serviço Python (vem do application.properties)
     */
    public PythonPredictionClient(
            RestTemplate restTemplate,
            @Value("${prediction.service.url}") String pythonServiceUrl) {
        this.restTemplate = restTemplate;
        this.pythonServiceUrl = pythonServiceUrl;
        log.info("🔗 PythonPredictionClient inicializado. URL: {}", pythonServiceUrl);
    }

    /**
     * Faz requisição POST para o serviço Python
     * 
     * @param request Dados do voo em formato ICAO
     * @return Previsão retornada pelo modelo de ML
     * @throws RuntimeException se houver erro na comunicação
     */
    public PythonPredictionResponse getPrediction(PythonPredictionRequest request) {
        try {
            log.info("📤 Enviando requisição para Python: {} → {}",
                    request.getOrigemIcao(),
                    request.getDestinoIcao());

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Criar a requisição
            HttpEntity<PythonPredictionRequest> httpEntity = new HttpEntity<>(request, headers);

            // Fazer a chamada POST
            ResponseEntity<PythonPredictionResponse> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/predict",
                    httpEntity,
                    PythonPredictionResponse.class
            );

            PythonPredictionResponse body = response.getBody();

            log.info("📥 Resposta do Python: Previsão={}, Probabilidade={}",
                    body != null ? body.getPrevisao() : "null",
                    body != null ? body.getProbabilidade() : "null");

            return body;

        } catch (Exception ex) {
            log.error("❌ Erro ao comunicar com o serviço Python: {}", ex.getMessage(), ex);
            throw new RuntimeException("Falha na comunicação com o serviço de previsão: " + ex.getMessage(), ex);
        }
    }

    /**
     * Health check do serviço Python
     * (Útil para monitoramento e testes)
     * 
     * @return true se o serviço está respondendo
     */
    public boolean isHealthy() {
        try {
            restTemplate.getForEntity(pythonServiceUrl + "/health", String.class);
            return true;
        } catch (Exception ex) {
            log.warn("⚠️ Serviço Python não está respondendo: {}", ex.getMessage());
            return false;
        }
    }
}
