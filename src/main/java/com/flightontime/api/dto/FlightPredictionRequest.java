package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisição de previsão de voo
 * Contrato alinhado com o time de Data Science
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do voo para previsão de atraso")
public class FlightPredictionRequest {

    @NotBlank(message = "Companhia aérea é obrigatória")
    @Schema(description = "Código IATA da companhia aérea", example = "AZ")
    @JsonProperty("companhia")
    private String companhia;

    @NotBlank(message = "Aeroporto de origem é obrigatório")
    @Schema(description = "Código IATA do aeroporto de origem", example = "GIG")
    @JsonProperty("origem")
    private String origem;

    @NotBlank(message = "Aeroporto de destino é obrigatório")
    @Schema(description = "Código IATA do aeroporto de destino", example = "GRU")
    @JsonProperty("destino")
    private String destino;

    @NotNull(message = "Data de partida é obrigatória")
    @Schema(description = "Data e hora de partida do voo", example = "2025-11-10T14:30:00")
    @JsonProperty("data_partida")
    private LocalDateTime dataPartida;

    @NotNull(message = "Distância é obrigatória")
    @Positive(message = "Distância deve ser um valor positivo")
    @Schema(description = "Distância em quilômetros", example = "350")
    @JsonProperty("distancia_km")
    private Integer distanciaKm;
}
