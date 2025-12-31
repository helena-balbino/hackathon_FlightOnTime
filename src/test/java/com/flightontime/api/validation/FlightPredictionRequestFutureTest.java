package com.flightontime.api.validation;

import com.flightontime.api.dto.FlightPredictionRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FlightPredictionRequestFutureTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void deveFalharQuandoDataPartidaForNoPassado() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().minusDays(1))
                .distanciaKm(350)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .hasSize(1)
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals("dataPartida") &&
                                v.getMessage().equals("Data de partida deve ser futura")
                );
    }

    @Test
    void devePassarQuandoDataPartidaForFutura() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(350)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalharQuandoDataPartidaForNoPresente() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now())
                .distanciaKm(350)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("dataPartida"));
    }

    @Test
    void deveFalharQuandoCamposObrigatoriosForemNulos() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .hasSizeGreaterThan(0)
                .anyMatch(v -> v.getPropertyPath().toString().equals("companhia") ||
                              v.getPropertyPath().toString().equals("origem") ||
                              v.getPropertyPath().toString().equals("destino") ||
                              v.getPropertyPath().toString().equals("dataPartida"));
    }

    @Test
    void deveFalharQuandoDistanciaForNegativa() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(-100)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("distanciaKm"));
    }

    @Test
    void deveFalharQuandoDistanciaForZero() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .distanciaKm(0)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("distanciaKm"));
    }
}
