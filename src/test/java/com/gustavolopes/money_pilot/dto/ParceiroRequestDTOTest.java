package com.gustavolopes.money_pilot.dto;

import com.gustavolopes.money_pilot.model.Endereco;
import com.gustavolopes.money_pilot.model.TipoDocumento;
import com.gustavolopes.money_pilot.model.TipoParceiro;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParceiroRequestDTOTest {

    static ValidatorFactory factory;
    static Validator validator;

    @BeforeAll
    public static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void tearDown() {
        factory.close();
    }

    private Endereco enderecoValido() {
        return new Endereco(1L, "Rua das Flores", "100", "Centro", "12345-000", "São Paulo", "SP");
    }

    @Test
    public void naoDeveGerarViolacoesQuandoTodosOsCamposSaoValidos() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoNomeEstaEmBranco() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O nome do Parceiro é obrigatório");
    }

    @Test
    public void naoDeveGerarViolacaoQuandoTelefoneEstaNulo() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", null, "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoTelefoneTemFormatoInvalido() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "telefone-invalido", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O telefone deve ter um formato válido (ex: 11987654321 ou 5511987654321)");
    }

    @Test
    public void naoDeveGerarViolacaoQuandoEmailEstaNulo() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", null, Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes).isEmpty();
    }

    @Test
    public void deveGerarViolacaoQuandoTipoParceirosEstaVazio() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(),
                TipoDocumento.CPF, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("Informe ao menos um tipo de parceiro");
    }

    @Test
    public void deveGerarViolacaoQuandoTipoDocumentoEhNulo() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                null, "12345678900", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O tipo de documento é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoEnderecoEhNulo() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, null
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O endereço é obrigatório");
    }

    @Test
    public void deveGerarViolacaoQuandoCpfTemFormatoInvalido() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "123", null, enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O cpf deve conter 11 dígitos numéricos");
    }

    @Test
    public void deveGerarViolacaoQuandoCnpjTemFormatoInvalido() {
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "Empresa X", "11987654321", "contato@email.com", Set.of(TipoParceiro.FORNECEDOR),
                TipoDocumento.CNPJ, null, "abc123", enderecoValido()
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(ConstraintViolation::getMessage)
                .contains("O cnpj deve conter 14 dígitos numéricos");
    }

    @Test
    public void deveGerarViolacaoQuandoEnderecoTemCampoObrigatorioEmBranco() {
        Endereco enderecoInvalido = new Endereco(1L, "Rua das Flores", "100", "Centro", "", "São Paulo", "SP");
        ParceiroRequestDTO dto = new ParceiroRequestDTO(
                "João", "11987654321", "joao@email.com", Set.of(TipoParceiro.CLIENTE),
                TipoDocumento.CPF, "12345678900", null, enderecoInvalido
        );

        Set<ConstraintViolation<ParceiroRequestDTO>> violacoes = validator.validate(dto);

        assertThat(violacoes)
                .extracting(violacao -> violacao.getPropertyPath().toString())
                .anyMatch(caminho -> caminho.contains("endereco"));
    }
}
