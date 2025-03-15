package com.totvs.contas_pagar.exceptions;

public class ContaNotFoundException extends RuntimeException {
    public ContaNotFoundException() {
        super("Conta não encontrado!");
    }

    public ContaNotFoundException(String message) {
        super(message);
    }
}
