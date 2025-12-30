package com.flightontime.api.validation;

import com.flightontime.api.dto.FlightPredictionRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FlightPredictionRequestFutureTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
                .dataPartida(LocalDateTime.now().plusHours(2))
                .distanciaKm(350)
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
