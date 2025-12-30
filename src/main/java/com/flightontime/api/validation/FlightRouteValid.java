package com.flightontime.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * -Anotação de validação de classe para garantir que a origem e o destino do voo
 * sejam aeroportos diferentes.
 * -Aplicável apenas a classes que possuem os campos "origem" e "destino"
 * -Mensagem padrão: "Origem e destino não podem ser iguais"
 * -Essa anotação é processada pelo FlightRouteValidator
 */
@Documented
@Constraint(validatedBy = FlightRouteValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlightRouteValid {

    String message() default "Origem e destino não podem ser iguais";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
