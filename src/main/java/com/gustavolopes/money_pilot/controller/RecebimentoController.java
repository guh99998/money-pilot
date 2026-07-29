package com.gustavolopes.money_pilot.controller;

import com.gustavolopes.money_pilot.dto.RecebimentoRequestDTO;
import com.gustavolopes.money_pilot.dto.RecebimentoResponseDTO;
import com.gustavolopes.money_pilot.service.RecebimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recebimentos")
public class RecebimentoController {
    @Autowired
    private RecebimentoService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecebimentoResponseDTO> getAllRecebimentos(Pageable pagination) {
        return service.getAllRecebimentos(pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecebimentoResponseDTO getRecebimentoById(@PathVariable Long id) {
        return service.getRecebimentoById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RecebimentoResponseDTO createRecebimento(@Valid @RequestBody RecebimentoRequestDTO dto) {
        return service.createRecebimento(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecebimentoResponseDTO updateRecebimento(@PathVariable Long id, @Valid @RequestBody RecebimentoRequestDTO dto) {
        return service.updateRecebimento(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecebimento(@PathVariable Long id) {
        service.deleteRecebimento(id);
    }

    @PatchMapping("/{id}/estornar")
    @ResponseStatus(HttpStatus.OK)
    public RecebimentoResponseDTO estornarRecebimento(@PathVariable Long id) {
        return service.estornarRecebimento(id);
    }

    @PatchMapping("/{id}/confirmar")
    @ResponseStatus(HttpStatus.OK)
    public RecebimentoResponseDTO confirmarRecebimento(@PathVariable Long id) {
        return service.confirmarRecebimento(id);
    }
}
