package com.mb3.challenge.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 — El recurso no fue encontrado o la validación falló.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String detailMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Error de validación en la solicitud: {}", detailMessage);
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                detailMessage
        );
    }

    /**
     * 400 — Error de tipo de argumento en la solicitud.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        Object value = ex.getValue();
        String message = String.format("El parámetro '%s' debe ser de tipo '%s' (valor recibido: '%s')", name, type, value);

        log.warn("Error de conversión de tipo: {}", message);
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                message
        );
    }

    /**
     * 404 — El recurso no fue encontrado en el API externo (Petstore).
     */
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiErrorResponse> handleFeignNotFound(FeignException.NotFound ex) {
        log.warn("Recurso no encontrado en el API externo: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                "El recurso solicitado no existe en el sistema externo"
        );
    }

    /**
     * 400 — El API externo rechazó la solicitud por datos inválidos.
     */
    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ApiErrorResponse> handleFeignBadRequest(FeignException.BadRequest ex) {
        log.warn("Solicitud inválida rechazada por el API externo: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "La solicitud enviada al sistema externo contiene datos inválidos"
        );
    }

    /**
     * 502 — Error genérico de comunicación con el API externo (Feign).
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorResponse> handleFeignException(FeignException ex) {
        log.error("Error de comunicación con el API externo [status={}]: {}",
                ex.status(), ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "Bad Gateway",
                "Error al comunicarse con el sistema externo"
        );
    }

    /**
     * 500 — Error de negocio interno.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceException(ServiceException ex) {
        log.error("Error interno del servicio: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage()
        );
    }

    /**
     * 500 — Cualquier otra excepción no controlada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocurrió un error inesperado, intente más tarde"
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                            String error,
                                                            String message) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
