package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ReceitaNotFoundException extends RuntimeException {
    public ReceitaNotFoundException(Long id) {
        super("A Receita " + id + " não foi encontrada");
    }
}
