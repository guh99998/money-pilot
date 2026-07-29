package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.DespesaRequestDTO;
import com.gustavolopes.money_pilot.dto.DespesaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.exception.DespesaNotFoundException;
import com.gustavolopes.money_pilot.exception.ParceiroNotFoundException;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.model.Despesa;
import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.Parceiro;
import com.gustavolopes.money_pilot.model.ParceiroPF;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.repository.DespesaRepository;
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
public class DespesaServiceTest {

    @Mock
    DespesaRepository repository;

    @Mock
    ParceiroService parceiroService;

    @Mock
    CategoriaService categoriaService;

    @InjectMocks
    DespesaService despesaService;

    private Endereco criarEndereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private Parceiro criarParceiro(Long id, String nome) {
        ParceiroPF parceiro = new ParceiroPF();
        parceiro.setId(id);
        parceiro.setNome(nome);
        parceiro.setTelefone("11987654321");
        parceiro.setEmail("contato@email.com");
        parceiro.setTipoParceiros(Set.of(TipoParceiro.FORNECEDOR));
        parceiro.setEndereco(criarEndereco());
        parceiro.setCpf("12345678900");
        return parceiro;
    }

    private Categoria criarCategoria(Long id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setTagCor("#FFFFFF");
        categoria.setTipoCategoria(TipoCategoria.DESPESA);
        return categoria;
    }

    private Despesa criarDespesa(Long id, Parceiro parceiro, Categoria categoria) {
        Despesa despesa = new Despesa();
        despesa.setId(id);
        despesa.setNomeDespesa("Aluguel");
        despesa.setParceiro(parceiro);
        despesa.setDataVencimento(LocalDate.of(2026, 8, 5));
        despesa.setNumeroParcelas(1);
        despesa.setValorParcela(BigDecimal.valueOf(1000));
        despesa.setCategoria(categoria);
        despesa.setObservacoes("Pagamento mensal");
        return despesa;
    }

    private DespesaRequestDTO criarRequestDTO(Long parceiroId, Long categoriaId) {
        return new DespesaRequestDTO(
                "Aluguel",
                parceiroId,
                LocalDate.of(2026, 8, 5),
                1,
                BigDecimal.valueOf(1000),
                categoriaId,
                "Pagamento mensal"
        );
    }

    @Test
    public void deveRetornarPaginaDeDespesasMapeadas() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Aluguel");
        Despesa despesa = criarDespesa(1L, parceiro, categoria);
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(despesa)));

        Page<DespesaResponseDTO> resultado = despesaService.getAllDespesas(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nomeDespesa()).isEqualTo("Aluguel");
    }

    @Test
    public void deveRetornarDespesaQuandoIdExiste() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Aluguel");
        Despesa despesa = criarDespesa(1L, parceiro, categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(despesa));

        DespesaResponseDTO resultado = despesaService.getDespesaById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeDespesa()).isEqualTo("Aluguel");
        assertThat(resultado.parceiro().id()).isEqualTo(1L);
        assertThat(resultado.categoria().id()).isEqualTo(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despesaService.getDespesaById(idInexistente))
                .isInstanceOf(DespesaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");
    }

    @Test
    public void deveCriarDespesa() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Aluguel");
        DespesaRequestDTO dto = criarRequestDTO(1L, 1L);
        Despesa despesaSalva = criarDespesa(1L, parceiro, categoria);

        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(1L)).thenReturn(categoria);
        when(repository.save(any(Despesa.class))).thenReturn(despesaSalva);

        DespesaResponseDTO resultado = despesaService.createDespesa(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeDespesa()).isEqualTo("Aluguel");

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getParceiro()).isEqualTo(parceiro);
        assertThat(captor.getValue().getCategoria()).isEqualTo(categoria);
        assertThat(captor.getValue().getValorParcela()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void deveLancarExcecaoAoCriarDespesaComParceiroInexistente() {
        DespesaRequestDTO dto = criarRequestDTO(999L, 1L);

        when(parceiroService.buscarParceiroOuLancarExcecao(999L))
                .thenThrow(new ParceiroNotFoundException(999L));

        assertThatThrownBy(() -> despesaService.createDespesa(dto))
                .isInstanceOf(ParceiroNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoCriarDespesaComCategoriaInexistente() {
        Parceiro parceiro = criarParceiro(1L, "João");
        DespesaRequestDTO dto = criarRequestDTO(1L, 999L);

        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(999L))
                .thenThrow(new CategoriaNotFoundException(999L));

        assertThatThrownBy(() -> despesaService.createDespesa(dto))
                .isInstanceOf(CategoriaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveAtualizarDespesaExistente() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Aluguel");
        Despesa despesaSalva = criarDespesa(1L, parceiro, categoria);
        DespesaRequestDTO dtoAtualizacao = new DespesaRequestDTO(
                "Aluguel atualizado", 1L, LocalDate.of(2026, 9, 5), 2,
                BigDecimal.valueOf(2000), 1L, "Observação atualizada"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(despesaSalva));
        when(parceiroService.buscarParceiroOuLancarExcecao(1L)).thenReturn(parceiro);
        when(categoriaService.buscarCategoriaOuLancarExcecao(1L)).thenReturn(categoria);
        when(repository.save(any(Despesa.class))).thenReturn(despesaSalva);

        DespesaResponseDTO resultado = despesaService.updateDespesa(1L, dtoAtualizacao);

        assertThat(resultado.id()).isEqualTo(1L);

        ArgumentCaptor<Despesa> captor = ArgumentCaptor.forClass(Despesa.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNomeDespesa()).isEqualTo("Aluguel atualizado");
        assertThat(captor.getValue().getNumeroParcelas()).isEqualTo(2);
        assertThat(captor.getValue().getValorParcela()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        DespesaRequestDTO dto = criarRequestDTO(1L, 1L);

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despesaService.updateDespesa(idInexistente, dto))
                .isInstanceOf(DespesaNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarDespesaQuandoIdExiste() {
        Parceiro parceiro = criarParceiro(1L, "João");
        Categoria categoria = criarCategoria(1L, "Aluguel");
        Despesa despesa = criarDespesa(1L, parceiro, categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(despesa));

        despesaService.deleteDespesa(1L);

        verify(repository).delete(despesa);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despesaService.deleteDespesa(idInexistente))
                .isInstanceOf(DespesaNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}
