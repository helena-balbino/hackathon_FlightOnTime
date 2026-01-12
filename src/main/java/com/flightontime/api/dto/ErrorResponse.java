package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de erro padronizada
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de erro padronizada")
public class ErrorResponse {

    @Schema(description = "Timestamp do erro", example = "2025-12-16T10:30:00")
    @JsonProperty("timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Código HTTP do erro", example = "400")
    @JsonProperty("status")
    private Integer status;

    @Schema(description = "Tipo do erro", example = "Bad Request")
    @JsonProperty("error")
    private String error;

    @Schema(description = "Mensagem descritiva do erro", example = "Dados inválidos fornecidos")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/v1/predict")
    @JsonProperty("path")
    private String path;

    @Schema(description = "Lista de erros de validação, quando aplicável")
    @JsonProperty("errors")
    private List<String> errors;
}
