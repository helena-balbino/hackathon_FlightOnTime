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
public class PythonPredictionResponse {
    private Integer prediction;
    private String label;

    @JsonProperty("proba_atraso")
    private Double probaAtraso;

    @JsonProperty("explain_global") // Mapeia o que vem do Python
    private Object explainGlobal;    // Gera o getExplainGlobal()

    @JsonProperty("explain_local")  // Mapeia o que vem do Python
    private Object explainLocal;     // Gera o getExplainLocal()
}