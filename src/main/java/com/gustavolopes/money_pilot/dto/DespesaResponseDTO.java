package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponseDTO(
        Long id,
        String nomeDespesa,
        ParceiroResponseDTO parceiro,
        LocalDate dataVencimento,
        int numeroParcelas,
        BigDecimal valorParcela,
        CategoriaResponseDTO categoria,
        String observacoes
) {
    public DespesaResponseDTO(Despesa despesa) {
        this(
                despesa.getId(),
                despesa.getNomeDespesa(),
                new ParceiroResponseDTO(despesa.getParceiro()),
                despesa.getDataVencimento(),
                despesa.getNumeroParcelas(),
                despesa.getValorParcela(),
                new CategoriaResponseDTO(despesa.getCategoria()),
                despesa.getObservacoes()
        );
    }
}