package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DespesaNotFoundException extends RuntimeException {
    public DespesaNotFoundException(Long id) {
        super("A Despesa " + id + " não foi encontrada");
    }
}
