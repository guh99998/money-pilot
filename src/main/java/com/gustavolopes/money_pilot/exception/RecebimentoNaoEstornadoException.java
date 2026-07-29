package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecebimentoNaoEstornadoException extends RuntimeException {
    public RecebimentoNaoEstornadoException(Long id) {
        super("O Recebimento " + id + " precisa estar estornado para essa operação");
    }
}
