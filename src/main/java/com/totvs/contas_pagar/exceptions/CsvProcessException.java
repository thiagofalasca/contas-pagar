package com.totvs.contas_pagar.exceptions;

public class CsvProcessException extends RuntimeException {
    public CsvProcessException(String message) {
        super(message);
    }

    public CsvProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}

