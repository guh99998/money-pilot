package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.RecebimentoRequestDTO;
import com.gustavolopes.money_pilot.dto.RecebimentoResponseDTO;
import com.gustavolopes.money_pilot.exception.ContaBancariaNotFoundException;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoConfirmadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNaoEstornadoException;
import com.gustavolopes.money_pilot.exception.RecebimentoNotFoundException;
import com.gustavolopes.money_pilot.exception.ReceitaNaoEncontradaNaListaException;
import com.gustavolopes.money_pilot.model.Banco;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.model.ContaBancaria;
import com.gustavolopes.money_pilot.model.ParceiroPF;
import com.gustavolopes.money_pilot.model.Recebimento;
import com.gustavolopes.money_pilot.model.Receita;
import com.gustavolopes.money_pilot.model.StatusRecebimento;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.repository.RecebimentoRepository;
import com.gustavolopes.money_pilot.repository.ReceitaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecebimentoServiceTest {

    @Mock
    RecebimentoRepository repository;

    @Mock
    ReceitaRepository receitaRepository;

    @Mock
    ReceitaService receitaService;

    @Mock
    ContaBancariaService contaBancariaService;

    @InjectMocks
    RecebimentoService recebimentoService;

    private ContaBancaria criarContaBancaria(Long id) {
        Banco banco = new Banco();
        banco.setId(1L);
        banco.setCodigoBanco("341");
        banco.setNomeBanco("Itaú Unibanco S.A.");

        ContaBancaria contaBancaria = new ContaBancaria();
        contaBancaria.setId(id);
        contaBancaria.setBanco(banco);
        contaBancaria.setSaldoInicial(BigDecimal.valueOf(1000));
        contaBancaria.setSaldoAtual(BigDecimal.valueOf(1000));
        return contaBancaria;
    }

    private Receita criarReceita(Long id) {
        ParceiroPF parceiro = new ParceiroPF();
        parceiro.setId(1L);
        parceiro.setNome("João");
        parceiro.setCpf("12345678900");

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Salário");
        categoria.setTagCor("#FFFFFF");
        categoria.setTipoCategoria(TipoCategoria.RECEITA);

        Receita receita = new Receita();
        receita.setId(id);
        receita.setNomeReceita("Salário");
        receita.setParceiro(parceiro);
        receita.setCategoria(categoria);
        receita.setObservacoes("Pagamento mensal");
        return receita;
    }

    private Recebimento criarRecebimento(Long id, StatusRecebimento status, ContaBancaria contaBancaria, List<Receita> receitas) {
        Recebimento recebimento = new Recebimento();
        recebimento.setId(id);
        recebimento.setDataRecebimento(LocalDate.now());
        recebimento.setValorFinal(BigDecimal.valueOf(1000));
        recebimento.setContaBancaria(contaBancaria);
        recebimento.setStatus(status);
        receitas.forEach(r -> r.setRecebimento(recebimento));
        recebimento.setReceitas(receitas);
        return recebimento;
    }

    private RecebimentoRequestDTO criarRequestDTO(List<Long> receitasId, Long contaBancariaId) {
        return new RecebimentoRequestDTO(receitasId, LocalDate.now(), BigDecimal.valueOf(1000), contaBancariaId);
    }

    @Test
    public void deveRetornarPaginaDeRecebimentosMapeados() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of(receita));
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(recebimento)));

        Page<RecebimentoResponseDTO> resultado = recebimentoService.getAllRecebimentos(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(1L);
    }

    @Test
    public void deveRetornarRecebimentoQuandoIdExiste() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of(receita));

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        RecebimentoResponseDTO resultado = recebimentoService.getRecebimentoById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(StatusRecebimento.CONFIRMADO);
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recebimentoService.getRecebimentoById(999L))
                .isInstanceOf(RecebimentoNotFoundException.class);
    }

    @Test
    public void deveConfirmarRecebimentoVinculandoReceitasECreditandoSaldo() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita1 = criarReceita(1L);
        Receita receita2 = criarReceita(2L);
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L, 2L), 1L);

        Recebimento recebimentoSalvo = new Recebimento();
        recebimentoSalvo.setId(10L);
        recebimentoSalvo.setContaBancaria(contaBancaria);
        recebimentoSalvo.setValorFinal(dto.valorFinal());

        when(receitaRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(receita1, receita2));
        when(contaBancariaService.buscarContaBancariaOuLancarExcecao(1L)).thenReturn(contaBancaria);
        when(repository.save(any(Recebimento.class))).thenReturn(recebimentoSalvo);

        RecebimentoResponseDTO resultado = recebimentoService.createRecebimento(dto);

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.receitas()).hasSize(2);

        ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusRecebimento.CONFIRMADO);

        assertThat(receita1.getRecebimento()).isEqualTo(recebimentoSalvo);
        assertThat(receita2.getRecebimento()).isEqualTo(recebimentoSalvo);
        verify(receitaRepository).saveAll(List.of(receita1, receita2));
        verify(contaBancariaService).creditarSaldo(contaBancaria, dto.valorFinal());
    }

    @Test
    public void deveLancarExcecaoAoConfirmarRecebimentoComReceitaInexistente() {
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L, 2L), 1L);

        when(receitaRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(criarReceita(1L)));

        assertThatThrownBy(() -> recebimentoService.createRecebimento(dto))
                .isInstanceOf(ReceitaNaoEncontradaNaListaException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoConfirmarRecebimentoComContaBancariaInexistente() {
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L), 999L);

        when(receitaRepository.findAllById(List.of(1L))).thenReturn(List.of(criarReceita(1L)));
        when(contaBancariaService.buscarContaBancariaOuLancarExcecao(999L))
                .thenThrow(new ContaBancariaNotFoundException(999L));

        assertThatThrownBy(() -> recebimentoService.createRecebimento(dto))
                .isInstanceOf(ContaBancariaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveEstornarRecebimentoConfirmadoDebitandoSaldoESemDesvincularReceitas() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of(receita));

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));
        when(repository.save(any(Recebimento.class))).thenReturn(recebimento);

        RecebimentoResponseDTO resultado = recebimentoService.estornarRecebimento(1L);

        assertThat(resultado.status()).isEqualTo(StatusRecebimento.ESTORNADO);
        assertThat(receita.getRecebimento()).isEqualTo(recebimento);
        verify(contaBancariaService).debitarSaldo(contaBancaria, recebimento.getValorFinal());
    }

    @Test
    public void deveLancarExcecaoAoEstornarRecebimentoJaEstornado() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.ESTORNADO, contaBancaria, List.of());

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        assertThatThrownBy(() -> recebimentoService.estornarRecebimento(1L))
                .isInstanceOf(RecebimentoNaoConfirmadoException.class);

        verify(contaBancariaService, never()).debitarSaldo(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoEstornarRecebimentoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recebimentoService.estornarRecebimento(999L))
                .isInstanceOf(RecebimentoNotFoundException.class);
    }

    @Test
    public void deveConfirmarNovamenteRecebimentoEstornadoCreditandoSaldo() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.ESTORNADO, contaBancaria, List.of(receita));

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));
        when(repository.save(any(Recebimento.class))).thenReturn(recebimento);

        RecebimentoResponseDTO resultado = recebimentoService.confirmarRecebimento(1L);

        assertThat(resultado.status()).isEqualTo(StatusRecebimento.CONFIRMADO);
        verify(contaBancariaService).creditarSaldo(contaBancaria, recebimento.getValorFinal());
    }

    @Test
    public void deveLancarExcecaoAoConfirmarRecebimentoJaConfirmado() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of());

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        assertThatThrownBy(() -> recebimentoService.confirmarRecebimento(1L))
                .isInstanceOf(RecebimentoNaoEstornadoException.class);

        verify(contaBancariaService, never()).creditarSaldo(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoConfirmarRecebimentoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recebimentoService.confirmarRecebimento(999L))
                .isInstanceOf(RecebimentoNotFoundException.class);
    }

    @Test
    public void deveExcluirRecebimentoEstornadoDesvinculandoReceitas() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.ESTORNADO, contaBancaria, List.of(receita));

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        recebimentoService.deleteRecebimento(1L);

        assertThat(receita.getRecebimento()).isNull();
        verify(receitaRepository).saveAll(List.of(receita));
        verify(repository).delete(recebimento);
    }

    @Test
    public void deveLancarExcecaoAoExcluirRecebimentoConfirmado() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of(receita));

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        assertThatThrownBy(() -> recebimentoService.deleteRecebimento(1L))
                .isInstanceOf(RecebimentoNaoEstornadoException.class);

        assertThat(receita.getRecebimento()).isEqualTo(recebimento);
        verify(repository, never()).delete(any());
    }

    @Test
    public void deveLancarExcecaoAoExcluirRecebimentoInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recebimentoService.deleteRecebimento(999L))
                .isInstanceOf(RecebimentoNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    public void deveAtualizarRecebimentoEstornadoTrocandoReceitasEDados() {
        ContaBancaria contaBancariaAntiga = criarContaBancaria(1L);
        ContaBancaria contaBancariaNova = criarContaBancaria(2L);
        Receita receitaAntiga = criarReceita(1L);
        Receita receitaNova = criarReceita(2L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.ESTORNADO, contaBancariaAntiga, List.of(receitaAntiga));
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(2L), 2L);

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));
        when(receitaRepository.findAllById(List.of(2L))).thenReturn(List.of(receitaNova));
        when(contaBancariaService.buscarContaBancariaOuLancarExcecao(2L)).thenReturn(contaBancariaNova);
        when(repository.save(any(Recebimento.class))).thenReturn(recebimento);

        RecebimentoResponseDTO resultado = recebimentoService.updateRecebimento(1L, dto);

        assertThat(resultado.receitas()).hasSize(1);
        assertThat(receitaAntiga.getRecebimento()).isNull();
        assertThat(receitaNova.getRecebimento()).isEqualTo(recebimento);
        assertThat(recebimento.getContaBancaria()).isEqualTo(contaBancariaNova);
    }

    @Test
    public void deveLancarExcecaoAoAtualizarRecebimentoConfirmado() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Receita receita = criarReceita(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.CONFIRMADO, contaBancaria, List.of(receita));
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L), 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));

        assertThatThrownBy(() -> recebimentoService.updateRecebimento(1L, dto))
                .isInstanceOf(RecebimentoNaoEstornadoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarRecebimentoComReceitaInexistente() {
        ContaBancaria contaBancaria = criarContaBancaria(1L);
        Recebimento recebimento = criarRecebimento(1L, StatusRecebimento.ESTORNADO, contaBancaria, List.of());
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L, 2L), 1L);

        when(repository.findById(1L)).thenReturn(Optional.of(recebimento));
        when(receitaRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(criarReceita(1L)));

        assertThatThrownBy(() -> recebimentoService.updateRecebimento(1L, dto))
                .isInstanceOf(ReceitaNaoEncontradaNaListaException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarRecebimentoInexistente() {
        RecebimentoRequestDTO dto = criarRequestDTO(List.of(1L), 1L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recebimentoService.updateRecebimento(999L, dto))
                .isInstanceOf(RecebimentoNotFoundException.class);

        verify(repository, never()).save(any());
    }
}
