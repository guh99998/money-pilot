package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecebimentoNaoConfirmadoException extends RuntimeException {
    public RecebimentoNaoConfirmadoException(Long id) {
        super("O Recebimento " + id + " não está confirmado");
    }
}
