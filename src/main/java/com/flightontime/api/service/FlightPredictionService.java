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
        
        log.info("✅ Previsão: {} (Probabilidade: {:.2f})", previsao, probabilidadeAtraso);

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

        // Fator 3: Distância
        if (request.getDistanciaKm() < 500) {
            score -= 0.1; // Voo curto: menos atraso
        } else if (request.getDistanciaKm() > 1500) {
            score += 0.1; // Voo longo: mais atraso
        }

        // Fator 4: Companhias específicas (simulação)
        if ("AZ".equalsIgnoreCase(request.getCompanhia())) {
            score -= 0.05; // Companhia com boa pontualidade
        }

        // Garante que a probabilidade fica entre 0.1 e 0.95
        return Math.max(0.1, Math.min(0.95, score));
    }
}
