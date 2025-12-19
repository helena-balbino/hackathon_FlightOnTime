package com.flightontime.api.service;

import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        log.info("🔮 Processando previsão para voo {} → {} (Companhia: {})",
                request.getOrigem(), 
                request.getDestino(), 
                request.getCompanhia());


        // MOCK: Lógica simples baseada em heurísticas
        double probabilidadeAtraso = calcularProbabilidadeMock(request);
        
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
    private double calcularProbabilidadeMock(FlightPredictionRequest request) {
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
        if ("AZ".equalsIgnoreCase(request.getCompanhia())) {
            score -= 0.05; // Companhia com boa pontualidade
        } else if ("G3".equalsIgnoreCase(request.getCompanhia())) {
            score += 0.05; // Companhia com boa pontualidade
        } else if ("LA".equalsIgnoreCase(request.getCompanhia())) {
            score -= 0.05; // Companhia com boa pontualidade
        }

        // Fator 5: Datas Críticas (Ex: Natal/Ano Novo/carnaval)
        int dia = request.getDataPartida().getDayOfMonth();
        int mes = request.getDataPartida().getMonthValue();

        // Natal / Ano Novo
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
        java.util.List<String> hubs = java.util.Arrays.asList("GRU", "CGH", "BSB", "SDU");
        if (hubs.contains(request.getOrigem().toUpperCase())) {
            score += 0.18;
            log.info("Alerta Hub: Origem em aeroporto de alta densidade detectada.");
        }


        // ------------------------------- Fatores Mitigantes --------------------------------
        // Pra o nosso mock não ficar tão pessimista e acabar tendendo muito ao atraso vou adicionar alguns casos onde o voo tende a ser mais pontual pesquisei alguns fatores na IA


        // 1º fator mitigante: Aeroportos maiores e com baixo fluxo
        java.util.List<String> hubsOtimizados = java.util.List.of("CNF", "BSB");
        if (hubsOtimizados.contains(request.getDestino().toUpperCase()) &&
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
