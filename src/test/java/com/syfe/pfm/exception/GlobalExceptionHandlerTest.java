package com.syfe.pfm.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testResourceNotFound() {
        ResponseEntity<Map<String, String>> res = handler.handleResourceNotFound(new ResourceNotFoundException("Not found"));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("Not found", res.getBody().get("error"));
    }

    @Test
    void testConflict() {
        ResponseEntity<Map<String, String>> res = handler.handleConflict(new ConflictException("Conflict"));
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals("Conflict", res.getBody().get("error"));
    }

    @Test
    void testBadRequest() {
        ResponseEntity<Map<String, String>> res = handler.handleBadRequest(new BadRequestException("Bad"));
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Bad", res.getBody().get("error"));
    }

    @Test
    void testForbidden() {
        ResponseEntity<Map<String, String>> res = handler.handleForbidden(new ForbiddenException("Forb"));
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
        assertEquals("Forb", res.getBody().get("error"));
    }

    @Test
    void testException() {
        ResponseEntity<Map<String, String>> res = handler.handleAll(new Exception("Unknown"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertEquals("Internal Server Error", res.getBody().get("error"));
    }

    @Test
    void testValidation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        FieldError fe = new FieldError("object", "field", "message");
        when(br.getFieldErrors()).thenReturn(Collections.singletonList(fe));

        ResponseEntity<Map<String, Object>> res = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Map<String, String> errors = (Map<String, String>) res.getBody().get("errors");
        assertEquals("message", errors.get("field"));
    }
}
