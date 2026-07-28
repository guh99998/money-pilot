package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.*;

import java.util.Set;

public record ParceiroResponseDTO (
        Long id,
        String nome,
        String telefone,
        String email,
        Set<TipoParceiro> tipoParceiros,
        TipoDocumento tipoDocumento,
        String cpf,
        String cnpj,
        Endereco endereco
) {
    public ParceiroResponseDTO(Parceiro parceiro) {
        this(
                parceiro.getId(),
                parceiro.getNome(),
                parceiro.getTelefone(),
                parceiro.getEmail(),
                parceiro.getTipoParceiros(),
                parceiro instanceof ParceiroPF ? TipoDocumento.CPF : TipoDocumento.CNPJ,
                parceiro instanceof ParceiroPF pf ? pf.getCpf() : null,
                parceiro instanceof ParceiroPJ pj ? pj.getCnpj() : null,
                parceiro.getEndereco()
        );
    }
}
