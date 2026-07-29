package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Banco;

public record BancoResponseDTO(
        Long id,
        String codigoBanco,
        String nomeBanco
) {
    public BancoResponseDTO(Banco banco) {
        this(
                banco.getId(),
                banco.getCodigoBanco(),
                banco.getNomeBanco()
        );
    }
}
