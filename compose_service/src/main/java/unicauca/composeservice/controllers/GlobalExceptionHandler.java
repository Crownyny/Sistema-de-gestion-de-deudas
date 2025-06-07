package unicauca.composeservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientRequestException(WebClientRequestException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause instanceof ConnectException) {
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            errorResponse.put("error", "Servicio no disponible");
            errorResponse.put("message", "No se pudo conectar con el servicio solicitado. Por favor, intente más tarde.");
            errorResponse.put("details", cause.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }

        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Error inesperado en WebClient");
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
