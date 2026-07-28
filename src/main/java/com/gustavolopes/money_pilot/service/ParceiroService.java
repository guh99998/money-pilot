package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.ParceiroRequestDTO;
import com.gustavolopes.money_pilot.dto.ParceiroResponseDTO;
import com.gustavolopes.money_pilot.exception.DocumentoImutavelException;
import com.gustavolopes.money_pilot.exception.DocumentoInvalidoException;
import com.gustavolopes.money_pilot.exception.ParceiroEmUsoException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.Parceiro;
import com.gustavolopes.money_pilot.model.ParceiroPF;
import com.gustavolopes.money_pilot.model.ParceiroPJ;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.repository.ParceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ParceiroService {

        @Autowired
        private ParceiroRepository repository;

        public ParceiroResponseDTO createParceiro(ParceiroRequestDTO dto) {
            validarDocumentoInformado(dto);

            Parceiro parceiro = switch (dto.tipoDocumento()) {
                case CPF -> {
                    ParceiroPF pf = new ParceiroPF();
                    pf.setCpf(dto.cpf());
                    yield pf;
                }
                case CNPJ -> {
                    ParceiroPJ pj = new ParceiroPJ();
                    pj.setCnpj(dto.cnpj());
                    yield pj;
                }
            };

            parceiro.setNome(dto.nome());
            parceiro.setTelefone(dto.telefone());
            parceiro.setEmail(dto.email());
            parceiro.setTipoParceiros(dto.tipoParceiros());

            Endereco endereco = new Endereco();
            aplicarEndereco(endereco, dto.endereco());
            parceiro.setEndereco(endereco);

            Parceiro salvo = repository.save(parceiro);
            return new ParceiroResponseDTO(salvo);
        }

        public Page<ParceiroResponseDTO> getAllParceiros(Pageable pagination) {
            return repository.findAll(pagination).map(ParceiroResponseDTO::new);
        }

        public Parceiro buscarParceiroOuLancarExcecao(Long id) {
            return repository.findById(id).orElseThrow(() -> new ParceiroNotFoundException(id));
        }

        public ParceiroResponseDTO getParceiroById(Long id) {
            return new ParceiroResponseDTO(buscarParceiroOuLancarExcecao(id));
        }

        public ParceiroResponseDTO updateParceiro(Long id, ParceiroRequestDTO dto) {
            Parceiro parceiro = buscarParceiroOuLancarExcecao(id);

            validarDocumentoImutavel(parceiro, dto);

            parceiro.setNome(dto.nome());
            parceiro.setTelefone(dto.telefone());
            parceiro.setEmail(dto.email());
            parceiro.setTipoParceiros(dto.tipoParceiros());
            aplicarEndereco(parceiro.getEndereco(), dto.endereco());

            Parceiro salvo = repository.save(parceiro);
            return new ParceiroResponseDTO(salvo);
        }

        private void aplicarEndereco(Endereco destino, Endereco origem) {
            destino.setRua(origem.getRua());
            destino.setNumero(origem.getNumero());
            destino.setBairro(origem.getBairro());
            destino.setCep(origem.getCep());
            destino.setCidade(origem.getCidade());
            destino.setUf(origem.getUf());
        }

        private void validarDocumentoInformado(ParceiroRequestDTO dto) {
            boolean invalido = switch (dto.tipoDocumento()) {
                case CPF -> dto.cpf() == null || dto.cpf().isBlank();
                case CNPJ -> dto.cnpj() == null || dto.cnpj().isBlank();
            };

            if (invalido) {
                throw new DocumentoInvalidoException(dto.tipoDocumento());
            }
        }

        private void validarDocumentoImutavel(Parceiro parceiro, ParceiroRequestDTO dto) {
            boolean documentoAlterado = switch (parceiro) {
                case ParceiroPF pf -> dto.tipoDocumento() != TipoDocumento.CPF
                        || !pf.getCpf().equals(dto.cpf());
                case ParceiroPJ pj -> dto.tipoDocumento() != TipoDocumento.CNPJ
                        || !pj.getCnpj().equals(dto.cnpj());
                default -> false;
            };

            if (documentoAlterado) {
                throw new DocumentoImutavelException(parceiro.getId());
            }
        }

        public void deleteParceiro(Long id) {
            Parceiro parceiro = buscarParceiroOuLancarExcecao(id);
            try {
                repository.delete(parceiro);
            } catch (DataIntegrityViolationException e) {
                throw new ParceiroEmUsoException(id);
            }
        }
}
