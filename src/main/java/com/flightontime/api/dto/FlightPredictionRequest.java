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

    @JsonProperty("companhia_icao")
    private String companhiaIcao;

    @JsonProperty("origem_icao")
    private String origemIcao;

    @JsonProperty("destino_icao")
    private String destinoIcao;

    @JsonProperty("data_partida")
    private String dataPartida; // Formato ISO: "2025-11-10T14:30:00"

    @JsonProperty("distancia_km")
    private Integer distanciaKm;
}
