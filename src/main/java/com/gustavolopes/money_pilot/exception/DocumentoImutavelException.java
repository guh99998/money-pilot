package com.gustavolopes.money_pilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DocumentoImutavelException extends RuntimeException {
    public DocumentoImutavelException(Long id) {
        super("O documento (CPF/CNPJ) do Parceiro " + id + " não pode ser alterado após a criação");
    }
}
