package com.techub.api.exception;

import com.techub.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlredyExistsExeception.class)
    public ResponseEntity<ErrorResponse> handdleEmailExists(EmailAlredyExistsExeception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        409,
                        "EMAIL_AREALDY_EXISTS",
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler (InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handdleInvalidCredentials(InvalidCredentialsException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(
                        401,
                        "INVALID_CREDENTIALS",
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                400,
                                "GENERIC_ERROR",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ErrorResponse> EmailSendException(EmailSendException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        500,
                        "SEND_ERROR_EMAIL",
                        ex.getMessage()
                )
        );
    }

    @ExceptionHandler(DominioEmailInvalidoException.class)
    public ResponseEntity<String> handleDominioInvalido(DominioEmailInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}