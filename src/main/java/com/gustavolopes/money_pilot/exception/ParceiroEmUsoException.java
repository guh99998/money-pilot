package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParceiroEmUsoException extends RuntimeException {
    public ParceiroEmUsoException(Long id) {
        super("O Parceiro " + id + " está associado a uma ou mais receitas/despesas e não pode ser removido");
    }
}
