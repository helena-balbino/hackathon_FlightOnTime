package com.flightontime.api.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AirportCodeMapperTest {

    private final AirportCodeMapper mapper = new AirportCodeMapper();

    @Test
    @DisplayName("Deve converter códigos IATA brasileiros para ICAO com sucesso")
    void deveConverterIataParaIcao() {
        assertEquals("SBGR", mapper.toIcao("GRU"));
        assertEquals("SBGL", mapper.toIcao("GIG"));
        assertEquals("SBSP", mapper.toIcao("CGH"));
        assertEquals("SBRF", mapper.toIcao("REC"));
    }

    @Test
    @DisplayName("Deve retornar o próprio código se não encontrar mapeamento")
    void deveRetornarMesmoCodigoQuandoNaoEncontrado() {
        assertEquals("JFK", mapper.toIcao("JFK"));
    }
}