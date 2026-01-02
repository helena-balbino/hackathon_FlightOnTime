package com.flightontime.api.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper para conversão de códigos IATA → ICAO de companhias aéreas
 * 
 * EQUIPE RESPONSÁVEL: Squad A (Interface & Dados)
 * 
 * CONTEXTO:
 * - Usuário envia códigos IATA (2 caracteres): G3, AZ, LA, etc.
 * - Modelo Python espera códigos ICAO (3 letras): GLO, AZU, TAM, etc.
 * 
 * MVP: Map estático com as principais companhias que operam no Brasil
 * FUTURO: Pode ser migrado para banco de dados ou API externa
 */
@Slf4j
@Component
public class AirlineCodeMapper {

    /**
     * Mapa de conversão IATA → ICAO
     * Principais companhias aéreas operando no Brasil
     */
    private static final Map<String, String> IATA_TO_ICAO = Map.ofEntries(
            // Brasileiras
            Map.entry("AZ", "AZU"),  // Azul
            Map.entry("G3", "GLO"),  // GOL
            Map.entry("LA", "TAM"),  // LATAM
            Map.entry("AD", "AZU"),  // Azul Conecta (usa mesmo ICAO)
            
            // Internacionais operando no Brasil
            Map.entry("AC", "ACN"),  // Air Canada
            Map.entry("UX", "AEA"),  // Air Europa
            Map.entry("AF", "AFR"),  // Air France
            Map.entry("AM", "AMX"),  // Aeroméxico
            Map.entry("AR", "ARG"),  // Aerolíneas Argentinas
            Map.entry("AV", "AVA"),  // Avianca
            Map.entry("BA", "BAW"),  // British Airways
            Map.entry("CM", "CMP"),  // Copa Airlines
            Map.entry("DL", "DAL"),  // Delta
            Map.entry("IB", "IBE"),  // Iberia
            Map.entry("KL", "KLM"),  // KLM
            Map.entry("LH", "DLH"),  // Lufthansa
            Map.entry("TP", "TAP"),  // TAP Portugal
            Map.entry("UA", "UAL")   // United Airlines
    );

    /**
     * Converte código IATA para ICAO
     * 
     * @param iataCode Código IATA (2 caracteres, ex: "G3")
     * @return Código ICAO (3 letras, ex: "GLO")
     * @throws IllegalArgumentException se o código IATA não for reconhecido
     */
    public String toIcao(String iataCode) {
        if (iataCode == null || iataCode.isBlank()) {
            throw new IllegalArgumentException("Código IATA não pode ser vazio");
        }

        String normalizedIata = iataCode.trim().toUpperCase();
        String icaoCode = IATA_TO_ICAO.get(normalizedIata);

        if (icaoCode == null) {
            log.warn("⚠️ Código IATA de companhia não mapeado: {}. Retornando o código original.", normalizedIata);
            // Fallback: retorna o código original
            return normalizedIata;
        }

        log.debug("✅ Conversão: {} → {}", normalizedIata, icaoCode);
        return icaoCode;
    }

    /**
     * Verifica se um código IATA é suportado
     * 
     * @param iataCode Código IATA
     * @return true se o código está mapeado
     */
    public boolean isSupported(String iataCode) {
        if (iataCode == null || iataCode.isBlank()) {
            return false;
        }
        return IATA_TO_ICAO.containsKey(iataCode.trim().toUpperCase());
    }

    /**
     * Retorna todos os códigos IATA suportados
     * (Útil para documentação e testes)
     */
    public java.util.Set<String> getSupportedIataCodes() {
        return IATA_TO_ICAO.keySet();
    }
}
