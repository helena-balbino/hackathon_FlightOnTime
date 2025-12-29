package com.flightontime.api.validation;

import com.flightontime.api.dto.FlightPredictionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * -Validador que implementa a regra da anotação FlightRouteValid
 * -Verifica se a origem e o destino de um  FlightPredictionRequest são diferentes
 * -Se forem iguais, adiciona uma violação de validação no campo "origem"
 */
public class FlightRouteValidator
        implements ConstraintValidator<FlightRouteValid, FlightPredictionRequest> {

    @Override
    public boolean isValid(
            FlightPredictionRequest value,
            ConstraintValidatorContext context
    ) {
        if (value == null) {
            return true; // Bean Validation padrão
        }

        if (value.getOrigem() == null || value.getDestino() == null) {
            return true; // deixa @NotBlank cuidar disso
        }

        boolean valid = !value.getOrigem().equals(value.getDestino());

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Origem e destino devem ser aeroportos diferentes"
                    ).addPropertyNode("origem") // aponta erro no campo
                    .addConstraintViolation();
        }

        return valid;
    }
}
