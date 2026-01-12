package com.flightontime.api.client;

import com.flightontime.api.dto.PythonPredictionRequest;
import com.flightontime.api.dto.PythonPredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client HTTP para comunicação com o microserviço Python (Data Science)
 * 
 * EQUIPE RESPONSÁVEL: Squad B (Integração & Core)
 * 
 * RESPONSABILIDADE:
 * - Fazer requisição POST para o serviço Python
 * - Retry automático com backoff exponencial
 * - Converter exceções HTTP em exceções de negócio
 * - Logar requisições/respostas para debugging
 * 
 * TECH STACK:
 * - RestTemplate (Spring Framework)
 * - Spring Retry para resiliência
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
     * Com retry automático em caso de falha transitória
     * 
     * @param request Dados do voo em formato ICAO
     * @return Previsão retornada pelo modelo de ML
     * @throws RestClientException se houver erro na comunicação (após todas as tentativas)
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
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
            log.error("❌ Erro ao comunicar com o serviço Python: {}", ex.getMessage());
            throw new RestClientException("Falha na comunicação com o serviço de previsão", ex);
        }
    }

    /**
     * Método de recuperação quando todas as tentativas de retry falharem
     * 
     * @param ex Exceção que causou a falha
     * @param request Request original
     * @return null (indica falha para o service usar fallback)
     */
    @Recover
    public PythonPredictionResponse recover(RestClientException ex, PythonPredictionRequest request) {
        log.error("⚠️ Todas as {} tentativas de conexão falharam. Fallback será acionado.", 3);
        throw ex; // Re-lança para o Service tratar com mock
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
