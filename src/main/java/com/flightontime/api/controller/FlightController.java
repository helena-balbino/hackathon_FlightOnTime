package com.flightontime.api.controller;

import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import com.flightontime.api.service.FlightPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Flight Prediction", description = "API para previsão de atrasos em voos")
public class FlightController {

    private final FlightPredictionService predictionService;

    @PostMapping("/predict")
    public ResponseEntity<FlightPredictionResponse> predict(
            @Valid @RequestBody FlightPredictionRequest request) {
        log.info("Recebida requisição de previsão: {} → {}", request.getOrigem(), request.getDestino());
        FlightPredictionResponse response = predictionService.predict(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Verificar saúde da API", description = "Endpoint para health check detalhado")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "FlightOnTime API is running! ✈️");
        response.put("java_version", System.getProperty("java.version"));
        response.put("timestamp", LocalDateTime.now());

        log.info("Health check acessado: Status UP");
        return ResponseEntity.ok(response);
    }
}