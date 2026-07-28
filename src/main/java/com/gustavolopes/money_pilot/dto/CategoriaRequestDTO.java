package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CategoriaRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome,

        @NotBlank(message = "A cor da tag é obrigatória")
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "A tag de cor deve ser um código Hexadecimal válido (ex: #FF5733)")
        String tagCor,

        @NotNull(message = "O tipo da Categoria é obrigatório")
        TipoCategoria tipoCategoria
) {}
