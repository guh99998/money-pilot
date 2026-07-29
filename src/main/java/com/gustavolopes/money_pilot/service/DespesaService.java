package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.DespesaRequestDTO;
import com.gustavolopes.money_pilot.dto.DespesaResponseDTO;
import com.gustavolopes.money_pilot.exception.DespesaNotFoundException;
import com.gustavolopes.money_pilot.model.Despesa;
import com.gustavolopes.money_pilot.repository.DespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DespesaService {
    @Autowired
    private DespesaRepository repository;

    @Autowired
    private ParceiroService parceiroService;

    @Autowired
    private CategoriaService categoriaService;

    public Page<DespesaResponseDTO> getAllDespesas(Pageable pagination) {
        return repository.findAll(pagination).map(DespesaResponseDTO::new);
    }

    private Despesa buscarDespesaOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DespesaNotFoundException(id));
    }

    public DespesaResponseDTO getDespesaById(Long id) {
        return new DespesaResponseDTO(buscarDespesaOuLancarExcecao(id));
    }

    public DespesaResponseDTO createDespesa(DespesaRequestDTO dto) {
        Despesa despesa = new Despesa();

        despesa.setNomeDespesa(dto.nomeDespesa());
        despesa.setParceiro(parceiroService.buscarParceiroOuLancarExcecao(dto.parceiroId()));
        despesa.setDataVencimento(dto.dataVencimento());
        despesa.setNumeroParcelas(dto.numeroParcelas());
        despesa.setValorParcela(dto.valorParcela());
        despesa.setCategoria(categoriaService.buscarCategoriaOuLancarExcecao(dto.categoriaId()));
        despesa.setObservacoes(dto.observacoes());

        Despesa salva = repository.save(despesa);
        return new DespesaResponseDTO(salva);
    }

    public void deleteDespesa(Long id) {
        Despesa despesa = buscarDespesaOuLancarExcecao(id);

        repository.delete(despesa);
    }

    public DespesaResponseDTO updateDespesa(Long id, DespesaRequestDTO dto) {
        Despesa despesa = buscarDespesaOuLancarExcecao(id);

        despesa.setNomeDespesa(dto.nomeDespesa());
        despesa.setParceiro(parceiroService.buscarParceiroOuLancarExcecao(dto.parceiroId()));
        despesa.setDataVencimento(dto.dataVencimento());
        despesa.setNumeroParcelas(dto.numeroParcelas());
        despesa.setValorParcela(dto.valorParcela());
        despesa.setCategoria(categoriaService.buscarCategoriaOuLancarExcecao(dto.categoriaId()));
        despesa.setObservacoes(dto.observacoes());

        Despesa salva = repository.save(despesa);

        return new DespesaResponseDTO(salva);
    }
}
