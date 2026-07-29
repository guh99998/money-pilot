package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.ReceitaRequestDTO;
import com.gustavolopes.money_pilot.dto.ReceitaResponseDTO;
import com.gustavolopes.money_pilot.service.ReceitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/receitas")
public class ReceitaController {
    @Autowired
    private ReceitaService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReceitaResponseDTO> getAllReceitas(Pageable pagination) {
        return service.getAllReceitas(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReceitaResponseDTO getReceitaById(@PathVariable Long id) {
        return service.getReceitaById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ReceitaResponseDTO createReceita(@Valid @RequestBody ReceitaRequestDTO receita) {
        return service.createReceita(receita);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReceita(@PathVariable Long id) {
        service.deleteReceita(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReceitaResponseDTO updateReceita(@PathVariable Long id, @Valid @RequestBody ReceitaRequestDTO receita) {
        return service.updateReceita(id, receita);
    }
}
