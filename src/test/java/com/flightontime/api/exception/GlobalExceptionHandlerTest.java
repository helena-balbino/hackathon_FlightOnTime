package com.flightontime.api.exception;

import com.flightontime.api.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar JSON padronizado para erros de argumento inválido")
    void deveTratarErroDeArgumento() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/predict");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Valor inválido"), request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("/api/predict", response.getBody().getPath());
    }
}