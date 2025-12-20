package com.flightontime.api.service;

import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

/**
 * Serviço responsável pela lógica de previsão de voos
 * 
 * SEMANA 1: Retorna dados MOCKADOS para permitir desenvolvimento independente
 * SEMANA 2: Será implementada a integração com o microserviço Python do time DS
 */
@Slf4j
@Service
public class FlightPredictionService {

    /**
     * Realiza a previsão de atraso do voo
     * 
     * MOCK STRATEGY (Semana 1):
     * - Voos de manhã (06h-12h): maior probabilidade de serem pontuais
     * - Voos à tarde/noite (12h-23h): maior probabilidade de atraso
     * - Fins de semana: maior probabilidade de serem pontuais
     * - Voos de curta distância (<500km): maior probabilidade de serem pontuais
     * 
     * @param request Dados do voo
     * @return Previsão com status e probabilidade
     */

    private static final Map<String, String> AEROPORTO_MAP = Map.ofEntries(
            Map.entry("GRU", "SBGR"), Map.entry("CGH", "SBSP"), Map.entry("BSB", "SBBR"),
            Map.entry("GIG", "SBGL"), Map.entry("SDU", "SBRJ"), Map.entry("CNF", "SBCF"),
            Map.entry("POA", "SBPA"), Map.entry("CWB", "SBCT"), Map.entry("MAO", "SBEG"),
            Map.entry("VCP", "SBKP"), Map.entry("AFL", "SBAT"), Map.entry("CMG", "SBCR"),
            Map.entry("CKS", "SBCJ"), Map.entry("JDO", "SBJU"), Map.entry("POO", "SBPC")
    );

    private static final Map<String, String> COMPANHIA_MAP = Map.ofEntries(
            Map.entry("AZ", "AZU"), Map.entry("G3", "GLO"), Map.entry("LA", "TAM"),
            Map.entry("AC", "ACN"), Map.entry("UX", "AEA"), Map.entry("AF", "AFR"),
            Map.entry("AM", "AMX"), Map.entry("AR", "ARG"), Map.entry("AV", "AVA")
    );

    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        String origemIcao = AEROPORTO_MAP.getOrDefault(request.getOrigem().toUpperCase(), request.getOrigem());
        String destinoIcao = AEROPORTO_MAP.getOrDefault(request.getDestino().toUpperCase(), request.getDestino());
        String companhiaIcao = COMPANHIA_MAP.getOrDefault(request.getCompanhia().toUpperCase(), request.getCompanhia());

        log.info("🔮 Processando previsão para voo {} → {} (Companhia: {})",
                request.getOrigem(), 
                request.getDestino(), 
                request.getCompanhia());


        // MOCK: Lógica simples baseada em heurísticas
        double probabilidadeAtraso = calcularProbabilidadeMock(request, origemIcao, destinoIcao, companhiaIcao);
        
        String previsao = probabilidadeAtraso > 0.5 ? "Atrasado" : "Pontual";

        log.info("✅ Previsão: {} (Probabilidade: {})", previsao, Math.round(probabilidadeAtraso * 100.0) / 100.0);

        return FlightPredictionResponse.builder()
                .previsao(previsao)
                .probabilidade(Math.round(probabilidadeAtraso * 100.0) / 100.0) // Arredonda para 2 casas
                .build();
    }

    /**
     * Calcula probabilidade mockada com base em heurísticas simples
     */
    private double calcularProbabilidadeMock(FlightPredictionRequest request, String origemIcao,  String destinoIcao, String companhiaIcao) {
        double score = 0.5; // Base neutra

        // Fator 1: Horário do voo
        LocalTime horario = request.getDataPartida().toLocalTime();
        if (horario.isBefore(LocalTime.of(12, 0))) {
            score -= 0.2; // Manhã: menos atraso
        } else if (horario.isAfter(LocalTime.of(18, 0))) {
            score += 0.2; // Noite: mais atraso
        }

        // Fator 2: Dia da semana
        DayOfWeek diaSemana = request.getDataPartida().getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            score -= 0.1; // Fim de semana: menos atraso
        } else if (diaSemana == DayOfWeek.FRIDAY) {
            score += 0.15; // Sexta: mais atraso
        }

        int distancia = request.getDistanciaKm();
        if (distancia < 500) {
            score -= 0.1; // Voo curto: menos atraso
        } else if (distancia > 1500) {
            score += 0.1; // Voo longo: mais atraso
        }

        // Fator 4: Companhias específicas (simulação)
        if ("AZU".equalsIgnoreCase(companhiaIcao)) {
            score -= 0.05; // boa reputação
        } else if ("GLO".equalsIgnoreCase(companhiaIcao)) {
            score += 0.05; // má reputação
        } else if ("TAM".equalsIgnoreCase(companhiaIcao)) {
            score -= 0.05; // boa reputação
        } else if ("ACN".equalsIgnoreCase(companhiaIcao)) {
            score += 0.05; // má reputação
        } else if ("AFR".equalsIgnoreCase(companhiaIcao)) {
            score -= 0.05; // boa reputação
        }

        // Fator 5: Datas Críticas (Ex: Natal/Ano Novo/)
        int dia = request.getDataPartida().getDayOfMonth();
        int mes = request.getDataPartida().getMonthValue();

        if (mes == 12 && dia >= 20) {
            score += 0.20;

            if (distancia < 500) {
                log.info("🔄 Voo curto em período crítico: risco de efeito cascata.");
                score += 0.08;
            }
            log.info("Fator Sazonal: Período de festas e alta demanda.");
        }


        // Fator 6: Tempestades de verão
        int hora = horario.getHour();
        if ((mes == 12 || mes <= 2) && (hora >= 16 && hora <= 20)) {
            score += 0.15;
            log.info("Alerta Clima: Voo em janela de alta probabilidade de chuvas fortes.");
        }

        // Fator 7: Aeroportos que devido ao fluxo elevado tendem a ter maior atraso
        java.util.List<String> hubs = java.util.Arrays.asList("SBGR", "SBSP", "SBRJ", "SBGL", "SBBR");
        if (hubs.contains(origemIcao.toUpperCase())) {
            score += 0.18;
            log.info("Alerta Hub: Origem em aeroporto de alta densidade detectada.");
        }


        // ------------------------------- Fatores Mitigantes --------------------------------
        // Pra o nosso mock não ficar tão pessimista e acabar tendendo muito ao atraso vou adicionar alguns casos onde o voo tende a ser mais pontual


        // 1º fator mitigante: Aeroportos maiores e com baixo fluxo
        java.util.List<String> hubsOtimizados = java.util.List.of("SBJU", "SBCJ", "SBCR", "SBAT", "SBPC");
        if (hubsOtimizados.contains(destinoIcao.toUpperCase()) &&
                (horario.isAfter(LocalTime.of(10, 0)) && horario.isBefore(LocalTime.of(15, 0)))) {
            score -= 0.10;
            log.info("Fator Mitigante: Fluxo otimizado no destino em horário de baixa densidade.");
        }

        // 2º fator mitigante: Estabilidade Climática (Outono/Inverno)
        if (mes >= 5 && mes <= 8) {
            score -= 0.08;
            log.info("Fator Mitigante: Período de maior estabilidade climática.");
        }


        // Garante que a probabilidade fica entre 0.1 e 0.95
        return Math.max(0.1, Math.min(0.95, score));
    }
}
