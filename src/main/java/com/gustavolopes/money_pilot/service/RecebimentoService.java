package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.RecebimentoRequestDTO;
import com.gustavolopes.money_pilot.dto.RecebimentoResponseDTO;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoConfirmadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoEstornadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNotFoundException;
import com.gustavolopes.money_pilot.exception.ReceitaNaoEncontradaNaListaException;
import com.gustavolopes.money_pilot.exception.ReceitaNotFoundException;
import com.gustavolopes.money_pilot.model.Recebimento;
import com.gustavolopes.money_pilot.model.Receita;
import com.gustavolopes.money_pilot.model.StatusRecebimento;
import com.gustavolopes.money_pilot.repository.RecebimentoRepository;
import com.gustavolopes.money_pilot.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecebimentoService {
    @Autowired
    private RecebimentoRepository repository;

    @Autowired
    private ReceitaService receitaService;

    @Autowired
    private ContaBancariaService contaBancariaService;
    @Autowired
    private ReceitaRepository receitaRepository;

    public Page<RecebimentoResponseDTO> getAllRecebimentos(Pageable pagination) {
        return repository.findAll(pagination).map(RecebimentoResponseDTO::new);
    }

    private Recebimento buscarRecebimentOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecebimentoNotFoundException(id));
    }

    public RecebimentoResponseDTO getRecebimentoById(Long id) {
        return new RecebimentoResponseDTO(buscarRecebimentOuLancarExcecao(id));
    }

    @Transactional
    public RecebimentoResponseDTO createRecebimento(RecebimentoRequestDTO dto) {
        List<Receita> receitas = receitaRepository.findAllById(dto.receitasId());

        if (receitas.size() != dto.receitasId().size()) {
            throw new ReceitaNaoEncontradaNaListaException();
        }

        Recebimento recebimento = new Recebimento();

        recebimento.setDataRecebimento(dto.dataRecbimento());
        recebimento.setValorFinal(dto.valorFinal());
        recebimento.setContaBancaria(contaBancariaService.buscarContaBancariaOuLancarExcecao(dto.contaBancariaId()));
        recebimento.setStatus(StatusRecebimento.CONFIRMADO);

        Recebimento salvo = repository.save(recebimento);

        receitas.forEach(r -> r.setRecebimento(salvo));
        receitaRepository.saveAll(receitas);

        contaBancariaService.creditarSaldo(salvo.getContaBancaria(), salvo.getValorFinal());

        salvo.setReceitas(receitas);
        return new RecebimentoResponseDTO(salvo);
    }

    @Transactional
    public RecebimentoResponseDTO estornarRecebimento(Long id) {
        Recebimento recebimento = buscarRecebimentOuLancarExcecao(id);

        if (recebimento.getStatus() != StatusRecebimento.CONFIRMADO) {
            throw new RecebimentoNaoConfirmadoException(id);
        }

        contaBancariaService.debitarSaldo(recebimento.getContaBancaria(), recebimento.getValorFinal());

        recebimento.setStatus(StatusRecebimento.ESTORNADO);
        Recebimento salvo = repository.save(recebimento);

        return new RecebimentoResponseDTO(salvo);
    }

    @Transactional
    public RecebimentoResponseDTO confirmarRecebimento(Long id) {
        Recebimento recebimento = buscarRecebimentOuLancarExcecao(id);

        if (recebimento.getStatus() != StatusRecebimento.ESTORNADO) {
            throw new RecebimentoNaoEstornadoException(id);
        }

        contaBancariaService.creditarSaldo(recebimento.getContaBancaria(), recebimento.getValorFinal());

        recebimento.setStatus(StatusRecebimento.CONFIRMADO);
        Recebimento salvo = repository.save(recebimento);

        return new RecebimentoResponseDTO(salvo);
    }

    @Transactional
    public void deleteRecebimento(Long id) {
        Recebimento recebimento = buscarRecebimentOuLancarExcecao(id);

        if (recebimento.getStatus() != StatusRecebimento.ESTORNADO) {
            throw new RecebimentoNaoEstornadoException(id);
        }

        List<Receita> receitas = recebimento.getReceitas();
        receitas.forEach(r -> r.setRecebimento(null));
        receitaRepository.saveAll(receitas);

        repository.delete(recebimento);
    }

    @Transactional
    public RecebimentoResponseDTO updateRecebimento(Long id, RecebimentoRequestDTO dto) {
        Recebimento recebimento = buscarRecebimentOuLancarExcecao(id);

        if (recebimento.getStatus() != StatusRecebimento.ESTORNADO) {
            throw new RecebimentoNaoEstornadoException(id);
        }

        List<Receita> novasReceitas = receitaRepository.findAllById(dto.receitasId());

        if (novasReceitas.size() != dto.receitasId().size()) {
            throw new ReceitaNaoEncontradaNaListaException();
        }

        List<Receita> receitasAntigas = recebimento.getReceitas();
        receitasAntigas.forEach(r -> r.setRecebimento(null));
        receitaRepository.saveAll(receitasAntigas);

        recebimento.setDataRecebimento(dto.dataRecbimento());
        recebimento.setValorFinal(dto.valorFinal());
        recebimento.setContaBancaria(contaBancariaService.buscarContaBancariaOuLancarExcecao(dto.contaBancariaId()));

        Recebimento salvo = repository.save(recebimento);

        novasReceitas.forEach(r -> r.setRecebimento(salvo));
        receitaRepository.saveAll(novasReceitas);

        salvo.setReceitas(novasReceitas);
        return new RecebimentoResponseDTO(salvo);
    }
}
