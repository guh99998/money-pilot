package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContaBancariaEmUsoException extends RuntimeException {
    public ContaBancariaEmUsoException(Long id) {
        super("A Conta Bancária " + id + " está associado a uma ou mais receitas/despesas e não pode ser removida");
    }
}
