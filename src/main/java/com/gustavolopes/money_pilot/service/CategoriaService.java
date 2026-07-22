package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.CategoriaRequestDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository repository;

    public Page<CategoriaResponseDTO> getAllCategorias(Pageable pagination) {
        return repository.findAll(pagination).map(CategoriaResponseDTO::new);
    }

    public CategoriaResponseDTO getCategoriaById(Long id) {
        Optional<Categoria> categoria = repository.findById(id);

        if (categoria.isEmpty())
            throw new CategoriaNotFoundException(id);

        return new CategoriaResponseDTO(categoria.get());
    }

    public CategoriaResponseDTO createCategoria(CategoriaRequestDTO categoriaRequestDTO) {
        Categoria categoria = new Categoria();

        categoria.setNome(categoriaRequestDTO.nome());
        categoria.setTagCor(categoriaRequestDTO.tagCor());
        categoria.setTipoCategoria(categoriaRequestDTO.tipoCategoria());

        Categoria categoriaSalva = repository.save(categoria);

        return new CategoriaResponseDTO(categoriaSalva);
    }

    public void deleteCategoria(Long id) {
        CategoriaResponseDTO categoriaResponseDTO = this.getCategoriaById(id);
        Categoria categoria = new Categoria();

        categoria.setId(categoriaResponseDTO.id());
        categoria.setNome(categoriaResponseDTO.nome());
        categoria.setTagCor(categoriaResponseDTO.tagCor());
        categoria.setTipoCategoria(categoriaResponseDTO.tipoCategoria());

        repository.delete(categoria);
    }

    public CategoriaResponseDTO updateCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO) {
        this.getCategoriaById(id);

        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(categoriaRequestDTO.nome());
        categoria.setTagCor(categoriaRequestDTO.tagCor());
        categoria.setTipoCategoria(categoriaRequestDTO.tipoCategoria());

        Categoria categoriaSalva = repository.save(categoria);

        return new CategoriaResponseDTO(categoriaSalva);
    }
}
