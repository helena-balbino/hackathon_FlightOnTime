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

class FlightRouteValidatorTest {

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
    void deveFalharQuandoOrigemEDestinoForemIguais() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GRU")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .hasSize(1)
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals("origem") &&
                                v.getMessage().equals("Origem e destino devem ser aeroportos diferentes")
                );
    }

    @Test
    void devePassarQuandoOrigemEDestinoForemDiferentes() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void deveFalharQuandoOrigemForNula() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem(null)
                .destino("GRU")
                .dataPartida(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("origem"));
    }

    @Test
    void deveFalharQuandoDestinoForNulo() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GIG")
                .destino(null)
                .dataPartida(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .isNotEmpty()
                .anyMatch(v -> v.getPropertyPath().toString().equals("destino"));
    }

    @Test
    void deveFalharQuandoCodigosAeroportosForemInvalidos() {
        FlightPredictionRequest request = FlightPredictionRequest.builder()
                .companhia("G3")
                .origem("GRUU") // 4 caracteres ao invés de 3
                .destino("GI") // 2 caracteres ao invés de 3
                .dataPartida(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<FlightPredictionRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .hasSizeGreaterThanOrEqualTo(2);
    }
}
