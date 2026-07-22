package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.CategoriaRequestDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    @Autowired
    private CategoriaService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<CategoriaResponseDTO> getAllCategorias(Pageable pagination) {
        return service.getAllCategorias(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoriaResponseDTO getCategoriaById(@PathVariable Long id) {
        return service.getCategoriaById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponseDTO createCategoria(@Valid @RequestBody CategoriaRequestDTO categoria) {
        return service.createCategoria(categoria);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoria(@PathVariable Long id) {
        service.deleteCategoria(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoriaResponseDTO updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO categoria) {
        return service.updateCategoria(id, categoria);
    }
}
