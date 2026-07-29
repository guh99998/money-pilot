package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.BancoRequestDTO;
import com.gustavolopes.money_pilot.dto.BancoResponseDTO;
import com.gustavolopes.money_pilot.exception.BancoCodigoImutavelException;
import com.gustavolopes.money_pilot.exception.BancoEmUsoException;
import com.gustavolopes.money_pilot.exception.BancoNotFoundException;
import com.gustavolopes.money_pilot.model.Banco;
import com.gustavolopes.money_pilot.repository.BancoRepository;
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
public class BancoServiceTest {

    @Mock
    BancoRepository repository;

    @InjectMocks
    BancoService bancoService;

    private Banco criarBanco(Long id, String codigo, String nome) {
        Banco banco = new Banco();
        banco.setId(id);
        banco.setCodigoBanco(codigo);
        banco.setNomeBanco(nome);
        return banco;
    }

    private BancoRequestDTO criarRequestDTO(String codigo, String nome) {
        return new BancoRequestDTO(codigo, nome);
    }

    @Test
    public void deveRetornarPaginaDeBancosMapeados() {
        Banco banco = criarBanco(1L, "341", "Itaú Unibanco S.A.");
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(banco)));

        Page<BancoResponseDTO> resultado = bancoService.getAllBancos(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nomeBanco()).isEqualTo("Itaú Unibanco S.A.");
    }

    @Test
    public void deveRetornarBancoQuandoIdExiste() {
        Banco banco = criarBanco(1L, "341", "Itaú Unibanco S.A.");

        when(repository.findById(1L)).thenReturn(Optional.of(banco));

        BancoResponseDTO resultado = bancoService.getBancoById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.codigoBanco()).isEqualTo("341");
        assertThat(resultado.nomeBanco()).isEqualTo("Itaú Unibanco S.A.");
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bancoService.getBancoById(idInexistente))
                .isInstanceOf(BancoNotFoundException.class)
                .hasMessageContaining("não foi encontrado");
    }

    @Test
    public void deveCriarBanco() {
        BancoRequestDTO dto = criarRequestDTO("341", "Itaú Unibanco S.A.");
        Banco bancoSalvo = criarBanco(1L, "341", "Itaú Unibanco S.A.");

        when(repository.save(any(Banco.class))).thenReturn(bancoSalvo);

        BancoResponseDTO resultado = bancoService.createBanco(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.codigoBanco()).isEqualTo("341");
        assertThat(resultado.nomeBanco()).isEqualTo("Itaú Unibanco S.A.");

        ArgumentCaptor<Banco> captor = ArgumentCaptor.forClass(Banco.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCodigoBanco()).isEqualTo("341");
    }

    @Test
    public void deveAtualizarNomeDoBancoExistente() {
        Banco bancoExistente = criarBanco(1L, "341", "Itaú Unibanco S.A.");
        BancoRequestDTO dtoAtualizacao = criarRequestDTO("341", "Itaú Unibanco");

        when(repository.findById(1L)).thenReturn(Optional.of(bancoExistente));
        when(repository.save(any(Banco.class))).thenReturn(bancoExistente);

        BancoResponseDTO resultado = bancoService.updateBanco(1L, dtoAtualizacao);

        assertThat(resultado.id()).isEqualTo(1L);

        ArgumentCaptor<Banco> captor = ArgumentCaptor.forClass(Banco.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNomeBanco()).isEqualTo("Itaú Unibanco");
        assertThat(captor.getValue().getCodigoBanco()).isEqualTo("341");
    }

    @Test
    public void deveLancarExcecaoAoAtualizarComCodigoBancoAlterado() {
        Banco bancoExistente = criarBanco(1L, "341", "Itaú Unibanco S.A.");
        BancoRequestDTO dtoComCodigoAlterado = criarRequestDTO("001", "Itaú Unibanco S.A.");

        when(repository.findById(1L)).thenReturn(Optional.of(bancoExistente));

        assertThatThrownBy(() -> bancoService.updateBanco(1L, dtoComCodigoAlterado))
                .isInstanceOf(BancoCodigoImutavelException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        BancoRequestDTO dto = criarRequestDTO("341", "Itaú Unibanco S.A.");

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bancoService.updateBanco(idInexistente, dto))
                .isInstanceOf(BancoNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarBancoQuandoIdExiste() {
        Banco banco = criarBanco(1L, "341", "Itaú Unibanco S.A.");

        when(repository.findById(1L)).thenReturn(Optional.of(banco));

        bancoService.deleteBanco(1L);

        verify(repository).delete(banco);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bancoService.deleteBanco(idInexistente))
                .isInstanceOf(BancoNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    public void deveLancarBancoEmUsoQuandoBancoEstaAssociadoAUmaContaBancaria() {
        Banco banco = criarBanco(1L, "341", "Itaú Unibanco S.A.");

        when(repository.findById(1L)).thenReturn(Optional.of(banco));
        doThrow(new DataIntegrityViolationException("violação")).when(repository).delete(banco);

        assertThatThrownBy(() -> bancoService.deleteBanco(1L))
                .isInstanceOf(BancoEmUsoException.class);
    }
}
