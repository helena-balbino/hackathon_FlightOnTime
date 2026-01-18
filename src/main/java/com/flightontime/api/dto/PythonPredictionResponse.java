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
@NoArgsConstructor
@AllArgsConstructor
public class PythonPredictionResponse {

    private Integer prediction;
    private String label;

    @JsonProperty("proba_atraso")
    private Double probaAtraso;

    // AQUI ESTÁ O SEGREDO: O nome no JsonProperty deve ser igual ao do Python!
    @JsonProperty("explain_global") 
    private Object explainGlobal;

    @JsonProperty("explain_local")
    private Object explainLocal;
}

