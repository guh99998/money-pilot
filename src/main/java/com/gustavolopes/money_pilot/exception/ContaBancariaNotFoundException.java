package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ContaBancariaNotFoundException extends RuntimeException {
    public ContaBancariaNotFoundException(Long id) {
        super("A conta bancária " + id + " não foi encontrada");
    }
}
