package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BancoCodigoImutavelException extends RuntimeException {
    public BancoCodigoImutavelException(Long id) {
        super("O código FEBRABAN do Banco " + id + " não pode ser alterado após a criação");
    }
}
