package com.flightontime.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de REQUEST para o microserviço Python
 *
 * EQUIPE RESPONSÁVEL: Squad A (Interface & Dados)
 *
 * ⚠️ ATENÇÃO: Este DTO deve estar EXATAMENTE igual ao que o time de DS espera!
 * Qualquer campo com nome diferente vai resultar em erro 400/422.
 *
 * CONTRATO COM DATA SCIENCE (Python):
 * {
 *   "companhia_icao": "GLO",
 *   "origem_icao": "SBGR",
 *   "destino_icao": "SBGL",
 *   "data_partida": "2025-11-10T14:30:00",
 *   "distancia_km": 350
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PythonPredictionRequest {
    // Este é o novo Wrapper que o Python exige
    @JsonProperty("dados")
    private PythonDataPayload dados;

    @JsonProperty("topk")
    @Builder.Default
    private Integer topk = 8;

    @Data
    @Builder
    public static class PythonDataPayload {
        @JsonProperty("partida_prevista")
        private String partidaPrevista;
        @JsonProperty("empresa_aerea")
        private String empresaAerea;
        @JsonProperty("aerodromo_origem")
        private String aerodromoOrigem;
        @JsonProperty("aerodromo_destino")
        private String aerodromoDestino;
        @JsonProperty("codigo_tipo_linha")
        private String codigoTipoLinha;
    }
}