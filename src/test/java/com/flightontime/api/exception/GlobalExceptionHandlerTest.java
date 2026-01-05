package com.flightontime.api.exception;

import com.flightontime.api.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        assertEquals("Dados inválidos", response.getBody().getMessage());
        assertEquals("/api/predict", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve tratar status 404 - Not Found com NoHandlerFoundException")
    void deveTratarResourceNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/voo/999");
        
        NoHandlerFoundException ex = new NoHandlerFoundException(
                "GET", "/api/voo/999", null);

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("O recurso solicitado não foi encontrado", response.getBody().getMessage());
        assertEquals("/api/voo/999", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
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
        assertEquals("Ocorreu um erro interno inesperado", response.getBody().getMessage());
        assertEquals("/api/predict", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    /**
     * Teste de integração para verificar se o handler captura 404 automaticamente
     * Requer configuração no application.properties:
     * spring.mvc.throw-exception-if-no-handler-found=true
     * spring.web.resources.add-mappings=false
     */
    @SpringBootTest
    @AutoConfigureMockMvc
    static class IntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("Deve retornar 404 para rota inexistente (integração)")
        void deveRetornar404ParaRotaInexistente() throws Exception {
            mockMvc.perform(get("/api/rota/inexistente"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("O recurso solicitado não foi encontrado"));
        }
    }
}