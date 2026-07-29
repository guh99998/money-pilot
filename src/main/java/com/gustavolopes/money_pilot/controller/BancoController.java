package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.BancoRequestDTO;
import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.service.BancoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bancos")
public class BancoController {
    @Autowired
    private BancoService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<BancoResponseDTO> getAllBancos(Pageable pagination) {
        return service.getAllBancos(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BancoResponseDTO getBancoById(@PathVariable Long id) {
        return service.getBancoById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BancoResponseDTO createBanco(@Valid @RequestBody BancoRequestDTO dto) {
        return service.createBanco(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBanco(@PathVariable Long id) {
        service.deleteBanco(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BancoResponseDTO updateBanco(@PathVariable Long id, @Valid @RequestBody BancoRequestDTO dto) {
        return service.updateBanco(id, dto);
    }

}
