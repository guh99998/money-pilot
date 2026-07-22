package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.TipoCategoria;

public record CategoriaResponseDTO(
        Long id,
        String nome,
        String tagCor,
        TipoCategoria tipoCategoria
) {
}
