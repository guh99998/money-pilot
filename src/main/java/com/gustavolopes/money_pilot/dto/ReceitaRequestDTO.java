package com.gustavolopes.money_pilot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceitaRequestDTO(
        @NotBlank(message = "O nome da receita é obrigatório")
        String nomeReceita,

        @NotNull(message = "O Parceiro é obrigatório")
        Long parceiroId,

        @NotNull(message = "A data de vencimento é obrigatória")
        LocalDate dataVencimento,

        @Min(1)
        int numeroParcelas,

        @NotNull(message = "O valor da parcela é obrigatório")
        @Positive
        BigDecimal valorParcela,

        @NotNull(message = "A categoria da receita é obrigatória")
        Long categoriaId,

        @NotNull(message = "As observações da receita são obrigatórias")
        String observacoes
) {}
