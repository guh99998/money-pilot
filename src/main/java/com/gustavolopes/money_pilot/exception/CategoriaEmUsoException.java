package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaEmUsoException extends RuntimeException {
    public CategoriaEmUsoException(Long id) {
        super("A categoria " + id + " está associada a uma ou mais receitas e não pode ser removida");
    }
}
