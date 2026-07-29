package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.DespesaRequestDTO;
import com.gustavolopes.money_pilot.dto.DespesaResponseDTO;
import com.gustavolopes.money_pilot.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/despesas")
public class DespesaController {
    @Autowired
    private DespesaService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<DespesaResponseDTO> getAllDespesas(Pageable pagination) {
        return service.getAllDespesas(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DespesaResponseDTO getDespesaById(@PathVariable Long id) {
        return service.getDespesaById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public DespesaResponseDTO createDespesa(@Valid @RequestBody DespesaRequestDTO despesa) {
        return service.createDespesa(despesa);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDespesa(@PathVariable Long id) {
        service.deleteDespesa(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DespesaResponseDTO updateDespesa(@PathVariable Long id, @Valid @RequestBody DespesaRequestDTO despesa) {
        return service.updateDespesa(id, despesa);
    }
}
