package com.techub.api.exception;

public class UserDesactivatedException extends RuntimeException {
    public UserDesactivatedException() { super("Sua conta foi desativada! Para mais informações entre em contato em jose.xavier.dev@gmail.com"); }
}
