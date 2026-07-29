package com.gustavolopes.money_pilot.dto;

import jakarta.validation.constraints.NotBlank;

public record BancoRequestDTO(
        @NotBlank(message = "O código do banco é obrigatório")
        String codigoBanco,

        @NotBlank(message = "O nome do banco é obrigatório")
        String nomeBanco
) {
}
