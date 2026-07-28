package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record ParceiroRequestDTO (
        @NotBlank(message = "O nome do Parceiro é obrigatório")
        String nome,

        @Pattern(
                regexp = "^(55)?([1-9]{2})?(\\d{4,5})(\\d{4})$",
                message = "O telefone deve ter um formato válido (ex: 11987654321 ou 5511987654321)"
        )
        String telefone,

        String email,

        @NotEmpty(message = "Informe ao menos um tipo de parceiro")
        Set<TipoParceiro> tipoParceiros,

        @NotNull(message = "O tipo de documento é obrigatório")
        TipoDocumento tipoDocumento,

        @Pattern(regexp = "^\\d{11}$", message = "O cpf deve conter 11 dígitos numéricos")
        String cpf,

        @Pattern(regexp = "^\\d{14}$", message = "O cnpj deve conter 14 dígitos numéricos")
        String cnpj,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        Endereco endereco
) {}
