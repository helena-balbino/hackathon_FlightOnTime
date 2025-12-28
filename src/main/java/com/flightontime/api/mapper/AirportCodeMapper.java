package com.flightontime.api.mapper;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class AirportCodeMapper {
    private static final Map<String, String> iataToIcao = new HashMap<>();

    static {
        iataToIcao.put("GRU", "SBGR");
        iataToIcao.put("CGH", "SBSP");
        iataToIcao.put("GIG", "SBGL");
        iataToIcao.put("SDU", "SBRJ");
        iataToIcao.put("REC", "SBRF");
        iataToIcao.put("CNF", "SBCF");
    }

    public String toIcao(String iata) {
        if (iata == null) return null;
        return iataToIcao.getOrDefault(iata.toUpperCase(), iata);
    }
}