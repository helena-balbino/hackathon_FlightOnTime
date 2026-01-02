package com.flightontime.api.exception;

import com.flightontime.api.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 400 para IllegalArgumentException")
    void deveTratarErroDeArgumento() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/predict");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Dados inválidos"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve tratar status 404 - Not Found")
    void deveTratarResourceNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/voo/999");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    @DisplayName("Deve tratar status 500 - Internal Server Error")
    void deveTratarExcecaoGenerica() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/predict");

        ResponseEntity<ErrorResponse> response = handler.handleAllExceptions(
                new Exception("Erro inesperado"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}