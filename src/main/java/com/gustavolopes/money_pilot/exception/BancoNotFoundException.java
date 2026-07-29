package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BancoNotFoundException extends RuntimeException {
    public BancoNotFoundException(Long id) {
        super("O Banco " + id + " não foi encontrado");
    }
}
