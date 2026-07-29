package com.gustavolopes.money_pilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ContaBancariaRequestDTO(
        @NotNull(message = "O Banco é obrigatório")
        Long bancoId,

        @NotBlank(message = "O nome da Conta Bancária é obrigatório")
        String nomeContaBancaria,

        @NotBlank(message = "O número da Agência é obrigatório")
        String agencia,

        @NotBlank(message = "O Número da Conta Bancária é obrigatória")
        String numeroConta,

        @NotNull(message = "O Saldo Inicial é obrigatório")
        @PositiveOrZero
        BigDecimal saldoInicial
) {
}
