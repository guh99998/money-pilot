package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.model.TipoCategoria;

public record CategoriaResponseDTO(
        Long id,
        String nome,
        String tagCor,
        TipoCategoria tipoCategoria
) {
    public CategoriaResponseDTO(Categoria categoria) {
        this(
                categoria.getId(),
                categoria.getNome(),
                categoria.getTagCor(),
                categoria.getTipoCategoria()
        );
    }
}