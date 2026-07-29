package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.BancoRequestDTO;
import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.exception.BancoCodigoImutavelException;
import com.gustavolopes.money_pilot.exception.BancoEmUsoException;
import com.gustavolopes.money_pilot.exception.BancoNotFoundException;
import com.gustavolopes.money_pilot.model.Banco;
import com.gustavolopes.money_pilot.repository.BancoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BancoService {
    @Autowired
    private BancoRepository repository;

    public Page<BancoResponseDTO> getAllBancos(Pageable pagination) {
        return repository.findAll(pagination).map(BancoResponseDTO::new);
    }

    public Banco buscarBancoOuLancarExcecao(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BancoNotFoundException(id));
    }

    public BancoResponseDTO getBancoById(Long id) {
        return new BancoResponseDTO(buscarBancoOuLancarExcecao(id));
    }

    public BancoResponseDTO createBanco(BancoRequestDTO dto) {
        Banco banco = new Banco();

        banco.setCodigoBanco(dto.codigoBanco());
        banco.setNomeBanco(dto.nomeBanco());

        Banco salvo = repository.save(banco);

        return new BancoResponseDTO(salvo);
    }

    public void deleteBanco(Long id) {
        Banco banco = buscarBancoOuLancarExcecao(id);
        try {
            repository.delete(banco);
        } catch (DataIntegrityViolationException e) {
            throw new BancoEmUsoException(id);
        }
    }

    public BancoResponseDTO updateBanco(Long id, BancoRequestDTO dto) {
        Banco banco = buscarBancoOuLancarExcecao(id);

        validarCodigoImutavel(banco, dto);

        banco.setNomeBanco(dto.nomeBanco());

        Banco salvo = repository.save(banco);

        return new BancoResponseDTO(salvo);
    }

    private void validarCodigoImutavel(Banco banco, BancoRequestDTO dto) {
        if (!banco.getCodigoBanco().equals(dto.codigoBanco())) {
            throw new BancoCodigoImutavelException(banco.getId());
        }
    }

}
