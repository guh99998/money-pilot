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
import com.gustavolopes.money_pilot.model.TipoParceiro;
import com.gustavolopes.money_pilot.repository.ParceiroRepository;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParceiroServiceTest {

    @Mock
    ParceiroRepository repository;

    @InjectMocks
    ParceiroService parceiroService;

    private Endereco criarEndereco() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    private ParceiroPF criarParceiroPF(Long id, String nome, String cpf) {
        ParceiroPF pf = new ParceiroPF();
        pf.setId(id);
        pf.setNome(nome);
        pf.setTelefone("11987654321");
        pf.setEmail("contato@email.com");
        pf.setTipoParceiros(Set.of(TipoParceiro.CLIENTE));
        pf.setEndereco(criarEndereco());
        pf.setCpf(cpf);
        return pf;
    }

    private ParceiroPJ criarParceiroPJ(Long id, String nome, String cnpj) {
        ParceiroPJ pj = new ParceiroPJ();
        pj.setId(id);
        pj.setNome(nome);
        pj.setTelefone("11987654321");
        pj.setEmail("contato@email.com");
        pj.setTipoParceiros(Set.of(TipoParceiro.FORNECEDOR));
        pj.setEndereco(criarEndereco());
        pj.setCnpj(cnpj);
        return pj;
    }

    private ParceiroRequestDTO criarRequestDTOPF(String nome, String cpf) {
        return new ParceiroRequestDTO(
                nome,
                "11987654321",
                "contato@email.com",
                Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF,
                cpf,
                null,
                criarEndereco()
        );
    }

    private ParceiroRequestDTO criarRequestDTOPJ(String nome, String cnpj) {
        return new ParceiroRequestDTO(
                nome,
                "11987654321",
                "contato@email.com",
                Set.of(TipoParceiro.FORNECEDOR),
                TipoDocumento.CNPJ,
                null,
                cnpj,
                criarEndereco()
        );
    }

    @Test
    public void deveRetornarPaginaDeParceirosMapeados() {
        ParceiroPF parceiro = criarParceiroPF(1L, "João", "12345678900");
        Pageable pageable = Pageable.unpaged();
        Page<Parceiro> paginaFalsa = new PageImpl<>(List.of(parceiro));

        when(repository.findAll(pageable)).thenReturn(paginaFalsa);

        Page<ParceiroResponseDTO> resultado = parceiroService.getAllParceiros(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("João");
        assertThat(resultado.getContent().get(0).cpf()).isEqualTo("12345678900");
    }

    @Test
    public void deveRetornarParceiroPFQuandoIdExiste() {
        ParceiroPF parceiro = criarParceiroPF(1L, "João", "12345678900");

        when(repository.findById(1L)).thenReturn(Optional.of(parceiro));

        ParceiroResponseDTO resultado = parceiroService.getParceiroById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.tipoDocumento()).isEqualTo(TipoDocumento.CPF);
        assertThat(resultado.cpf()).isEqualTo("12345678900");
        assertThat(resultado.cnpj()).isNull();
    }

    @Test
    public void deveRetornarParceiroPJQuandoIdExiste() {
        ParceiroPJ parceiro = criarParceiroPJ(2L, "Empresa X", "12345678000199");

        when(repository.findById(2L)).thenReturn(Optional.of(parceiro));

        ParceiroResponseDTO resultado = parceiroService.getParceiroById(2L);

        assertThat(resultado.tipoDocumento()).isEqualTo(TipoDocumento.CNPJ);
        assertThat(resultado.cnpj()).isEqualTo("12345678000199");
        assertThat(resultado.cpf()).isNull();
    }

    @Test
    public void deveLancarExcecaoQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parceiroService.getParceiroById(idInexistente))
                .isInstanceOf(ParceiroNotFoundException.class)
                .hasMessageContaining("não foi encontrado");
    }

    @Test
    public void deveCriarParceiroPF() {
        ParceiroRequestDTO dto = criarRequestDTOPF("João", "12345678900");
        ParceiroPF parceiroSalvo = criarParceiroPF(1L, "João", "12345678900");

        when(repository.save(any(Parceiro.class))).thenReturn(parceiroSalvo);

        ParceiroResponseDTO resultado = parceiroService.createParceiro(dto);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.tipoDocumento()).isEqualTo(TipoDocumento.CPF);
        assertThat(resultado.cpf()).isEqualTo("12345678900");

        ArgumentCaptor<Parceiro> captor = ArgumentCaptor.forClass(Parceiro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(ParceiroPF.class);
        assertThat(((ParceiroPF) captor.getValue()).getCpf()).isEqualTo("12345678900");
    }

    @Test
    public void deveCriarParceiroPJ() {
        ParceiroRequestDTO dto = criarRequestDTOPJ("Empresa X", "12345678000199");
        ParceiroPJ parceiroSalvo = criarParceiroPJ(2L, "Empresa X", "12345678000199");

        when(repository.save(any(Parceiro.class))).thenReturn(parceiroSalvo);

        ParceiroResponseDTO resultado = parceiroService.createParceiro(dto);

        assertThat(resultado.tipoDocumento()).isEqualTo(TipoDocumento.CNPJ);
        assertThat(resultado.cnpj()).isEqualTo("12345678000199");

        ArgumentCaptor<Parceiro> captor = ArgumentCaptor.forClass(Parceiro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(ParceiroPJ.class);
    }

    @Test
    public void deveLancarExcecaoAoCriarParceiroPFSemCpf() {
        ParceiroRequestDTO dto = criarRequestDTOPF("João", null);

        assertThatThrownBy(() -> parceiroService.createParceiro(dto))
                .isInstanceOf(DocumentoInvalidoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoCriarParceiroPJSemCnpj() {
        ParceiroRequestDTO dto = criarRequestDTOPJ("Empresa X", "");

        assertThatThrownBy(() -> parceiroService.createParceiro(dto))
                .isInstanceOf(DocumentoInvalidoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveAtualizarParceiroPFMantendoDocumento() {
        ParceiroPF parceiroSalvo = criarParceiroPF(1L, "João", "12345678900");
        ParceiroRequestDTO dtoAtualizacao = new ParceiroRequestDTO(
                "João Silva",
                "11999998888",
                "joaosilva@email.com",
                Set.of(TipoParceiro.CLIENTE, TipoParceiro.FORNECEDOR),
                TipoDocumento.CPF,
                "12345678900",
                null,
                criarEndereco()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(parceiroSalvo));
        when(repository.save(any(Parceiro.class))).thenReturn(parceiroSalvo);

        ParceiroResponseDTO resultado = parceiroService.updateParceiro(1L, dtoAtualizacao);

        assertThat(resultado.id()).isEqualTo(1L);

        ArgumentCaptor<Parceiro> captor = ArgumentCaptor.forClass(Parceiro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("João Silva");
        assertThat(captor.getValue().getTelefone()).isEqualTo("11999998888");
    }

    @Test
    public void deveLancarExcecaoAoAtualizarComTipoDocumentoDivergente() {
        ParceiroPF parceiroSalvo = criarParceiroPF(1L, "João", "12345678900");
        ParceiroRequestDTO dtoAtualizacao = criarRequestDTOPJ("João", "12345678000199");

        when(repository.findById(1L)).thenReturn(Optional.of(parceiroSalvo));

        assertThatThrownBy(() -> parceiroService.updateParceiro(1L, dtoAtualizacao))
                .isInstanceOf(DocumentoImutavelException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarComCpfDivergente() {
        ParceiroPF parceiroSalvo = criarParceiroPF(1L, "João", "12345678900");
        ParceiroRequestDTO dtoAtualizacao = criarRequestDTOPF("João", "00000000000");

        when(repository.findById(1L)).thenReturn(Optional.of(parceiroSalvo));

        assertThatThrownBy(() -> parceiroService.updateParceiro(1L, dtoAtualizacao))
                .isInstanceOf(DocumentoImutavelException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarQuandoIdNaoExiste() {
        Long idInexistente = 999L;
        ParceiroRequestDTO dto = criarRequestDTOPF("João", "12345678900");

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parceiroService.updateParceiro(idInexistente, dto))
                .isInstanceOf(ParceiroNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void deveDeletarParceiroQuandoIdExiste() {
        ParceiroPF parceiro = criarParceiroPF(1L, "João", "12345678900");

        when(repository.findById(1L)).thenReturn(Optional.of(parceiro));

        parceiroService.deleteParceiro(1L);

        verify(repository).delete(parceiro);
    }

    @Test
    public void deveLancarExcecaoAoDeletarQuandoIdNaoExiste() {
        Long idInexistente = 999L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parceiroService.deleteParceiro(idInexistente))
                .isInstanceOf(ParceiroNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    public void deveIgnorarIdDeEnderecoInformadoAoCriarParceiro() {
        Endereco enderecoComIdDeOutroRegistro = new Endereco(999L, "Rua Hijack", "1", "Bairro", "00000-000", "Cidade", "SP");
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "contato@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoComIdDeOutroRegistro
        );

        when(repository.save(any(Parceiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        parceiroService.createParceiro(dto);

        ArgumentCaptor<Parceiro> captor = ArgumentCaptor.forClass(Parceiro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEndereco().getId()).isNull();
        assertThat(captor.getValue().getEndereco().getRua()).isEqualTo("Rua Hijack");
    }

    @Test
    public void deveManterIdDoEnderecoOriginalAoAtualizarIgnorandoIdInformado() {
        ParceiroPF parceiroSalvo = criarParceiroPF(1L, "João", "12345678900");
        Endereco enderecoComIdDivergente = new Endereco(999L, "Rua Nova", "2", "Bairro Novo", "11111-000", "Nova Cidade", "RJ");
        ParceiroRequestDTO dtoAtualizacao = new ParceiroRequestDTO(
                "João", "11987654321", "contato@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoComIdDivergente
        );

        when(repository.findById(1L)).thenReturn(Optional.of(parceiroSalvo));
        when(repository.save(any(Parceiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        parceiroService.updateParceiro(1L, dtoAtualizacao);

        ArgumentCaptor<Parceiro> captor = ArgumentCaptor.forClass(Parceiro.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEndereco().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getEndereco().getRua()).isEqualTo("Rua Nova");
    }

    @Test
    public void deveLancarExcecaoQuandoParceiroEstaEmUso() {
        ParceiroPF parceiro = criarParceiroPF(1L, "João", "12345678900");

        when(repository.findById(1L)).thenReturn(Optional.of(parceiro));
        doThrow(new DataIntegrityViolationException("Violação de FK"))
                .when(repository).delete(parceiro);

        assertThatThrownBy(() -> parceiroService.deleteParceiro(1L))
                .isInstanceOf(ParceiroEmUsoException.class)
                .hasMessageContaining("não pode ser removido");
    }
}
