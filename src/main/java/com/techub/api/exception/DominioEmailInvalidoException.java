package com.techub.api.exception;

public class DominioEmailInvalidoException extends RuntimeException {
    public DominioEmailInvalidoException() {
        super("E-mail deve ser de um domínio institucional da CPS/Fatec");
    }
}