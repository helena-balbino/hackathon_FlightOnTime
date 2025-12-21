package com.flightontime.api.service;

import com.flightontime.api.client.PythonPredictionClient;
import com.flightontime.api.dto.FlightPredictionRequest;
import com.flightontime.api.dto.FlightPredictionResponse;
import com.flightontime.api.dto.PythonPredictionRequest;
import com.flightontime.api.dto.PythonPredictionResponse;
import com.flightontime.api.mapper.AirlineCodeMapper;
import com.flightontime.api.mapper.AirportCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Serviço responsável pela lógica de previsão de voos
 * 
 * SEMANA 1: Retorna dados MOCKADOS ✅
 * SEMANA 2: Integração com microserviço Python ⬅️ ESTAMOS AQUI!
 * 
 * ESTRATÉGIA DE TRANSIÇÃO:
 * - Flag (use-mock-service) controla mock vs Python
 * - Permite testar a integração gradualmente
 * - Rollback rápido se Python tiver problemas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlightPredictionService {

    private final AirportCodeMapper airportMapper;
    private final AirlineCodeMapper airlineMapper;
    private final PythonPredictionClient pythonClient;

    @Value("${prediction.service.use-mock:true}")
    private boolean useMockService;

    /**
     * Realiza a previsão de atraso do voo
     * 
     * FLUXO:
     * 1. Converte códigos IATA → ICAO (Squad A)
     * 2. Monta DTO para Python
     * 3. Chama serviço Python OU mock (Squad B)
     * 4. Retorna resposta para o Controller
     * 
     * @param request Dados do voo (formato IATA)
     * @return Previsão com status e probabilidade
     */
    public FlightPredictionResponse predict(FlightPredictionRequest request) {
        log.info("🔮 Processando previsão para voo {} → {} (Companhia: {})",
                request.getOrigem(), 
                request.getDestino(), 
                request.getCompanhia());

        // ETAPA 1: Conversão IATA → ICAO (Squad A)
        String origemIcao = airportMapper.toIcao(request.getOrigem());
        String destinoIcao = airportMapper.toIcao(request.getDestino());
        String companhiaIcao = airlineMapper.toIcao(request.getCompanhia());

        log.debug("📝 Conversões: {} → {}, {} → {}, {} → {}",
                request.getOrigem(), origemIcao,
                request.getDestino(), destinoIcao,
                request.getCompanhia(), companhiaIcao);

        // ETAPA 2: Decidir entre Mock ou Python
        if (useMockService) {
            log.info("🎭 MODO MOCK ativado - Usando lógica local");
            return predictWithMock(request, origemIcao, destinoIcao, companhiaIcao);
        } else {
            log.info("🐍 MODO PYTHON ativado - Chamando microserviço");
            return predictWithPython(request, origemIcao, destinoIcao, companhiaIcao);
        }
    }

    /**
     * Previsão usando o microserviço Python (SEMANA 2)
     */
    private FlightPredictionResponse predictWithPython(
            FlightPredictionRequest request,
            String origemIcao,
            String destinoIcao,
            String companhiaIcao) {

        try {
            // Monta DTO para Python
            PythonPredictionRequest pythonRequest = PythonPredictionRequest.builder()
                    .companhiaIcao(companhiaIcao)
                    .origemIcao(origemIcao)
                    .destinoIcao(destinoIcao)
                    .dataPartida(request.getDataPartida().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .distanciaKm(request.getDistanciaKm())
                    .build();

            // Chama serviço Python (Squad B)
            PythonPredictionResponse pythonResponse = pythonClient.getPrediction(pythonRequest);

            // Converte resposta Python → resposta API
            return FlightPredictionResponse.builder()
                    .previsao(pythonResponse.getPrevisao())
                    .probabilidade(pythonResponse.getProbabilidade())
                    .build();

        } catch (Exception ex) {
            log.error("❌ Erro ao chamar Python. Fallback para MOCK.", ex);
            // Fallback: se Python falhar, usa mock
            return predictWithMock(request, origemIcao, destinoIcao, companhiaIcao);
        }
    }

    /**
     * Previsão usando lógica mockada (SEMANA 1)
     * Mantida como fallback de segurança
     */
    private FlightPredictionResponse predictWithMock(
            FlightPredictionRequest request,
            String origemIcao,
            String destinoIcao,
            String companhiaIcao) {

        double probabilidadeAtraso = calcularProbabilidadeMock(request, origemIcao, destinoIcao, companhiaIcao);
        String previsao = probabilidadeAtraso > 0.5 ? "Atrasado" : "Pontual";

        log.info("✅ Previsão MOCK: {} (Probabilidade: {})", previsao, Math.round(probabilidadeAtraso * 100.0) / 100.0);

        return FlightPredictionResponse.builder()
                .previsao(previsao)
                .probabilidade(Math.round(probabilidadeAtraso * 100.0) / 100.0)
                .build();
    }

    /**
     * Calcula probabilidade mockada com base em heurísticas simples
     * (Mantido da Semana 1)
     */
    private double calcularProbabilidadeMock(
            FlightPredictionRequest request,
            String origemIcao,
            String destinoIcao,
            String companhiaIcao) {
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
