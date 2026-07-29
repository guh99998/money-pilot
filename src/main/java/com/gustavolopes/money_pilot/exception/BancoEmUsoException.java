package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BancoEmUsoException extends RuntimeException {
    public BancoEmUsoException(Long id) {
        super("O banco " + id + " está associado a uma ou mais contas bancárias e não pode ser removido");
    }
}
