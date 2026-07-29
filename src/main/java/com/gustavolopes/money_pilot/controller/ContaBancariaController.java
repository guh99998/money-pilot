package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.ContaBancariaRequestDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaResponseDTO;
import com.gustavolopes.money_pilot.service.ContaBancariaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contasbancarias")
public class ContaBancariaController {
    @Autowired
    private ContaBancariaService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<ContaBancariaResponseDTO> getAllContasBancarias(Pageable pagination) {
        return service.getAllContasBancarias(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContaBancariaResponseDTO getContaBancariaById(@PathVariable Long id) {
        return service.getContaBancariById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ContaBancariaResponseDTO createContaBancaria(@Valid @RequestBody ContaBancariaRequestDTO dto) {
        return service.createContaBancaria(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContaBancaria(@PathVariable Long id) {
        service.deleteContaBancaria(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContaBancariaResponseDTO updateContaBancaria(@PathVariable Long id, @Valid @RequestBody ContaBancariaRequestDTO dto) {
        return service.updateContaBancaria(id, dto);
    }


}
