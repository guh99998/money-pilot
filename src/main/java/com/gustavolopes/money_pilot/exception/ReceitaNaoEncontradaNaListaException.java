package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReceitaNaoEncontradaNaListaException extends RuntimeException {
    public ReceitaNaoEncontradaNaListaException() {
        super("Uma das Receitas não foi encontrada dentro do Recebimento");
    }
}
