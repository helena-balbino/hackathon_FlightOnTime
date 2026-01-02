package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de RESPONSE do microserviço Python
 * 
 * EQUIPE RESPONSÁVEL: Squad A (Interface & Dados)
 * 
 * ⚠️ ATENÇÃO: Este DTO deve estar EXATAMENTE igual ao que o time de DS vai retornar!
 * 
 * CONTRATO COM DATA SCIENCE (Python):
 * {
 *   "previsao": "Atrasado",
 *   "probabilidade": 0.78,
 *   "modelo_versao": "v1.0"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PythonPredictionResponse {

    @JsonProperty("previsao")
    private String previsao; // "Pontual" ou "Atrasado"

    @JsonProperty("probabilidade")
    private Double probabilidade; // 0.0 a 1.0

    @JsonProperty("modelo_versao")
    private String modeloVersao; // Versão do modelo de ML
}
