package com.techub.api.exception;

import com.techub.api.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(401, "INVALID_CREDENTIALS", ex.getMessage())
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(409, "EMAIL_ALREADY_EXISTS", ex.getMessage())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        String mensagem = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        if (mensagem.contains("email") || mensagem.contains("ukhymsg6hpnk88xrsy9kdsuhur9")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse(409, "EMAIL_ALREADY_EXISTS", "Email já está em uso.")
            );
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(409, "CONSTRAINT_VIOLATION", "Violação de integridade de dados.")
        );
    }

    @ExceptionHandler(DominioEmailInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleDominioInvalido(DominioEmailInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(422, "DOMINIO_EMAIL_INVALIDO", ex.getMessage())
        );
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ErrorResponse> handleEmailSend(EmailSendException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(500, "EMAIL_SEND_ERROR", ex.getMessage())
        );
    }

    @ExceptionHandler(TokenExpiradoException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpirado(TokenExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(
                new ErrorResponse(410, "TOKEN_EXPIRADO", ex.getMessage())
        );
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(400, "TOKEN_INVALIDO", ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ErrorResponse(422, "VALIDATION_ERROR", mensagem)
        );
    }

    @ExceptionHandler(UserDesactivatedException.class)
    public ResponseEntity<ErrorResponse> handdleUserDesactivated(UserDesactivatedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(401, "UNAUTHORIZED", ex.getMessage())
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(
                new ErrorResponse(
                        ex.getStatusCode().value(),
                        "HTTP_ERROR",
                        ex.getReason()
                )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(500, "INTERNAL_ERROR", ex.getMessage())
        );
    }
}