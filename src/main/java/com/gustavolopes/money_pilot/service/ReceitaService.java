package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.ReceitaRequestDTO;
import com.gustavolopes.money_pilot.dto.ReceitaResponseDTO;
import com.gustavolopes.money_pilot.exception.ReceitaNotFoundException;
import com.gustavolopes.money_pilot.model.Receita;
import com.gustavolopes.money_pilot.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReceitaService {
    @Autowired
    private ReceitaRepository repository;

    @Autowired
    private ParceiroService parceiroService;

    @Autowired
    private CategoriaService categoriaService;

    public Page<ReceitaResponseDTO> getAllReceitas(Pageable pagination) {
        return repository.findAll(pagination).map(ReceitaResponseDTO::new);
    }

    private Receita buscarReceitaOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReceitaNotFoundException(id));
    }

    public ReceitaResponseDTO getReceitaById(Long id) {
        return new ReceitaResponseDTO(buscarReceitaOuLancarExcecao(id));
    }

    public ReceitaResponseDTO createReceita(ReceitaRequestDTO dto) {
        Receita receita = new Receita();

        receita.setNomeReceita(dto.nomeReceita());
        receita.setParceiro(parceiroService.buscarParceiroOuLancarExcecao(dto.parceiroId()));
        receita.setDataVencimento(dto.dataVencimento());
        receita.setNumeroParcelas(dto.numeroParcelas());
        receita.setValorParcela(dto.valorParcela());
        receita.setCategoria(categoriaService.buscarCategoriaOuLancarExcecao(dto.categoriaId()));
        receita.setObservacoes(dto.observacoes());

        Receita salva = repository.save(receita);
        return new ReceitaResponseDTO(salva);
    }

    public void deleteReceita(Long id) {
        Receita receita = buscarReceitaOuLancarExcecao(id);

        repository.delete(receita);
    }

    public ReceitaResponseDTO updateReceita(Long id, ReceitaRequestDTO dto) {
        Receita receita = buscarReceitaOuLancarExcecao(id);

        receita.setNomeReceita(dto.nomeReceita());
        receita.setParceiro(parceiroService.buscarParceiroOuLancarExcecao(dto.parceiroId()));
        receita.setDataVencimento(dto.dataVencimento());
        receita.setNumeroParcelas(dto.numeroParcelas());
        receita.setValorParcela(dto.valorParcela());
        receita.setCategoria(categoriaService.buscarCategoriaOuLancarExcecao(dto.categoriaId()));
        receita.setObservacoes(dto.observacoes());

        Receita salva = repository.save(receita);

        return new ReceitaResponseDTO(salva);
    }

}
