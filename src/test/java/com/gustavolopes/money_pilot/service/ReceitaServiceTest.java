package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.ReceitaRequestDTO;
import com.gustavolopes.money_pilot.dto.ReceitaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.exception.ReceitaNotFoundException;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.Parceiro;
import com.gustavolopes.money_pilot.model.ParceiroPF;
import com.gustavolopes.money_pilot.model.Receita;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.model.TipoParceiro;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceitaServiceTest {

    @Mock
    ReceitaRepository repository;

    @Mock
    ParceiroService parceiroService;

    @Mock
    CategoriaService categoriaService;

    @InjectMocks
    ReceitaService receitaService;

    private Endereco criarEndereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private Parceiro criarParceiro(Long id, String nome) {
        ParceiroPF parceiro = new ParceiroPF();
        parceiro.setId(id);
        parceiro.setNome(nome);
        parceiro.setTelefone("11987654321");
        parceiro.setEmail("contato@email.com");
        parceiro.setTipoParceiros(Set.of(TipoParceiro.CLIENTE));
        parceiro.setEndereco(criarEndereco());
        parceiro.setCpf("12345678900");
        return parceiro;
    }

    private Categoria criarCategoria(Long id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setTagCor("#FFFFFF");
        categoria.setTipoCategoria(TipoCategoria.RECEITA);
        return categoria;
    }

    private Receita criarReceita(Long id, Parceiro parceiro, Categoria categoria) {
        Receita receita = new Receita();
        receita.setId(id);
        receita.setNomeReceita("Salário");
        receita.setParceiro(parceiro);
        receita.setDataVencimento(LocalDate.of(2026, 8, 5));
        receita.setNumeroParcelas(1);
        receita.setValorParcela(BigDecimal.valueOf(1000));
        receita.setCategoria(categoria);
        receita.setObservacoes("Pagamento mensal");
        return receita;
    }

    private ReceitaRequestDTO criarRequestDTO(Long parceiroId, Long categoriaId) {
        return new ReceitaRequestDTO(
                "Salário",
                parceiroId,
                LocalDate.of(2026, 8, 5),
                1,
                BigDecimal.valueOf(1000),
                categoriaId,
                "Pagamento mensal"
        );
    }

    @Test
    public void deveRetornarPaginaDeReceitasMapeadas() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Salário");
        Receita receita = criarReceita(1L, parceiro, categoria);
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(receita)));

        Page<ReceitaResponseDTO> resultado = receitaService.getAllReceitas(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nomeReceita()).isEqualTo("Salário");
    }

    @Test
    public void deveRetornarReceitaQuandoIdExiste() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Salário");
        Receita receita = criarReceita(1L, parceiro, categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(receita));

        ReceitaResponseDTO resultado = receitaService.getReceitaById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeReceita()).isEqualTo("Salário");
        assertThat(resultado.parceiro().id()).isEqualTo(1L);
        assertThat(resultado.categoria().id()).isEqualTo(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> receitaService.getReceitaById(idInexistente))
                .isInstanceOf(ReceitaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");
    }

    @Test
    public void deveCriarReceita() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Salário");
        ReceitaRequestDTO dto = criarRequestDTO(1L, 1L);
        Receita receitaSalva = criarReceita(1L, parceiro, categoria);

        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(1L)).thenReturn(categoria);
        when(repository.save(any(Receita.class))).thenReturn(receitaSalva);

        ReceitaResponseDTO resultado = receitaService.createReceita(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeReceita()).isEqualTo("Salário");

        ArgumentCaptor<Receita> captor = ArgumentCaptor.forClass(Receita.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getParceiro()).isEqualTo(parceiro);
        assertThat(captor.getValue().getCategoria()).isEqualTo(categoria);
        assertThat(captor.getValue().getValorParcela()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void deveLancarExcecaoAoCriarReceitaComParceiroInexistente() {
        ReceitaRequestDTO dto = criarRequestDTO(999L, 1L);

        when(parceiroService.buscarParceiroOuLancarExcecao(999L))
                .thenThrow(new ParceiroNotFoundException(999L));

        assertThatThrownBy(() -> receitaService.createReceita(dto))
                .isInstanceOf(ParceiroNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoCriarReceitaComCategoriaInexistente() {
        Parceiro parceiro = criarParceiro(1L, "João");
        ReceitaRequestDTO dto = criarRequestDTO(1L, 999L);

        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(999L))
                .thenThrow(new CategoriaNotFoundException(999L));

        assertThatThrownBy(() -> receitaService.createReceita(dto))
                .isInstanceOf(CategoriaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveAtualizarReceitaExistente() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Salário");
        Receita receitaSalva = criarReceita(1L, parceiro, categoria);
        ReceitaRequestDTO dtoAtualizacao = new ReceitaRequestDTO(
                "Salário atualizado", 1L, LocalDate.of(2026, 9, 5), 2,
                BigDecimal.valueOf(2000), 1L, "Observação atualizada"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(receitaSalva));
        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(1L)).thenReturn(categoria);
        when(repository.save(any(Receita.class))).thenReturn(receitaSalva);

        ReceitaResponseDTO resultado = receitaService.updateReceita(1L, dtoAtualizacao);

        assertThat(resultado.id()).isEqualTo(1L);

        ArgumentCaptor<Receita> captor = ArgumentCaptor.forClass(Receita.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNomeReceita()).isEqualTo("Salário atualizado");
        assertThat(captor.getValue().getNumeroParcelas()).isEqualTo(2);
        assertThat(captor.getValue().getValorParcela()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        ReceitaRequestDTO dto = criarRequestDTO(1L, 1L);

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> receitaService.updateReceita(idInexistente, dto))
                .isInstanceOf(ReceitaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarReceitaQuandoIdExiste() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Salário");
        Receita receita = criarReceita(1L, parceiro, categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(receita));

        receitaService.deleteReceita(1L);

        verify(repository).delete(receita);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> receitaService.deleteReceita(idInexistente))
                .isInstanceOf(ReceitaNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
