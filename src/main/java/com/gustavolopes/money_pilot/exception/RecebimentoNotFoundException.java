package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecebimentoNotFoundException extends RuntimeException {
    public RecebimentoNotFoundException(Long id) {
        super("O Recebimento " + id + " não foi encontrado");
    }
}
