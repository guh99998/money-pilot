package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Receita;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceitaResponseDTO(
        Long id,
        String nomeReceita,
        ParceiroResponseDTO parceiro,
        LocalDate dataVencimento,
        int numeroParcelas,
        BigDecimal valorParcela,
        CategoriaResponseDTO categoria,
        String observacoes
) {
    public ReceitaResponseDTO(Receita receita) {
        this(
                receita.getId(),
                receita.getNomeReceita(),
                new ParceiroResponseDTO(receita.getParceiro()),
                receita.getDataVencimento(),
                receita.getNumeroParcelas(),
                receita.getValorParcela(),
                new CategoriaResponseDTO(receita.getCategoria()),
                receita.getObservacoes()
        );
    }
}
