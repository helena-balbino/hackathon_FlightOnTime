package com.flightontime.api.controller;

import com.flightontime.api.client.PythonPredictionClient;
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

/**
 * Controller responsável pelo endpoint de previsão de voos
 * 
 * EQUIPE RESPONSÁVEL: Dupla "Gateway & Validação"
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Flight Prediction", description = "API para previsão de atrasos em voos")
public class FlightController {

    private final FlightPredictionService predictionService;
    private final PythonPredictionClient pythonClient;

    @Operation(
        summary = "Prever atraso de voo",
        description = "Recebe informações do voo e retorna a previsão de atraso com probabilidade associada"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Previsão realizada com sucesso",
            content = @Content(schema = @Schema(implementation = FlightPredictionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
        )
    })
    @PostMapping("/predict")
    public ResponseEntity<FlightPredictionResponse> predict(
            @Valid @RequestBody FlightPredictionRequest request) {
        
        log.info("📨 Recebida requisição de previsão: {} → {}", 
                request.getOrigem(), 
                request.getDestino());

        FlightPredictionResponse response = predictionService.predict(request);

        log.info("📤 Retornando previsão: {}", response.getPrevisao());
        
        return ResponseEntity.ok(response);
    }

    // refinando endpoint health
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean pythonUp = predictionService.isPythonHealthy();

        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "pythonService", pythonUp ? "UP" : "DOWN",
                "timestamp", LocalDateTime.now()
        ));
    }
}
