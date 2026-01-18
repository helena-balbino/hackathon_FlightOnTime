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
 * * RESPONSABILIDADE:
 * - Fazer requisição POST para o serviço Python (FastAPI)
 * - Implementar Retry em caso de falhas temporárias
 * - Converter JSON em objetos Java (DTOs)
 */
@Slf4j
@Component
public class PythonPredictionClient {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl;

    public PythonPredictionClient(
            RestTemplate restTemplate,
            @Value("${prediction.service.url}") String pythonServiceUrl) {
        this.restTemplate = restTemplate;
        this.pythonServiceUrl = pythonServiceUrl;
        log.info("🔗 PythonPredictionClient inicializado na URL: {}", pythonServiceUrl);
    }

    /**
     * Faz a chamada para a IA no Python.
     * Tenta 3 vezes com intervalo crescente (backoff) antes de desistir.
     */
    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PythonPredictionResponse getPrediction(PythonPredictionRequest request) {
        try {
            log.info("📤 Enviando para Python: {} → {} (Data: {})",
                    request.getDados().getAerodromoOrigem(),
                    request.getDados().getAerodromoDestino(),
                    request.getDados().getPartidaPrevista());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PythonPredictionRequest> httpEntity = new HttpEntity<>(request, headers);

            // Chamada POST para o endpoint /predict
            ResponseEntity<PythonPredictionResponse> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/predict",
                    httpEntity,
                    PythonPredictionResponse.class
            );

            PythonPredictionResponse body = response.getBody();

            if (body != null) {
                log.info("📥 Resposta do Python: Status={}, Probabilidade={}",
                        body.getLabel(),
                        body.getProbaAtraso());
            }

            return body;

        } catch (Exception ex) {
            log.error("❌ Erro na comunicação com Python: {}", ex.getMessage());
            throw new RestClientException("Erro ao conectar com a API de Data Science", ex);
        }
    }

    /**
     * Fallback: Executado quando as 3 tentativas de Retry falham.
     */
    @Recover
    public PythonPredictionResponse recover(RestClientException ex, PythonPredictionRequest request) {
        log.error("⚠️ Fallback acionado: O serviço Python está fora do ar ou o contrato mudou.");
        throw ex; // O Service capturará isso e usará o Mock
    }

    /**
     * Verifica se o microserviço Python está online
     */
    public boolean isHealthy() {
        try {
            restTemplate.getForEntity(pythonServiceUrl + "/health", String.class);
            return true;
        } catch (Exception ex) {
            log.warn("⚠️ Health Check falhou para o serviço Python.");
            return false;
        }
    }
}