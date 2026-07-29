package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.ContaBancariaRequestDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaResponseDTO;
import com.gustavolopes.money_pilot.exception.ContaBancariaEmUsoException;
import com.gustavolopes.money_pilot.exception.ContaBancariaNotFoundException;
import com.gustavolopes.money_pilot.model.ContaBancaria;
import com.gustavolopes.money_pilot.repository.ContaBancariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ContaBancariaService {
    @Autowired
    private ContaBancariaRepository repository;

    @Autowired
    private BancoService bancoService;

    public Page<ContaBancariaResponseDTO> getAllContasBancarias(Pageable pagination) {
        return repository.findAll(pagination).map(ContaBancariaResponseDTO::new);
    }

    public ContaBancaria buscarContaBancariaOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ContaBancariaNotFoundException(id));
    }

    public ContaBancariaResponseDTO getContaBancariById(Long id) {
        return new ContaBancariaResponseDTO(buscarContaBancariaOuLancarExcecao(id));
    }

    public ContaBancariaResponseDTO createContaBancaria(ContaBancariaRequestDTO dto) {
        ContaBancaria contaBancaria = new ContaBancaria();

        contaBancaria.setBanco(bancoService.buscarBancoOuLancarExcecao(dto.bancoId()));
        contaBancaria.setNomeContaBancaria(dto.nomeContaBancaria());
        contaBancaria.setAgencia(dto.agencia());
        contaBancaria.setNumeroConta(dto.numeroConta());
        contaBancaria.setSaldoInicial(dto.saldoInicial());
        contaBancaria.setSaldoAtual(dto.saldoInicial());

        ContaBancaria salva = repository.save(contaBancaria);
        return new ContaBancariaResponseDTO(salva);
    }

    public void creditarSaldo(ContaBancaria contaBancaria, BigDecimal valor) {
        contaBancaria.setSaldoAtual(contaBancaria.getSaldoAtual().add(valor));
        repository.save(contaBancaria);
    }

    public void debitarSaldo(ContaBancaria contaBancaria, BigDecimal valor) {
        contaBancaria.setSaldoAtual(contaBancaria.getSaldoAtual().subtract(valor));
        repository.save(contaBancaria);
    }

    public void deleteContaBancaria(Long id) {
        ContaBancaria contaBancaria = buscarContaBancariaOuLancarExcecao(id);

        try {
            repository.delete(contaBancaria);
        } catch (DataIntegrityViolationException e) {
            throw new ContaBancariaEmUsoException(id);
        }
    }

    public ContaBancariaResponseDTO updateContaBancaria(Long id, ContaBancariaRequestDTO dto) {
        ContaBancaria contaBancaria = buscarContaBancariaOuLancarExcecao(id);

        contaBancaria.setBanco(bancoService.buscarBancoOuLancarExcecao(dto.bancoId()));
        contaBancaria.setNomeContaBancaria(dto.nomeContaBancaria());
        contaBancaria.setAgencia(dto.agencia());
        contaBancaria.setNumeroConta(dto.numeroConta());
        contaBancaria.setSaldoInicial(dto.saldoInicial());

        ContaBancaria salva = repository.save(contaBancaria);

        return new ContaBancariaResponseDTO(salva);
    }
}
