package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Recebimento;
import com.gustavolopes.money_pilot.model.StatusRecebimento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RecebimentoResponseDTO(
        Long id,
        List<ReceitaResponseDTO> receitas,
        LocalDate dataRecebimento,
        BigDecimal valorFinal,
        ContaBancariaResponseDTO contaBancaria,
        StatusRecebimento status
) {
    public RecebimentoResponseDTO(Recebimento recebimento) {
        this(
                recebimento.getId(),
                recebimento.getReceitas().stream().map(ReceitaResponseDTO::new).toList(),
                recebimento.getDataRecebimento(),
                recebimento.getValorFinal(),
                new ContaBancariaResponseDTO(recebimento.getContaBancaria()),
                recebimento.getStatus()
        );
    }
}
