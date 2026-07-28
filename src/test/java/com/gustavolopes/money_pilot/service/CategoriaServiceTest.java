package com.gustavolopes.money_pilot.service;

import com.gustavolopes.money_pilot.dto.CategoriaRequestDTO;
import com.gustavolopes.money_pilot.dto.CategoriaResponseDTO;
import com.gustavolopes.money_pilot.exception.CategoriaEmUsoException;
import com.gustavolopes.money_pilot.exception.CategoriaNotFoundException;
import com.gustavolopes.money_pilot.model.Categoria;
import com.gustavolopes.money_pilot.model.TipoCategoria;
import com.gustavolopes.money_pilot.repository.CategoriaRepository;
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
public class CategoriaServiceTest {
    @Mock
    CategoriaRepository repository;

    @InjectMocks
    CategoriaService categoriaService;

    @Test
    public void deveRetornarPaginaDeCategoriasMapeadas() {
        Categoria categoria = new Categoria(1L, "Salário", "#00FF00", TipoCategoria.RECEITA);
        Pageable pageable = Pageable.unpaged();
        Page<Categoria> paginaFalsa = new PageImpl<>(List.of(categoria));

        when(repository.findAll(pageable)).thenReturn(paginaFalsa);

        Page<CategoriaResponseDTO> resultado = categoriaService.getAllCategorias(pageable);

        assertThat(resultado.getContent()).hasSize(1);

        CategoriaResponseDTO dto = resultado.getContent().get(0);
        assertThat(dto.id()).isEqualTo(categoria.getId());
        assertThat(dto.nome()).isEqualTo(categoria.getNome());
        assertThat(dto.tagCor()).isEqualTo(categoria.getTagCor());
        assertThat(dto.tipoCategoria()).isEqualTo(categoria.getTipoCategoria());
    }

    @Test
    public void deveRetornarCategoriaQuandoIdExiste() {
        Categoria categoria = new Categoria(1L, "Salário", "#00FF00", TipoCategoria.RECEITA);

        when(repository.findById(categoria.getId())).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO resultado = categoriaService.getCategoriaById(categoria.getId());

        assertThat(resultado.id()).isEqualTo(categoria.getId());
        assertThat(resultado.nome()).isEqualTo(categoria.getNome());
        assertThat(resultado.tagCor()).isEqualTo(categoria.getTagCor());
        assertThat(resultado.tipoCategoria()).isEqualTo(categoria.getTipoCategoria());

    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.getCategoriaById(idInexistente)).isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");
    }

    @Test
    public void deveCriarNovaCategoria() {
        CategoriaRequestDTO categoria = new CategoriaRequestDTO("Alimentação", "00FF01", TipoCategoria.DESPESA);
        Categoria categoriaSalva = new Categoria(1L, "Alimentação", "#00FF01", TipoCategoria.DESPESA);

        when(repository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        CategoriaResponseDTO resultado = categoriaService.createCategoria(categoria);

        assertThat(resultado.id()).isEqualTo(categoriaSalva.getId());
        assertThat(resultado.nome()).isEqualTo(categoriaSalva.getNome());
        assertThat(resultado.tagCor()).isEqualTo(categoriaSalva.getTagCor());
        assertThat(resultado.tipoCategoria()).isEqualTo(categoriaSalva.getTipoCategoria());
    }

    @Test
    public void deveAtualizarUmaCategoriaSeIdExiste() {
        Categoria categoriaSalva = new Categoria(3L,"Saúde", "00FF01", TipoCategoria.DESPESA);
        CategoriaRequestDTO categoriaASalvar = new CategoriaRequestDTO("Alimentação", "#00FF01", TipoCategoria.DESPESA);
        Categoria categoriaAtualizada = new Categoria(3L, "Alimentação", "#00FF01", TipoCategoria.DESPESA);

        when(repository.findById(categoriaSalva.getId())).thenReturn(Optional.of(categoriaSalva));
        when(repository.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

        CategoriaResponseDTO resultado = categoriaService.updateCategoria(3L, categoriaASalvar);

        assertThat(resultado.id()).isEqualTo(categoriaAtualizada.getId());

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(repository).save(captor.capture());
        Categoria categoriaCapturada = captor.getValue();

        assertThat(categoriaCapturada.getNome()).isEqualTo(categoriaASalvar.nome());
        assertThat(categoriaCapturada.getTagCor()).isEqualTo(categoriaASalvar.tagCor());
        assertThat(categoriaCapturada.getTipoCategoria()).isEqualTo(categoriaASalvar.tipoCategoria());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        CategoriaRequestDTO categoriaASalvar = new CategoriaRequestDTO("Lazer", "#0000FF", TipoCategoria.DESPESA);

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.updateCategoria(idInexistente, categoriaASalvar))
                .isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarCategoriaQuandoIdExiste() {
        Categoria categoria = new Categoria(1L, "Lazer", "#0000FF", TipoCategoria.DESPESA);

        when(repository.findById(categoria.getId())).thenReturn(Optional.of(categoria));

        categoriaService.deleteCategoria(categoria.getId());

        verify(repository).delete(categoria);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.deleteCategoria(idInexistente))
                .isInstanceOf(CategoriaNotFoundException.class)
                .hasMessageContaining("não foi encontrada");

        verify(repository, never()).delete(any());
    }

    @Test
    public void deveLancarExcecaoQuandoCategoriaEstaEmUso() {
        Categoria categoria = new Categoria(1L, "Lazer", "#0000FF", TipoCategoria.DESPESA);

        when(repository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        doThrow(new DataIntegrityViolationException("Violação de FK"))
                .when(repository).delete(categoria);

        assertThatThrownBy(() -> categoriaService.deleteCategoria(categoria.getId()))
                .isInstanceOf(CategoriaEmUsoException.class)
                .hasMessageContaining("não pode ser removida");
    }
}
