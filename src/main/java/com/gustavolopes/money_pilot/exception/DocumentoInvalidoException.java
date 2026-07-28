package com.gustavolopes.money_pilot.exception;

import com.gustavolopes.money_pilot.model.TipoDocumento;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DocumentoInvalidoException extends RuntimeException {
    public DocumentoInvalidoException(TipoDocumento tipoDocumento) {
        super("É necessário informar um " + (tipoDocumento == TipoDocumento.CPF ? "cpf" : "cnpj")
                + " válido para o tipo de documento " + tipoDocumento);
    }
}
