package com.gustavolopes.money_pilot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecebimentoRequestDTO(
        @NotNull(message = "Ao mínimo 1 receita é necessária")
        @Size(min = 1)
        List<Long> receitasId,

        @NotNull(message = "A data de recebimento é obrigatória")
        LocalDate dataRecbimento,

        @NotNull(message = "O valor final da Receita é obrigatório")
        @Positive
        BigDecimal valorFinal,

        @NotNull(message = "A Conta Bancária é obrigatória")
        Long contaBancariaId
) {
}
