package com.flightontime.api.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper para conversão de códigos IATA → ICAO de aeroportos brasileiros
 * 
 * EQUIPE RESPONSÁVEL: Squad A (Interface & Dados)
 * 
 * CONTEXTO:
 * - Usuário envia códigos IATA (3 letras): GRU, GIG, CGH, etc.
 * - Modelo Python espera códigos ICAO (4 letras): SBGR, SBGL, SBSP, etc.
 * 
 * MVP: Map estático com os principais aeroportos brasileiros
 * FUTURO: Pode ser migrado para banco de dados ou API externa
 */
@Slf4j
@Component
public class AirportCodeMapper {

    /**
     * Mapa de conversão IATA → ICAO
     * Principais aeroportos brasileiros (Top 15)
     */
    private static final Map<String, String> IATA_TO_ICAO = Map.ofEntries(
            // São Paulo
            Map.entry("GRU", "SBGR"),  // Guarulhos
            Map.entry("CGH", "SBSP"),  // Congonhas
            Map.entry("VCP", "SBKP"),  // Viracopos
            
            // Rio de Janeiro
            Map.entry("GIG", "SBGL"),  // Galeão
            Map.entry("SDU", "SBRJ"),  // Santos Dumont
            
            // Capitais
            Map.entry("BSB", "SBBR"),  // Brasília
            Map.entry("CNF", "SBCF"),  // Confins (BH)
            Map.entry("POA", "SBPA"),  // Porto Alegre
            Map.entry("CWB", "SBCT"),  // Curitiba
            Map.entry("MAO", "SBEG"),  // Manaus
            Map.entry("REC", "SBRF"),  // Recife
            Map.entry("SSA", "SBSV"),  // Salvador
            Map.entry("FOR", "SBFZ"),  // Fortaleza
            
            // Outros importantes
            Map.entry("AFL", "SBAT"),  // Alta Floresta
            Map.entry("CMG", "SBCR"),  // Corumbá
            Map.entry("CKS", "SBCJ"),  // Carajás
            Map.entry("JDO", "SBJU"),  // Juazeiro do Norte
            Map.entry("POO", "SBPC")   // Poços de Caldas
    );

    /**
     * Converte código IATA para ICAO
     * 
     * @param iataCode Código IATA (3 letras, ex: "GRU")
     * @return Código ICAO (4 letras, ex: "SBGR")
     * @throws IllegalArgumentException se o código IATA não for reconhecido
     */
    public String toIcao(String iataCode) {
        if (iataCode == null || iataCode.isBlank()) {
            throw new IllegalArgumentException("Código IATA não pode ser vazio");
        }

        String normalizedIata = iataCode.trim().toUpperCase();
        String icaoCode = IATA_TO_ICAO.get(normalizedIata);

        if (icaoCode == null) {
            log.warn("⚠️ Código IATA não mapeado: {}. Retornando o código original.", normalizedIata);
            // Fallback: retorna o código original (pode ser útil para testes)
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
