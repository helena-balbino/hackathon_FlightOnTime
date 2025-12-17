package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Size(min =2, max = 2, message = "O código da companhia aérea deve ter 2 caracteres")
    @JsonProperty("companhia")
    private String companhia;

    @NotBlank(message = "Aeroporto de origem é obrigatório")
    @Schema(description = "Código IATA do aeroporto de origem", example = "GIG")
    @Size(min = 3,max = 3, message = "A origem deve ter 3 caracteres (IATA)")
    @JsonProperty("origem")
    private String origem;

    @NotBlank(message = "Aeroporto de destino é obrigatório")
    @Schema(description = "Código IATA do aeroporto de destino", example = "GRU")
    @Size(min = 3,max = 3, message = "O destino deve ter 3 caracteres (IATA)")
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
