package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.ParceiroRequestDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.service.ParceiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parceiros")
public class ParceiroController {
    @Autowired
    private ParceiroService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<ParceiroResponseDTO> getAllParceiros(Pageable pagination) {
        return service.getAllParceiros(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParceiroResponseDTO getParceiroById(@PathVariable Long id) {
        return service.getParceiroById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ParceiroResponseDTO createParceiro(@Valid @RequestBody ParceiroRequestDTO parceiro) {
        return service.createParceiro(parceiro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParceiro(@PathVariable Long id) {
        service.deleteParceiro(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParceiroResponseDTO updateParceiro(@PathVariable Long id, @Valid @RequestBody ParceiroRequestDTO parceiro) {
        return service.updateParceiro(id, parceiro);
    }
}
