package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta da previsão de voo
 * Contrato alinhado com o time de Data Science
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado da previsão de atraso do voo")
public class FlightPredictionResponse {

    @Schema(description = "Status previsto do voo", example = "Atrasado", allowableValues = {"Pontual", "Atrasado"})
    @JsonProperty("previsao")
    private String previsao;

    @Schema(description = "Probabilidade da previsão (0.0 a 1.0)", example = "0.78", minimum = "0.0", maximum = "1.0")
    @JsonProperty("probabilidade")
    private Double probabilidade;
}
