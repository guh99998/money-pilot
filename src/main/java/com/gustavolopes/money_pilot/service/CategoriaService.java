package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.CategoriaRequestDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaEmUsoException;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository repository;

    public Page<CategoriaResponseDTO> getAllCategorias(Pageable pagination) {
        return repository.findAll(pagination).map(CategoriaResponseDTO::new);
    }

    private Categoria buscarCategoriaOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    public CategoriaResponseDTO getCategoriaById(Long id) {
        return new CategoriaResponseDTO(buscarCategoriaOuLancarExcecao(id));
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
        Categoria categoria = buscarCategoriaOuLancarExcecao(id);
        try {
            repository.delete(categoria);
        } catch (DataIntegrityViolationException e) {
            throw new CategoriaEmUsoException(id);
        }
    }

    public CategoriaResponseDTO updateCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO) {
        Categoria categoria = buscarCategoriaOuLancarExcecao(id);

        categoria.setNome(categoriaRequestDTO.nome());
        categoria.setTagCor(categoriaRequestDTO.tagCor());
        categoria.setTipoCategoria(categoriaRequestDTO.tipoCategoria());

        Categoria categoriaSalva = repository.save(categoria);
        return new CategoriaResponseDTO(categoriaSalva);
    }

}
