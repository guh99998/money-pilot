package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.ContaBancaria;

import java.math.BigDecimal;

public record ContaBancariaResponseDTO(
        Long id,
        BancoResponseDTO banco,
        String nomeContaBancaria,
        String agencia,
        String numeroConta,
        BigDecimal saldoInicial
) {
    public ContaBancariaResponseDTO(ContaBancaria contaBancaria) {
        this(
                contaBancaria.getId(),
                new BancoResponseDTO(contaBancaria.getBanco()),
                contaBancaria.getNomeContaBancaria(),
                contaBancaria.getAgencia(),
                contaBancaria.getNumeroConta(),
                contaBancaria.getSaldoInicial()
        );
    }
}
