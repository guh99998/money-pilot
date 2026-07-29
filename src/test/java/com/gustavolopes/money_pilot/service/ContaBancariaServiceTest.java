package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.ContaBancariaRequestDTO;
import com.gustavolopes.money_pilot.dto.ContaBancariaResponseDTO;
import com.gustavolopes.money_pilot.exception.BancoNotFoundException;
import com.gustavolopes.money_pilot.exception.ContaBancariaEmUsoException;
import com.gustavolopes.money_pilot.exception.ContaBancariaNotFoundException;
import com.gustavolopes.money_pilot.model.Banco;
import com.gustavolopes.money_pilot.model.ContaBancaria;
import com.gustavolopes.money_pilot.repository.ContaBancariaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContaBancariaServiceTest {

    @Mock
    ContaBancariaRepository repository;

    @Mock
    BancoService bancoService;

    @InjectMocks
    ContaBancariaService contaBancariaService;

    private Banco criarBanco(Long id) {
        Banco banco = new Banco();
        banco.setId(id);
        banco.setCodigoBanco("341");
        banco.setNomeBanco("Itaú Unibanco S.A.");
        return banco;
    }

    private ContaBancaria criarContaBancaria(Long id, Banco banco) {
        ContaBancaria contaBancaria = new ContaBancaria();
        contaBancaria.setId(id);
        contaBancaria.setBanco(banco);
        contaBancaria.setNomeContaBancaria("Conta Corrente");
        contaBancaria.setAgencia("0001");
        contaBancaria.setNumeroConta("123456-7");
        contaBancaria.setSaldoInicial(BigDecimal.valueOf(1000));
        contaBancaria.setSaldoAtual(BigDecimal.valueOf(1000));
        return contaBancaria;
    }

    private ContaBancariaRequestDTO criarRequestDTO(Long bancoId) {
        return new ContaBancariaRequestDTO(bancoId, "Conta Corrente", "0001", "123456-7", BigDecimal.valueOf(1000));
    }

    @Test
    public void deveRetornarPaginaDeContasBancariasMapeadas() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(contaBancaria)));

        Page<ContaBancariaResponseDTO> resultado = contaBancariaService.getAllContasBancarias(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nomeContaBancaria()).isEqualTo("Conta Corrente");
    }

    @Test
    public void deveRetornarContaBancariaQuandoIdExiste() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);

        when(repository.findById(1L)).thenReturn(Optional.of(contaBancaria));

        ContaBancariaResponseDTO resultado = contaBancariaService.getContaBancariById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeContaBancaria()).isEqualTo("Conta Corrente");
        assertThat(resultado.banco().id()).isEqualTo(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaBancariaService.getContaBancariById(idInexistente))
                .isInstanceOf(ContaBancariaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");
    }

    @Test
    public void deveCriarContaBancaria() {
        Banco banco = criarBanco(1L);
        ContaBancariaRequestDTO dto = criarRequestDTO(1L);
        ContaBancaria contaBancariaSalva = criarContaBancaria(1L, banco);

        when(bancoService.buscarBancoOuLancarExcecao(1L)).thenReturn(banco);
        when(repository.save(any(ContaBancaria.class))).thenReturn(contaBancariaSalva);

        ContaBancariaResponseDTO resultado = contaBancariaService.createContaBancaria(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeContaBancaria()).isEqualTo("Conta Corrente");

        ArgumentCaptor<ContaBancaria> captor = ArgumentCaptor.forClass(ContaBancaria.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getBanco()).isEqualTo(banco);
        assertThat(captor.getValue().getSaldoInicial()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(captor.getValue().getSaldoAtual()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void deveLancarExcecaoAoCriarContaBancariaComBancoInexistente() {
        ContaBancariaRequestDTO dto = criarRequestDTO(999L);

        when(bancoService.buscarBancoOuLancarExcecao(999L))
                .thenThrow(new BancoNotFoundException(999L));

        assertThatThrownBy(() -> contaBancariaService.createContaBancaria(dto))
                .isInstanceOf(BancoNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveAtualizarContaBancariaExistente() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancariaSalva = criarContaBancaria(1L, banco);
        ContaBancariaRequestDTO dtoAtualizacao = new ContaBancariaRequestDTO(
                1L, "Conta Poupança", "0002", "765432-1", BigDecimal.valueOf(2000)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(contaBancariaSalva));
        when(bancoService.buscarBancoOuLancarExcecao(1L)).thenReturn(banco);
        when(repository.save(any(ContaBancaria.class))).thenReturn(contaBancariaSalva);

        ContaBancariaResponseDTO resultado = contaBancariaService.updateContaBancaria(1L, dtoAtualizacao);

        assertThat(resultado.id()).isEqualTo(1L);

        ArgumentCaptor<ContaBancaria> captor = ArgumentCaptor.forClass(ContaBancaria.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNomeContaBancaria()).isEqualTo("Conta Poupança");
        assertThat(captor.getValue().getAgencia()).isEqualTo("0002");
        assertThat(captor.getValue().getNumeroConta()).isEqualTo("765432-1");
        assertThat(captor.getValue().getSaldoInicial()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        ContaBancariaRequestDTO dto = criarRequestDTO(1L);

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaBancariaService.updateContaBancaria(idInexistente, dto))
                .isInstanceOf(ContaBancariaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarContaBancariaQuandoIdExiste() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);

        when(repository.findById(1L)).thenReturn(Optional.of(contaBancaria));

        contaBancariaService.deleteContaBancaria(1L);

        verify(repository).delete(contaBancaria);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contaBancariaService.deleteContaBancaria(idInexistente))
                .isInstanceOf(ContaBancariaNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    public void deveLancarContaBancariaEmUsoQuandoContaEstaAssociadaAReceitasOuDespesas() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);

        when(repository.findById(1L)).thenReturn(Optional.of(contaBancaria));
        doThrow(new DataIntegrityViolationException("violação")).when(repository).delete(contaBancaria);

        assertThatThrownBy(() -> contaBancariaService.deleteContaBancaria(1L))
                .isInstanceOf(ContaBancariaEmUsoException.class);
    }

    @Test
    public void deveCreditarSaldoDaContaBancaria() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);
        contaBancaria.setSaldoAtual(BigDecimal.valueOf(1000));

        contaBancariaService.creditarSaldo(contaBancaria, BigDecimal.valueOf(500));

        assertThat(contaBancaria.getSaldoAtual()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        verify(repository).save(contaBancaria);
    }

    @Test
    public void deveDebitarSaldoDaContaBancaria() {
        Banco banco = criarBanco(1L);
        ContaBancaria contaBancaria = criarContaBancaria(1L, banco);
        contaBancaria.setSaldoAtual(BigDecimal.valueOf(1000));

        contaBancariaService.debitarSaldo(contaBancaria, BigDecimal.valueOf(300));

        assertThat(contaBancaria.getSaldoAtual()).isEqualByComparingTo(BigDecimal.valueOf(700));
        verify(repository).save(contaBancaria);
    }
}
