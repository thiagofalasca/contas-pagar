package com.totvs.contas_pagar.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(String message) {
        super(message);
    }
}
